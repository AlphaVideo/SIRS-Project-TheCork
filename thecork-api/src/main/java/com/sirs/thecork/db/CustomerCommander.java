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

    public boolean reservation(String user, String restaurant, Int nPeople, String datetime) {
		PreparedStatement stmt;
		ResultSet res = null;
		
		try {
            //We can assume the user exists because he already went through the login process
            //First we must find if restaurant exists
			stmt = _connection.prepareStatement("SELECT * FROM restaurant WHERE name = ?;");
            stmt.setString(1, restaurant);
			res = stmt.executeQuery();		

            //Check if result set isn't empty
            if(!res.isBeforeFirst()) {
                //Empty
                return false;
            }

            //Add reservation to database
            else {
                stmt = _connection.prepareStatement("INSERT INTO reservation VALUES (?, ?, ?, ?);");
                stmt.setString(1, user);
                stmt.setString(2, restaurant);
                stmt.setString(3, datetime);
                stmt.setString(4, nPeople);
                stmt.executeQuery();	

            }
            
            }   
            
		} catch (SQLException e) {
			// add info to logger
			e.printStackTrace();
		} catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

        //Everything succeeded
        return true;
	}
	
    public boolean buy_giftcard(String user, Int value) {
		PreparedStatement stmt;
		ResultSet res = null;
		
		try {
            //We can assume the user exists because he already went through the login process

            //Activate a giftcard
            //TODO: check if sql statement works and return the id and nonce to the customer
            stmt = _connection.prepareStatement("UPDATE TOP (1) giftcards SET owner = ? WHERE owner = NULL and value = ?;");
            stmt.setString(1, user);
            stmt.setString(2, value);
            stmt.executeQuery();	

            //TODO: Create a new giftcard
            
            }   
            
		} catch (SQLException e) {
			// add info to logger
			e.printStackTrace();
		} catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

        //Everything succeeded
        return true;
	}

    public boolean redeem_giftcard(String user, Int id, Int nonce) {
		PreparedStatement stmt;
		ResultSet res = null;
		
		try {
            //We can assume the user exists because he already went through the login process
            //First we must find if giftcard exists and it belongs to the user
			stmt = _connection.prepareStatement("SELECT * FROM giftcards WHERE id = ? and nonce = ? and owner = ?;");
            stmt.setString(1, id);
            stmt.setString(2, nonce);
            stmt.setString(3, user);
			res = stmt.executeQuery();		

            //Check if result set isn't empty
            if(!res.isBeforeFirst()) {
                //Empty
                return false;
            }

            //Redeem giftcard
            else {
                
                //Remove Giftcard From Database
                Int value = res.getInt("value");

                stmt = _connection.prepareStatement("DELETE FROM giftcards WHERE id = ? and nonce = ? and owner = ?;");
                stmt.setString(1, id);
                stmt.setString(2, nonce);
                stmt.setString(3, user);
                stmt.executeQuery();

                //Check how much money the user currently has
			    stmt = _connection.prepareStatement("SELECT * FROM client WHERE username = ?;");
                stmt.setString(1, user);
                res = stmt.executeQuery();	

                Int wallet = res.getInt("wallet");

                //Add the value of the Giftcard
                wallet += value

                stmt = _connection.prepareStatement("Update client SET wallet = ? WHERE username = ?;");
                stmt.setString(1, wallet);
                stmt.setString(2, user);
                stmt.executeQuery();	

            }
            
            }   
            
		} catch (SQLException e) {
			// add info to logger
			e.printStackTrace();
		} catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

        //Everything succeeded
        return true;
	}

    public boolean gift_giftcard(String user, String target, Int id, Int nonce) {
		PreparedStatement stmt;
		ResultSet res = null;
		
		try {
            //We can assume the user exists because he already went through the login process
            //First we must find if giftcard exists and it belongs to the user
			stmt = _connection.prepareStatement("SELECT * FROM giftcards WHERE id = ? and nonce = ? and owner = ?;");
            stmt.setString(1, id);
            stmt.setString(2, nonce);
            stmt.setString(3, user);
			stmt.executeQuery();		

            //Check if result set isn't empty
            if(!res.isBeforeFirst()) {
                //Empty
                return false;
            }

            else {
                //Now we must find if target exists
                stmt = _connection.prepareStatement("SELECT * FROM client WHERE username = ?;");
                stmt.setString(1, target);
                stmt.executeQuery();		

                //Check if result set isn't empty
                if(!res.isBeforeFirst()) {
                    //Empty
                    return false;
                }
                else {
                    //Change Ownership
                    stmt = _connection.prepareStatement("Update giftcard SET owner = ? WHERE id = ? and nonce = ?;");
                    stmt.setString(1, target);
                    stmt.setString(2, id);
                    stmt.setString(3, nonce);
                    stmt.executeQuery();
                }   
            
		} catch (SQLException e) {
			// add info to logger
			e.printStackTrace();
		} catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

        //Everything succeeded
        return true;
	}

     public boolean check_balance(String user) {
		PreparedStatement stmt;
		ResultSet res = null;
		
		try {
            //We can assume the user exists because he already went through the login process
            //Check Balance
			stmt = _connection.prepareStatement("SELECT * FROM cluent WHERE username = ?;");
            stmt.setString(1, user);
			res = stmt.executeQuery();	

            Int balance = res.getInt("wallet");

            //TODO: Return Balance somehow
 
            
		} catch (SQLException e) {
			// add info to logger
			e.printStackTrace();
		} catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

        //Everything succeeded
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
