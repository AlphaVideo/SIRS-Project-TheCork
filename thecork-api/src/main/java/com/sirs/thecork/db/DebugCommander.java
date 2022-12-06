package com.sirs.thecork.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class DebugCommander {
	
	Connection _connection = null;

	public DebugCommander() {
		_connection = DatabaseConnection.getConnection();
	}
	
	public ResultSet listRestaurant() {
		PreparedStatement stmt;
		ResultSet res = null;
		
		try {
			stmt = _connection.prepareStatement("SELECT * FROM restaurant;");
			res = stmt.executeQuery();		

		} catch (SQLException e) {
			// add info to logger
			e.printStackTrace();
		}
		return res;
	}
}
