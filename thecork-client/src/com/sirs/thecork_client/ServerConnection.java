package com.sirs.thecork_client;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.UnknownHostException;
import java.security.KeyStore;
import java.security.NoSuchAlgorithmException;
import java.security.cert.Certificate;

import javax.net.SocketFactory;
import javax.net.ssl.HandshakeCompletedEvent;
import javax.net.ssl.HandshakeCompletedListener;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLPeerUnverifiedException;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;

public class ServerConnection {

	public ServerConnection() {
		// TODO Auto-generated constructor stub
	}

	public void testInternet() throws NoSuchAlgorithmException, IOException {
		URL url = new URL("https://192.168.1.3:8443");
		
		//SSLContext ctx = SSLContext.getInstance("TLS");
		//SSLContext.setDefault(ctx);
		// Falta inicializar com KeyManager
		
		HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
		
		printConnInfo(connection);
		printContent(connection);
		
		connection.setRequestMethod("GET");
		connection.setDoOutput(true);
		
		System.out.println(connection.getInputStream());
	}
	
	public void testInternet2() throws UnknownHostException, IOException {
		SSLSocketFactory factory = (SSLSocketFactory) this.getSocketFactory();
		SSLSocket socket = (SSLSocket) factory.createSocket("192.168.1.3", 8443);
		
		String[] enable = {"TLS_DH_anon_WITH_AES_128_CBC_SHA"};
        socket.setEnabledCipherSuites(enable);
        String[] cipherSuites = socket.getEnabledCipherSuites();
        for (int i = 0; i < cipherSuites.length; i++) {
            System.out.println(cipherSuites[i]);
        }
        socket.addHandshakeCompletedListener(new HandshakeCompletedListener() {
            public void handshakeCompleted(HandshakeCompletedEvent event) {
                System.out.println("handshake done");
            }
        });
        socket.startHandshake();
        System.out.println(socket.getOutputStream());
	}
	
	private SocketFactory getSocketFactory(){
	    SSLSocketFactory ssf = null; 
	    try{
	        SSLContext ctx;
	        KeyManagerFactory kmf;
	        KeyStore ks;
	        
	        char[] passphrase = "sirssirs".toCharArray();
	        
	        ctx = SSLContext.getInstance("TLS");
	        kmf = KeyManagerFactory.getInstance("SunX509");
	        ks = KeyStore.getInstance("JKS");
	        
	        ks.load(new FileInputStream("/home/seed/TheCork/thecork-client/resources/client.jks"), passphrase);
	        kmf.init(ks, passphrase);
	        ctx.init(kmf.getKeyManagers(), null, null);
	        ssf = ctx.getSocketFactory();
	        
	        return ssf;
	    }catch(Exception e){
	        e.printStackTrace();
	    }
	    return null;
	}
	
	private void printConnInfo(HttpsURLConnection con) {
		if(con!=null){
			
		      try {
						
			System.out.println("Response Code : " + con.getResponseCode());
			System.out.println("Cipher Suite : " + con.getCipherSuite());
			System.out.println("\n");
						
			Certificate[] certs = con.getServerCertificates();
			for(Certificate cert : certs){
			   System.out.println("Cert Type : " + cert.getType());
			   System.out.println("Cert Hash Code : " + cert.hashCode());
			   System.out.println("Cert Public Key Algorithm : " 
		                                    + cert.getPublicKey().getAlgorithm());
			   System.out.println("Cert Public Key Format : " 
		                                    + cert.getPublicKey().getFormat());
			   System.out.println("\n");
			}
						
			} catch (SSLPeerUnverifiedException e) {
				e.printStackTrace();
			} catch (IOException e){
				e.printStackTrace();
			}

		}
	}
	
	   private void printContent(HttpsURLConnection con){
			if (con != null){
					
				try {
				
					System.out.println("****** Content of the URL ********");			
					BufferedReader br = 
							new BufferedReader(
									new InputStreamReader(con.getInputStream()));
						
					String input;
						
					while ((input = br.readLine()) != null){
						System.out.println(input);
					}
					br.close();
						
				} catch (IOException e) {
					e.printStackTrace();
				}
					
			}
	   }
}
