package com.sirs.thecork.common;

import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class EncryptionEngine {

	public EncryptionEngine() {
	}
	
	
	public String keyToString(SecretKey key) {
		return Base64.getEncoder().encodeToString(key.getEncoded());
	}
	
	public SecretKey stringToKey(String key) {
		byte[] decodedKey = Base64.getDecoder().decode(key);
		return new SecretKeySpec(decodedKey, 0, decodedKey.length, "AES"); 
	}
	
	public String ivToString(GCMParameterSpec iv) {
		return Base64.getEncoder().encodeToString(iv.getIV());
	}
	
	public GCMParameterSpec stringToIv(String iv){
		return new GCMParameterSpec(128, Base64.getDecoder().decode(iv));
	}
	
	public SecretKey generateKey(int bits) throws NoSuchAlgorithmException {
	    KeyGenerator keyGenerator;
	    SecretKey key;
	    
	    keyGenerator = KeyGenerator.getInstance("AES");
	    keyGenerator.init(bits);
	    key = keyGenerator.generateKey();

	    return key;
	}
	
	public GCMParameterSpec generateIv(int bits) {
		SecureRandom rand = new SecureRandom();
		byte[] iv = new byte[16];
	    rand.nextBytes(iv);
	    
	    return new GCMParameterSpec(128, iv);
	}
	
	public String encryptGCM(String plaintext, SecretKey key, GCMParameterSpec iv) throws InvalidKeyException,
				InvalidAlgorithmParameterException, NoSuchAlgorithmException, NoSuchPaddingException,
				IllegalBlockSizeException, BadPaddingException {
		
		Cipher cipher;
		byte[] ciphertext;
		
		cipher = Cipher.getInstance("AES/GCM/NoPadding");
	    cipher.init(Cipher.ENCRYPT_MODE, key, iv);
	    ciphertext = cipher.doFinal(plaintext.getBytes());

	    return Base64.getEncoder().encodeToString(ciphertext);
	}

	public String encryptECB(String plaintext, SecretKey key) throws InvalidKeyException,
				InvalidAlgorithmParameterException, NoSuchAlgorithmException, NoSuchPaddingException,
				IllegalBlockSizeException, BadPaddingException {
		
		Cipher cipher;
		byte[] ciphertext;
		
		cipher = Cipher.getInstance("AES/ECB");
	    cipher.init(Cipher.ENCRYPT_MODE, key);
	    ciphertext = cipher.doFinal(plaintext.getBytes());

	    return Base64.getEncoder().encodeToString(ciphertext);
	}
	
	public String decryptGCM(String ciphertext, SecretKey key, GCMParameterSpec iv) throws NoSuchPaddingException,
				NoSuchAlgorithmException, InvalidAlgorithmParameterException, InvalidKeyException,
				BadPaddingException, IllegalBlockSizeException {
		    
		    Cipher cipher;
		    byte[] plaintext;
		    
		    try {
				cipher = Cipher.getInstance("AES/GCM/NoPadding");
				cipher.init(Cipher.DECRYPT_MODE, key, iv);
				plaintext = cipher.doFinal(Base64.getDecoder().decode(ciphertext));
		    }
		    catch (javax.crypto.AEADBadTagException e) {
		    	return null;
		    }

		    return new String(plaintext);
		}

	public String decryptECB(String ciphertext, SecretKey key) throws NoSuchPaddingException,
				NoSuchAlgorithmException, InvalidAlgorithmParameterException, InvalidKeyException,
				BadPaddingException, IllegalBlockSizeException {
		    
		    Cipher cipher;
		    byte[] plaintext;
		    
		    cipher = Cipher.getInstance("AES/ECB");
		    cipher.init(Cipher.DECRYPT_MODE, key);
		    plaintext = cipher.doFinal(Base64.getDecoder().decode(ciphertext));

		    return new String(plaintext);
		}

}
