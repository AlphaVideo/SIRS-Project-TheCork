package com.sirs.thecork.db;

import java.nio.charset.StandardCharsets;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import org.json.JSONObject;

import com.sirs.thecork.common.JsonToolkit;
import com.sirs.thecork.common.TokenManager;
import com.sirs.thecork.common.Vault;

public class CustomerCommander {

    Connection _connection;
    TokenManager _tokenManager;
    Vault _vault;

    public CustomerCommander() {
        _connection = DatabaseConnection.getConnection();
        _tokenManager = new TokenManager(_connection, false);
        _vault = new Vault();
	}

    public String loginCustomer(String user, String pass) {
        PreparedStatement stmt;
        ResultSet res = null;
        String password = pass;

        try {
            //First we must find if user exists
            stmt = _connection.prepareStatement("SELECT * FROM client WHERE username = ?;");
            stmt.setString(1, user);
            res = stmt.executeQuery();

            //Check if result set isn't empty
            if(!res.isBeforeFirst()) {
                //Empty
                return JsonToolkit.generateStatus("ERROR", "Wrong user or password").toString();
            }
            else {
                //Obtain pass salt
                res.next();
                String salt = res.getString("pass_salt");
                password = password.concat(salt);
            }

            //Hash = Sha256(password+salt)
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] encodedHash = md.digest(password.getBytes(StandardCharsets.UTF_8));

            //Password hash must be decoded to hex for comparison with DB
            StringBuilder pass_hash = new StringBuilder(2*encodedHash.length);
            for (int i = 0; i < encodedHash.length; i++) {
                String hex = Integer.toHexString(0xff&encodedHash[i]);
                if(hex.length() == 1)
                    pass_hash.append('0');

                pass_hash.append(hex);
            }

            String dbPassHash = res.getString("pass_hash");

            //Wrong Password
            if(!pass_hash.toString().equals(dbPassHash)) {
                return JsonToolkit.generateStatus("ERROR", "Wrong user or password").toString();
            }

        } catch (SQLException | NoSuchAlgorithmException e) {
            e.printStackTrace();
            return JsonToolkit.generateStatus("ERROR", "Unknown SQL error").toString();
        }

        //Everything succeeded -> generate new auth token
        String token = _tokenManager.generateToken(user);

