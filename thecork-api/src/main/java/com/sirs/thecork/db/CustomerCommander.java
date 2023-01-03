package com.sirs.thecork.db;

import java.sql.Timestamp;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.nio.charset.StandardCharsets;

import org.json.JSONArray;
import org.json.JSONObject;

public class CustomerCommander {

    Connection _connection = null;
    TokenManager _tokenManager = null;

    public CustomerCommander() {
        _connection = DatabaseConnection.getConnection();
        _tokenManager = new TokenManager(_connection, false);
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
                return JsonToolkit.generateStatus("ERROR", "User non-existent").toString();
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

        } catch (SQLException e) {
			// add info to logger
			e.printStackTrace();
		} catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
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
		}

        //Everything succeeded
        return JsonToolkit.generateStatus("OK").toString();
	}

    public String buy_giftcard(String auth_token, int value) {
		PreparedStatement stmt;
		ResultSet res = null;

        String user = _tokenManager.validateToken(auth_token);
        if (user == null)
            return JsonToolkit.generateStatus("ERROR", "INVALID_AUTH_TOKEN").toString();

		try {
            //We can assume the user exists because he already went through the login process

            //Activate a giftcard
            //TODO: check if sql statement works and return the id and nonce to the customer
            stmt = _connection.prepareStatement("UPDATE TOP (1) giftcards SET owner = ? WHERE owner = NULL and value = ?;");
            stmt.setString(1, user);
            stmt.setInt(2, value);
            stmt.executeUpdate();

            //create new giftcard
            create_giftcard(value);


		} catch (SQLException e) {
			// add info to logger
			e.printStackTrace();
		}

        //Everything succeeded
        return JsonToolkit.generateStatus("ERROR", "Not implemented").toString();
	}

    public String redeem_giftcard(String auth_token, int id, String nonce) {
		PreparedStatement stmt;
		ResultSet res = null;

        String user = _tokenManager.validateToken(auth_token);
        if (user == null)
            return JsonToolkit.generateStatus("ERROR", "INVALID_AUTH_TOKEN").toString();

		try {
            //We can assume the user exists because he already went through the login process
            //First we must find if giftcard exists and it belongs to the user
			stmt = _connection.prepareStatement("SELECT * FROM giftcards WHERE id = ? and nonce = ? and owner = ?;");
            stmt.setInt(1, id);
            stmt.setString(2, nonce);
            stmt.setString(3, user);
			res = stmt.executeQuery();

            //Check if result set isn't empty
            if(!res.isBeforeFirst()) {
                //Empty
                return JsonToolkit.generateStatus("ERROR", "Giftcard non-existent for given user").toString();
            }

            //Redeem giftcard
            else {

                //Remove Giftcard From Database
                int value = res.getInt("value");

                stmt = _connection.prepareStatement("DELETE FROM giftcards WHERE id = ? and nonce = ? and owner = ?;");
                stmt.setInt(1, id);
                stmt.setString(2, nonce);
                stmt.setString(3, user);
                stmt.executeUpdate();

                //Check how much money the user currently has
			    stmt = _connection.prepareStatement("SELECT * FROM client WHERE username = ?;");
                stmt.setString(1, user);
                res = stmt.executeQuery();

                int wallet = res.getInt("wallet");

                //Add the value of the Giftcard
                wallet += value;

                stmt = _connection.prepareStatement("Update client SET wallet = ? WHERE username = ?;");
                stmt.setInt(1, wallet);
                stmt.setString(2, user);
                stmt.executeUpdate();

            }

		} catch (SQLException e) {
			// add info to logger
			e.printStackTrace();
		}

        //Everything succeeded
        return JsonToolkit.generateStatus("OK").toString();
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
			stmt = _connection.prepareStatement("SELECT * FROM giftcards WHERE id = ? and nonce = ? and owner = ?;");
            stmt.setInt(1, id);
            stmt.setString(2, nonce);
            stmt.setString(3, user);
			res = stmt.executeQuery();

            //Check if result set isn't empty
            if(!res.isBeforeFirst()) {
                //Empty
                return JsonToolkit.generateStatus("ERROR", "Giftcard non-existent for given user").toString();
            }

            else {
                //Now we must find if target exists
                stmt = _connection.prepareStatement("SELECT * FROM client WHERE username = ?;");
                stmt.setString(1, target);
                stmt.executeQuery();

                //Check if result set isn't empty
                if(!res.isBeforeFirst()) {
                    //Empty
                    return JsonToolkit.generateStatus("ERROR", "Target user non-existent").toString();
                }
                else {
                    //Change Ownership
                    stmt = _connection.prepareStatement("Update giftcard SET owner = ? WHERE id = ? and nonce = ?;");
                    stmt.setString(1, target);
                    stmt.setInt(2, id);
                    stmt.setString(3, nonce);
                    stmt.executeUpdate();
                }
		    }
        } catch (SQLException e) {
			// add info to logger
			e.printStackTrace();
		}

        //Everything succeeded
        return JsonToolkit.generateStatus("OK").toString();
	}

    public boolean create_giftcard(int value) {
		PreparedStatement stmt;

		try {

            //Generate a secure random number
            SecureRandom nonceGen = new SecureRandom();
            byte nonceBytes[] = new byte[8]; //Equivalent to 16 hex chars
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
            stmt.setInt(2, value);
			stmt.executeUpdate();


		} catch (SQLException e) {
			// add info to logger
			e.printStackTrace();
		}

        //Everything succeeded
        return true;
	}

    public String check_balance(String auth_token) {
		PreparedStatement stmt;
		ResultSet res = null;
        int balance;

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
            if(!res.isBeforeFirst()) {
                //Empty
                return JsonToolkit.generateStatus("ERROR", "SQL Query returned no results").toString();
            }
            else 
                res.next();
            
            balance = res.getInt("wallet");

            return JsonToolkit.generateStatus("OK", "balance", Integer.toString(balance)).toString();

		} catch (SQLException e) {
			// add info to logger
			e.printStackTrace();
		}
        return JsonToolkit.generateStatus("ERROR", "Unknown SQL error").toString();
	}

}
