package com.sirs.thecork.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Timestamp;

import java.util.concurrent.TimeUnit;

import java.security.SecureRandom;

public class TokenManager {

    final int TIMEOUT = 2;

    enum USER_TYPE {
        CUSTOMER,
        STAFF
    }

    private Connection _conn;
    private USER_TYPE _type;


    public TokenManager(Connection conn, boolean isStaff) {
        _conn = conn;

        if(isStaff)
        _type = USER_TYPE.STAFF;

        else
            _type = USER_TYPE.CUSTOMER;
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
        byte nonceBytes[] = new byte[32]; //Equivalent to 64 hex chars
        nonceGen.nextBytes(nonceBytes); // Stores random bytes in nonce byte array

        //Convert nonce bytes to hex string to store in DB
        StringBuilder tokenString = new StringBuilder(2*nonceBytes.length);
        for (int i = 0; i < nonceBytes.length; i++) {
            String hex = Integer.toHexString(0xff&nonceBytes[i]);
            if(hex.length() == 1)
                tokenString.append('0');

            tokenString.append(hex);
        }

        //Get current timestamp with added Timeout period
        Timestamp ts = new Timestamp(System.currentTimeMillis()+ TimeUnit.MINUTES.toMillis(TIMEOUT));

        try {
            if(_type == USER_TYPE.CUSTOMER)
			    stmt = _conn.prepareStatement("UPDATE client SET auth_token=?, token_exp_time=? WHERE username=?");
            else
                stmt = _conn.prepareStatement("UPDATE staff SET auth_token=?, token_exp_time=? WHERE username=?");

            stmt.setString(1, tokenString.toString());
            stmt.setTimestamp(2, ts);
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
     * Obtains user for given token
     * @param tk
     * @param user
     * @return user if valid token
     */
    public String validateToken(String tk)
    {
        PreparedStatement stmt;
        ResultSet res = null;
        String user = null;
        Timestamp exp;
        Timestamp now = new Timestamp(System.currentTimeMillis());

        try {
            if(_type == USER_TYPE.CUSTOMER)
                stmt = _conn.prepareStatement("SELECT username, token_exp_time FROM client WHERE auth_token=?;");
            else
                stmt = _conn.prepareStatement("SELECT username, token_exp_time FROM staff WHERE auth_token=?;");
            stmt.setString(1, tk);
            res = stmt.executeQuery();

            //Check if result set isn't empty
            if(!res.isBeforeFirst()) {
                //Empty
                return null;
            }
            else {
                res.next();
            }

            user = res.getString("username");
            exp = res.getTimestamp("token_exp_time");
        }
        catch (SQLException e){
			e.printStackTrace();
            return null;
		}

        if (now.after(exp)){
            try {
                if(_type == USER_TYPE.CUSTOMER)
                    stmt = _conn.prepareStatement("UPDATE client SET auth_token=NULL, token_exp_time=NULL WHERE auth_token=?;");
                else
                    stmt = _conn.prepareStatement("UPDATE staff SET auth_token=NULL, token_exp_time=NULL WHERE auth_token=?;");

                stmt.setString(1, tk);
                stmt.executeUpdate();
            }
            catch (SQLException e){
                e.printStackTrace();
            }
            return null;
        }

        return user;
    }
}
