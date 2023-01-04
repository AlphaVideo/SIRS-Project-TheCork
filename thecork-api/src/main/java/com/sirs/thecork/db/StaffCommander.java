package com.sirs.thecork.db;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.sirs.thecork.common.JsonToolkit;
import com.sirs.thecork.common.TokenManager;

public class StaffCommander {

    Connection _connection = null;
    TokenManager _tokenManager = null;

    public StaffCommander() {
        _connection = DatabaseConnection.getConnection();
        _tokenManager = new TokenManager(_connection, true);
	}

    public String loginStaff(String user, String pass) {
        PreparedStatement stmt;
        ResultSet res = null;
        String password = pass;

        try {
            //First we must find if user exists
            stmt = _connection.prepareStatement("SELECT * FROM staff WHERE username = ?;");
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

    public String createGiftcard(String auth_token, int value) {
		PreparedStatement stmt;
        int count = -1;

        String user = _tokenManager.validateToken(auth_token);
        if (user == null)
            return JsonToolkit.generateStatus("ERROR", "INVALID_AUTH_TOKEN").toString();

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
            stmt.setInt(2, value);
			count = stmt.executeUpdate();

		} catch (SQLException e) {
			// add info to logger
			e.printStackTrace();
		}

        if(count == 1)
            return JsonToolkit.generateStatus("OK").toString();
        else
            return JsonToolkit.generateStatus("ERROR", "Failed to insert new giftcard into DB.").toString();
	}

}
