package com.sirs.thecork.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
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
    
    public boolean loginCustomer(String user, String pass) {
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
                return false;
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
                return false;
            }   
            
		} catch (SQLException e) {
			// add info to logger
			e.printStackTrace();
		} catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

        //Everything succeeded -> generate new auth token
        String token = _tokenManager.generateToken(user);
        System.out.println(token);
        return true;
	}
	
	private JSONArray processResult(ResultSet res) throws SQLException {
		JSONArray json = new JSONArray();
		ResultSetMetaData rsmd = res.getMetaData();
		int numColumns = rsmd.getColumnCount();

		while(res.next()) {
		  JSONObject obj = new JSONObject();

		  for (int i=1; i<=numColumns; i++) {
		    String columnName = rsmd.getColumnName(i);
		    obj.put(columnName, res.getObject(columnName));
		  }
		  json.put(obj);
		}
		return json;
	}
}
