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
		conversorFdd();
		conversorFdd2();
		
		//try {
			//System.out.println(_ngn.encryptGCM("12345678", k, new GCMParameterSpec(128, Base64.getDecoder().decode(iv64))));
			//System.out.println(_ngn.encryptGCM("123456789ABCDEF0", k, new GCMParameterSpec(128, Base64.getDecoder().decode(iv64))));
			//System.out.println(_ngn.decryptGCM("8mddTBql4gOXLuSJR2IJV8B7o1Nu/Y2fm6dAP5v8eCg=", k, new GCMParameterSpec(128, Base64.getDecoder().decode(iv64))));

		//} catch (NoSuchAlgorithmException | InvalidKeyException | InvalidAlgorithmParameterException | NoSuchPaddingException | IllegalBlockSizeException | BadPaddingException e) {
			//e.printStackTrace();
		//}
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
			
            while (!res.next()) {
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
		String owner;
		int id, value;
		
		try {
			stmt = _conn.prepareStatement("SELECT id,owner,value FROM giftcard;");
			res = stmt.executeQuery();
			
            while (!res.next()) {
				id = res.getInt("id");
				owner = res.getString("owner");
				value = res.getInt("value");
				
				System.out.println("id: " + id);
				iv = _ngn.generateIv(128);
				System.out.println("owner: " + _ngn.encryptGCM(owner, _key, iv));
				System.out.println("value: " + _ngn.encryptGCM(Integer.toString(value), _key, iv));
            }
		}
		catch (SQLException | InvalidKeyException | InvalidAlgorithmParameterException | NoSuchAlgorithmException | NoSuchPaddingException | IllegalBlockSizeException | BadPaddingException e) {
			e.printStackTrace();
		}
	}
	
	private GCMParameterSpec getClientIv(String user) {
		PreparedStatement stmt;
		ResultSet res;
		String iv;
		
		try {
			stmt = _conn.prepareStatement("SELECT iv FROM client_ivs WHERE username = ?;");
			stmt.setString(1, user);
			res = stmt.executeQuery();
			
            if(!res.isBeforeFirst())
            	return null;
            
            res.next();
            iv = res.getString("iv");
		}
		catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
		return new GCMParameterSpec(128, Base64.getDecoder().decode(iv));		
	}
	private GCMParameterSpec getGiftcardIv(int id) {
		PreparedStatement stmt;
		ResultSet res;
		String iv;
		
		try {
			stmt = _conn.prepareStatement("SELECT iv FROM giftcard_ivs WHERE id = ?;");
			stmt.setInt(1, id);
			res = stmt.executeQuery();
			
            if(!res.isBeforeFirst())
            	return null;
            
            res.next();
            iv = res.getString("iv");
		}
		catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
		return new GCMParameterSpec(128, Base64.getDecoder().decode(iv));		
	}
}
