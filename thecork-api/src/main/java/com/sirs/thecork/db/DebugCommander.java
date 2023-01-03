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
			json = JsonToolkit.processResult(res);

		} catch (SQLException e) {
			// add info to logger
			e.printStackTrace();
		}

		return json;
	}

	public String addRestaurant(String name) {
		PreparedStatement stmt;

		try {
			stmt = _connection.prepareStatement("INSERT INTO restaurant VALUES (?);");
			stmt.setString(1,  name);
			stmt.executeUpdate();
		} catch (SQLException e) {
			// add info to logger
			e.printStackTrace();
			return JsonToolkit.generateStatus("ERROR", "Unknown SQL error").toString();
		}
		return JsonToolkit.generateStatus("OK").toString();
	}

	public String removeRestaurant(String name) {
		PreparedStatement stmt;
		int count;

		try {
			stmt = _connection.prepareStatement("DELETE FROM restaurant WHERE name = ?;");
			stmt.setString(1,  name);
			count = stmt.executeUpdate();
		} catch (SQLException e) {
			// add info to logger
			e.printStackTrace();
			return JsonToolkit.generateStatus("ERROR", "Unknow SQL error").toString();
		}
		
		if (count != 0)
			return JsonToolkit.generateStatus("OK").toString();
		else
			return JsonToolkit.generateStatus("ERROR", "Restaurant non-existent").toString();
	}
}
