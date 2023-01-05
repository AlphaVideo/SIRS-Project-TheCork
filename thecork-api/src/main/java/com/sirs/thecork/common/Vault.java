package com.sirs.thecork.common;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;

public class Vault {

	private Connection _conn = null;
	
	public Vault() {
		if (!dbExists()) {
			connect();  // dont call connect before if() as it tampers with dbExists()
			init();
		}
		else {
			connect();
		}
	}
	
	private void init() {
		Statement stmt;
		try {
			stmt = _conn.createStatement();
			stmt.executeUpdate("CREATE TABLE IF NOT EXISTS teste(id INTEGER PRIMARY KEY, name VARCHAR(16));");
			stmt.executeUpdate("CREATE TABLE IF NOT EXISTS teste(id INTEGER PRIMARY KEY, name VARCHAR(16));");
		
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	private void connect() {  
		PreparedStatement stmt;
		try {  
			Class.forName("org.sqlite.JDBC");
			_conn = DriverManager.getConnection("jdbc:sqlite:keys.db");  
              
		} catch (SQLException | ClassNotFoundException e) {  
			System.out.println(e.getMessage());  
		}  
	}
	
	private boolean dbExists() {
		File db = new File("keys.db");
		return db.exists();
	}
}
