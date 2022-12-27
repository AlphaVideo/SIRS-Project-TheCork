package com.sirs.thecork_client;

import java.io.IOException;
import java.net.URL;
import java.security.NoSuchAlgorithmException;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;

public class ServerConnection {

	public ServerConnection() {
		// TODO Auto-generated constructor stub
	}

	public void testInternet() throws NoSuchAlgorithmException, IOException {
		URL url = new URL("https://fenix.tecnico.ulisboa.pt/api/fenix/v1/courses/564560566181922/schedule?academicTerm=2021/2022&lang=pt-PT");
		
		SSLContext ctx = SSLContext.getInstance("TLS");
		SSLContext.setDefault(ctx);
		// Falta inicializar com KeyManager
		
		HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
		
		connection.setRequestMethod("GET");
		connection.setDoOutput(true);
		
		System.out.println(connection.getInputStream());
	}
}
