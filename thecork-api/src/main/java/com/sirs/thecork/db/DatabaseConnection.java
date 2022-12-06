package com.sirs.thecork.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection implements AutoCloseable {
	
	private static DatabaseConnection _singleton = null;
	private static Connection _conn = null;

	private DatabaseConnection() {
		// get logger
	    try
	    {
	        String url = "jdbc:mysql://10.0.0.1:3306/thecork";
	        Class.forName ("com.mysql.cj.jdbc.Driver");
	        _conn = DriverManager.getConnection (url,"sirs","sirs");
	        // add info to logger
	    }
	    catch (ClassNotFoundException | SQLException e)
	    {
	    	// add info to logger
	        e.printStackTrace();
        }
	}
	
	public static DatabaseConnection getInstance() {
		if (_singleton == null) {
			_singleton = new DatabaseConnection();
		}
		return _singleton;
	}
	
	public static Connection getConnection() {
		if (_singleton == null) {
			_singleton = new DatabaseConnection();
		}
		return _conn;
	}
	
	public void close() {
		if (_conn != null)
		{
			try
			{
				_conn.close ();
				// add info to logger
			}
			catch (SQLException e) {
				// add info to logger and ignore
			}
		}
	}
}
