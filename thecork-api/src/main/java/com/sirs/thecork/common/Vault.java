package com.sirs.thecork.common;

import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Base64;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;

import com.sirs.thecork.db.DatabaseConnection;

public class Vault {

	private Connection _conn;
	private EncryptionEngine _ngn;
	private final String _keyStr = "xDql5+mSpFgDCSezefMtfDO25NOv7KaA1ZeLSgIqDic=";
	private SecretKey _key;
	
	public Vault() {
		_conn = DatabaseConnection.getConnection();
		_ngn = new EncryptionEngine();
		_key = _ngn.stringToKey(_keyStr);
		//conversorFdd();
		//conversorFdd2();
	}
	
	public String clientDecipher(String user, String ciphertext) throws SQLException, NumberFormatException, InvalidKeyException, NoSuchPaddingException, NoSuchAlgorithmException, InvalidAlgorithmParameterException, BadPaddingException, IllegalBlockSizeException {
		PreparedStatement stmt;
		ResultSet res;
		GCMParameterSpec iv;
		
		stmt = _conn.prepareStatement("SELECT iv FROM client_ivs WHERE username = ?;");
		stmt.setString(1,  user);
		res = stmt.executeQuery();			
		res.next();
		
		iv = _ngn.stringToIv(res.getString("iv"));
		
		return _ngn.decryptGCM(ciphertext, _key, iv);
	}
	
	public String clientEncipher(String user, String plaintext) throws SQLException, NumberFormatException, InvalidKeyException, NoSuchPaddingException, NoSuchAlgorithmException, InvalidAlgorithmParameterException, BadPaddingException, IllegalBlockSizeException {
		PreparedStatement stmt;
		ResultSet res;
		GCMParameterSpec iv;
		
		stmt = _conn.prepareStatement("SELECT iv FROM client_ivs WHERE username = ?;");
		stmt.setString(1,  user);
		res = stmt.executeQuery();			
		res.next();
		
		iv = _ngn.stringToIv(res.getString("iv"));
		
		return _ngn.encryptGCM(plaintext, _key, iv);
	}
	
	
	public String giftcardDecipher(int id, String ciphertext) throws SQLException, NumberFormatException, InvalidKeyException, NoSuchPaddingException, NoSuchAlgorithmException, InvalidAlgorithmParameterException, BadPaddingException, IllegalBlockSizeException {
		PreparedStatement stmt;
		ResultSet res;
		GCMParameterSpec iv;
		
		stmt = _conn.prepareStatement("SELECT iv FROM giftcard_ivs WHERE id = ?;");
		stmt.setInt(1,  id);
		res = stmt.executeQuery();			
		res.next();
		
		iv = _ngn.stringToIv(res.getString("iv"));
		
		return _ngn.decryptGCM(ciphertext, _key, iv);
	}
	
	public String giftcardEncipher(int id, String plaintext) throws SQLException, NumberFormatException, InvalidKeyException, NoSuchPaddingException, NoSuchAlgorithmException, InvalidAlgorithmParameterException, BadPaddingException, IllegalBlockSizeException {
		PreparedStatement stmt;
		ResultSet res;
		GCMParameterSpec iv;
		
		stmt = _conn.prepareStatement("SELECT iv FROM giftcard_ivs WHERE id = ?;");
		stmt.setInt(1,  id);
		res = stmt.executeQuery();			
		res.next();
		
		iv = _ngn.stringToIv(res.getString("iv"));
		
		return _ngn.encryptGCM(plaintext, _key, iv);
	}
	
	
	private void conversorFdd() {
		PreparedStatement stmt;
		ResultSet res;
		GCMParameterSpec iv;
		String name;
		int wallet;
		
		try {
			stmt = _conn.prepareStatement("SELECT username,wallet FROM client;");
			res = stmt.executeQuery();
			
            while (res.next()) {
				name = res.getString("username");
				wallet = res.getInt("wallet");
				
				System.out.println("user: " + name);
				iv = _ngn.generateIv(128);
				System.out.println("wallet: " + _ngn.encryptGCM(Integer.toString(wallet), _key, iv));
            }
            
		}
		catch (SQLException | InvalidKeyException | InvalidAlgorithmParameterException | NoSuchAlgorithmException | NoSuchPaddingException | IllegalBlockSizeException | BadPaddingException e) {
			e.printStackTrace();
		}
	}
	private void conversorFdd2() {
		PreparedStatement stmt;
		ResultSet res;
		GCMParameterSpec iv;
		int id, value;
		
		try {
			stmt = _conn.prepareStatement("SELECT id,value FROM giftcard;");
			res = stmt.executeQuery();
			
            while (res.next()) {
				id = res.getInt("id");
				value = res.getInt("value");
				
				System.out.println("id: " + id);
				iv = _ngn.generateIv(128);
				System.out.println("value: " + _ngn.encryptGCM(Integer.toString(value), _key, iv));
            }
		}
		catch (SQLException | InvalidKeyException | InvalidAlgorithmParameterException | NoSuchAlgorithmException | NoSuchPaddingException | IllegalBlockSizeException | BadPaddingException e) {
			e.printStackTrace();
		}
	}
}
