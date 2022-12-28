package com.sirs.thecork_client;

public class App {

	public static void main(String[] args) {
		
		ServerConnection conn = new ServerConnection();

		try {
			conn.testInternet2();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}