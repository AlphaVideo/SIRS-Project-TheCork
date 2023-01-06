package com.sirs.thecork.common;

import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;

public class Tester {

	public Tester() {
		// TODO Auto-generated constructor stub
	}

	public static void main(String[] args) throws InvalidKeyException, InvalidAlgorithmParameterException, NoSuchAlgorithmException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException {
		//EncryptionEngine ngn = new EncryptionEngine();
		//ngn.generateIv(128);
		//Vault v = new Vault();
		EncryptionEngine _ngn = new EncryptionEngine();
		GCMParameterSpec iv;
		SecretKey _key = _ngn.stringToKey("xDql5+mSpFgDCSezefMtfDO25NOv7KaA1ZeLSgIqDic=");
		
		for (int i = 0; i < 4; i++) {
			System.out.println(String.format("- - - %d - - -", i));
			iv = _ngn.generateIv(128);
			System.out.println(String.format("IV: %s", _ngn.ivToString(iv)));
			System.out.println(String.format("0:  %s", _ngn.encryptGCM("000", _key, iv)));
		}
	}
}