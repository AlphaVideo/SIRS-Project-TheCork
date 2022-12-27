package com.sirs.thecork_client;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;

public class Application {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		ServerConnection conn = new ServerConnection();

		try {
			conn.testInternet();
		} catch (NoSuchAlgorithmException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
