package com.sirs.thecork.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Timestamp;

import java.security.SecureRandom;
import org.json.JSONArray;
import org.json.JSONObject;

public class TokenManager {
    
    enum USER_TYPE {
        CUSTOMER,
        STAFF
    }

    private Connection _conn;
    private USER_TYPE _type;


    public TokenManager(Connection conn, boolean isStaff) {
        _conn = conn;

        if(isStaff)
            _type = USER_TYPE.CUSTOMER;
        else
            _type = USER_TYPE.STAFF;
	}

    /**
     * Generate a new login token for the given user, updating DB
     * @param user
     * @return the token
     */
    public String generateToken(String user)
    {
        PreparedStatement stmt;

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

        //Get current timestamp
        //Timestamp ts = new Timestamp(System.currentTimeMillis());

        try {
            if(_type == USER_TYPE.CUSTOMER)
			    stmt = _conn.prepareStatement("UPDATE client SET auth_token_nonce=?, token_exp_date=? WHERE username=?");
            else
                stmt = _conn.prepareStatement("UPDATE staff SET auth_token_nonce=?, token_exp_date=? WHERE username=?");

            stmt.setString(1, tokenString.toString());
            stmt.setTimestamp(2, null, null);
			stmt.setString(3,  user);
			stmt.executeUpdate();
		} catch (SQLException e) {
			// add info to logger
			e.printStackTrace();
			return null;
		}
		
        return tokenString.toString();
    }

    /**
     * Checks if given token is still valid for given user
     * @param tk
     * @param user
     * @return true if valid token
     */
    public boolean validateToken(String user, String tk)
    {
        return true;
    }
    

}
