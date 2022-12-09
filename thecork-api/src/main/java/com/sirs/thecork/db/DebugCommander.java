package com.sirs.thecork.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;

import org.json.JSONArray;
import org.json.JSONObject;

public class DebugCommander {
	
	Connection _connection = null;

	public DebugCommander() {
		_connection = DatabaseConnection.getConnection();
	}
	
	public JSONArray listRestaurant() {
		PreparedStatement stmt;
		ResultSet res = null;
		JSONArray json = null;
		
		try {
			stmt = _connection.prepareStatement("SELECT * FROM restaurant;");
			res = stmt.executeQuery();		
			json = processResult(res);

		} catch (SQLException e) {
			// add info to logger
			e.printStackTrace();
		}
		
		return json;
	}
	
	public boolean addRestaurant(String name) {
		PreparedStatement stmt;
		int count;
		
		try {
			stmt = _connection.prepareStatement("INSERT INTO restaurant VALUES (?);");
			stmt.setString(1,  name);
			count = stmt.executeUpdate();
		} catch (SQLException e) {
			// add info to logger
			e.printStackTrace();
			return false;
		}
		return count == 1;
	}

	public boolean removeRestaurant(String name) {
		PreparedStatement stmt;
		int count;
		
		try {
			stmt = _connection.prepareStatement("DELETE FROM restaurant WHERE name = ?;");
			stmt.setString(1,  name);
			count = stmt.executeUpdate();
		} catch (SQLException e) {
			// add info to logger
			e.printStackTrace();
			return false;
		}
		return count == 1;
	}
	
	private JSONArray processResult(ResultSet res) throws SQLException {
		JSONArray json = new JSONArray();
		ResultSetMetaData rsmd = res.getMetaData();
		int numColumns = rsmd.getColumnCount();

		while(res.next()) {
		  JSONObject obj = new JSONObject();

		  for (int i=1; i<=numColumns; i++) {
		    String columnName = rsmd.getColumnName(i);
		    obj.put(columnName, res.getObject(columnName));
		  }
		  json.put(obj);
		}
		return json;
	}
}