        return JsonToolkit.generateStatus("OK", "auth_token", token).toString();
	}

    public String reservation(String auth_token, String restaurant, int nPeople, String datetime) {
		PreparedStatement stmt;
		ResultSet res = null;

        String user = _tokenManager.validateToken(auth_token);
        if (user == null)
            return JsonToolkit.generateStatus("ERROR", "INVALID_AUTH_TOKEN").toString();

		try {
            //We can assume the user exists because he already went through the login process
            //First we must find if restaurant exists
			stmt = _connection.prepareStatement("SELECT * FROM restaurant WHERE name = ?;");
            stmt.setString(1, restaurant);
			res = stmt.executeQuery();

            //Check if result set isn't empty
            if(!res.isBeforeFirst()) {
                //Empty
                return JsonToolkit.generateStatus("ERROR", "Restaurant non-existent").toString();
            }

            //Add reservation to database
            else {
                stmt = _connection.prepareStatement("INSERT INTO reservation VALUES (?, ?, ?, ?);");
                stmt.setString(1, user);
                stmt.setString(2, restaurant);
                stmt.setString(3, datetime);
                stmt.setInt(4, nPeople);
                stmt.executeUpdate();
            }

		} catch (SQLException e) {
			// add info to logger
			e.printStackTrace();
            return JsonToolkit.generateStatus("ERROR", "Unknown SQL error").toString();
		}

        //Everything succeeded
        return JsonToolkit.generateStatus("OK").toString();
	}

    public String buy_giftcard(String auth_token, int value) {
		PreparedStatement stmt;
		int count = 0;
        ResultSet res = null;

        String user = _tokenManager.validateToken(auth_token);
        if (user == null)
            return JsonToolkit.generateStatus("ERROR", "INVALID_AUTH_TOKEN").toString();
		try {

            //Find if there is an eligible gift card
            stmt = _connection.prepareStatement("SELECT * FROM giftcard WHERE value = ? and owner is NULL LIMIT 1;");
            stmt.setString(1, Integer.toString(value));
            res = stmt.executeQuery();

            //Check if result set isn't empty
            if(!res.isBeforeFirst()) {
                //Empty
                return JsonToolkit.generateStatus("ERROR", "No available gift card for the given value.").toString();
            }

            res.next();
            int id = res.getInt("id");

            String user_enc = _vault.giftcardEncipher(id, user);
            String value_enc = _vault.giftcardEncipher(id, Integer.toString(value));

            //Activate a giftcard - first giftcard with the value wanted that has no owner
            stmt = _connection.prepareStatement("UPDATE giftcard SET owner = ?, value = ? WHERE id = ?;");
            stmt.setString(1, user_enc);
            stmt.setString(2, value_enc);
            stmt.setInt(3, id);
            count = stmt.executeUpdate();

            //If update was successful
            if(count == 1) {

                //Restock DB
                create_giftcard(value);

                //Obtain purchased gift card's information for user
                JSONObject json = new JSONObject();
                json.put("status", "OK");
                json.put("value", value);
                json.put("card_id", res.getInt("id"));
                json.put("card_code", res.getString("nonce"));

                return json.toString();
            }
            else {
                return JsonToolkit.generateStatus("ERROR", "Couldn't purchase giftcard").toString();
            }


		} catch (SQLException | NumberFormatException | InvalidKeyException | NoSuchPaddingException | NoSuchAlgorithmException | InvalidAlgorithmParameterException | BadPaddingException | IllegalBlockSizeException e) {
			// add info to logger
			e.printStackTrace();
            return JsonToolkit.generateStatus("ERROR", "Unknown SQL error").toString();
		}
	}

    public String redeem_giftcard(String auth_token, int id, String nonce) {
		PreparedStatement stmt;
		ResultSet res = null;
		String valueEnc;
		int value;

        String user = _tokenManager.validateToken(auth_token);
        if (user == null)
            return JsonToolkit.generateStatus("ERROR", "INVALID_AUTH_TOKEN").toString();

		try {
            //We can assume the user exists because he already went through the login process
            //First we must find if giftcard exists and it belongs to the user
			stmt = _connection.prepareStatement("SELECT * FROM giftcard WHERE id = ?;");
            stmt.setInt(1, id);
			res = stmt.executeQuery();

            String check = checkGiftCard(res, id, user, nonce);
            if (check != null)
                return check;

            valueEnc = res.getString("value");
            value = Integer.parseInt(_vault.giftcardDecipher(id, valueEnc));
            
            stmt = _connection.prepareStatement("DELETE FROM giftcard_ivs WHERE id = ?;");
            stmt.setInt(1, id);
            stmt.executeUpdate();
            stmt = _connection.prepareStatement("DELETE FROM giftcard WHERE id = ?;");
            stmt.setInt(1, id);
            stmt.executeUpdate();
            
            incrementWallet(user, value);

		} catch (SQLException e) {
			e.printStackTrace();
            return JsonToolkit.generateStatus("ERROR", "Unknown SQL error").toString();
		}
		catch (NumberFormatException | InvalidKeyException | NoSuchPaddingException | NoSuchAlgorithmException | InvalidAlgorithmParameterException | BadPaddingException | IllegalBlockSizeException e) {
			e.printStackTrace();
            return JsonToolkit.generateStatus("ERROR", "Unknown encryption error").toString();
		}

        return JsonToolkit.generateStatus("OK").toString();
	}
    
    private void incrementWallet(String user, int val) throws SQLException, NumberFormatException, InvalidKeyException, NoSuchPaddingException, NoSuchAlgorithmException, InvalidAlgorithmParameterException, BadPaddingException, IllegalBlockSizeException {
		PreparedStatement stmt;
		ResultSet res;
		String walletEnc;
		int wallet;

		stmt = _connection.prepareStatement("SELECT wallet FROM client WHERE username = ?;");
		stmt.setString(1, user);
		res = stmt.executeQuery();
		
		res.next();
		walletEnc = res.getString("wallet");
		
		wallet = Integer.parseInt(_vault.clientDecipher(user, walletEnc));
		wallet += val;

		stmt = _connection.prepareStatement("UPDATE client SET wallet = ? WHERE username = ?;");
		stmt.setString(1, _vault.clientEncipher(user, String.format("%03d", wallet)));
		stmt.setString(2,  user);
		stmt.executeUpdate();
    }

    public String give_giftcard(String auth_token, String target, int id, String nonce) {
		PreparedStatement stmt;
		ResultSet res = null;

        String user = _tokenManager.validateToken(auth_token);
        if (user == null)
            return JsonToolkit.generateStatus("ERROR", "INVALID_AUTH_TOKEN").toString();

		try {
            //We can assume the user exists because he already went through the login process
            //First we must find if giftcard exists and it belongs to the user
			stmt = _connection.prepareStatement("SELECT * FROM giftcard WHERE id = ?;");
            stmt.setInt(1, id);
			res = stmt.executeQuery();

            String check = checkGiftCard(res, id, user, nonce);
            if (check != null)
                return check;

            //Now we must find if target exists
            stmt = _connection.prepareStatement("SELECT * FROM client WHERE username = ?;");
            stmt.setString(1, target);
            res = stmt.executeQuery();

            //Check if result set isn't empty
            if(!res.isBeforeFirst()) {
                //Empty
                return JsonToolkit.generateStatus("ERROR", "Target user non-existent").toString();
            }
            //Change Ownership
            stmt = _connection.prepareStatement("UPDATE giftcard SET owner = ? WHERE id = ?;");
            stmt.setString(1, _vault.giftcardEncipher(id, target));
            stmt.setInt(2, id);
            stmt.executeUpdate();

        } catch (SQLException  e) {
			e.printStackTrace();
            return JsonToolkit.generateStatus("ERROR", "Unknown SQL error").toString();
		}
		catch (NumberFormatException | InvalidKeyException | NoSuchPaddingException | NoSuchAlgorithmException | InvalidAlgorithmParameterException | BadPaddingException | IllegalBlockSizeException e) {
			e.printStackTrace();
			return JsonToolkit.generateStatus("ERROR", "Unknown encryption error").toString();
		}

        //Everything succeeded
        return JsonToolkit.generateStatus("OK").toString();
	}

    public String create_giftcard(int value) {
		PreparedStatement stmt;

		try {
            //Generate a secure random number
            SecureRandom nonceGen = new SecureRandom();
            byte nonceBytes[] = new byte[16]; //Equivalent to 32 hex chars
            nonceGen.nextBytes(nonceBytes); // Stores random bytes in nonce byte array

            //Convert nonce bytes to hex string to store in DB
            StringBuilder tokenString = new StringBuilder(2*nonceBytes.length);
            for (int i = 0; i < nonceBytes.length; i++) {
                String hex = Integer.toHexString(0xff&nonceBytes[i]);
                if(hex.length() == 1)
                    tokenString.append('0');

                tokenString.append(hex);
            }

			stmt = _connection.prepareStatement("INSERT INTO giftcard VALUES (0, ?, NULL, ?);");
            stmt.setString(1, tokenString.toString());
            stmt.setString(2, String.format("%03d", value));
			stmt.executeUpdate();


		} catch (SQLException e) {
			// add info to logger
			e.printStackTrace();
            return JsonToolkit.generateStatus("ERROR", "Unknown SQL error").toString();
		}

        //Everything succeeded
        return JsonToolkit.generateStatus("OK").toString();
	}

    public String check_balance(String auth_token) {
		PreparedStatement stmt;
		ResultSet res = null;
        String balanceEnc, balanceDec;

        String user = _tokenManager.validateToken(auth_token);
        if (user == null)
            return JsonToolkit.generateStatus("ERROR", "INVALID_AUTH_TOKEN").toString();

		try {
            //We can assume the user exists because he already went through the login process
            //Check Balance
			stmt = _connection.prepareStatement("SELECT * FROM client WHERE username = ?;");
            stmt.setString(1, user);
			res = stmt.executeQuery();

            //Check if result set isn't empty
            if(!res.isBeforeFirst())
                return JsonToolkit.generateStatus("ERROR", "SQL Query returned no results").toString();
            else
                res.next();

            balanceEnc = res.getString("wallet");
			balanceDec = _vault.clientDecipher(user, balanceEnc);

		} catch (SQLException e) {
			e.printStackTrace();
			return JsonToolkit.generateStatus("ERROR", "Unknown SQL error").toString();
		}
		catch (NumberFormatException | InvalidKeyException | NoSuchPaddingException | NoSuchAlgorithmException | InvalidAlgorithmParameterException | BadPaddingException | IllegalBlockSizeException e) {
			e.printStackTrace();
			return JsonToolkit.generateStatus("ERROR", "Unknown encryption error").toString();
		}
		
		return JsonToolkit.generateStatus("OK", "balance", Integer.parseInt(balanceDec)).toString();
	}

    private String checkGiftCard(ResultSet res, int id, String owner, String nonce) throws SQLException, NumberFormatException, InvalidKeyException, NoSuchPaddingException, NoSuchAlgorithmException, InvalidAlgorithmParameterException, BadPaddingException, IllegalBlockSizeException{
    	String realOwner;
    	
    	 if(!res.isBeforeFirst())
             return JsonToolkit.generateStatus("ERROR", "Giftcard non-existent").toString();
    	
    	res.next();
    	realOwner = _vault.giftcardDecipher(id, res.getString("owner"));
    	
        if (!realOwner.equals(owner))
            return JsonToolkit.generateStatus("ERROR", "Not the owner").toString();
        if (!res.getObject("nonce").equals(nonce))
            return JsonToolkit.generateStatus("ERROR", "Invalid nonce").toString();

        return null;
    }
}
