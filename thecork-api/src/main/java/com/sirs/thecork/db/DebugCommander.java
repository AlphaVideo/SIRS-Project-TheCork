package com.sirs.thecork.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.json.JSONArray;
import org.json.JSONException;
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
	
	public void addRestaurant(String name) {
		PreparedStatement stmt;
		
		try {
			stmt = _connection.prepareStatement("INSERT INTO restaurant VALUES ?;");
			stmt.executeQuery();
		} catch (SQLException e) {
			// add info to logger
			e.printStackTrace();
		}
	}

	public void removeRestaurant(String name) {
		PreparedStatement stmt;
		
		try {
			stmt = _connection.prepareStatement("DELETE FROM restaurant WHERE name = ?;");
			stmt.executeQuery();
		} catch (SQLException e) {
			// add info to logger
			e.printStackTrace();
		}
	}
	
	private JSONArray processResult(ResultSet res) throws SQLException {
		JSONArray json = new JSONArray();
		ResultSetMetaData md = res.getMetaData();
		int numCols = md.getColumnCount();

		List<String> colNames = IntStream.range(0, numCols)
		  .mapToObj(i -> {
		      try {
		          return md.getColumnName(i + 1);
		      } catch (SQLException e) {
		          e.printStackTrace();
		          return "?";
		      }
		  })
		  .collect(Collectors.toList());


		while (res.next()) {
		    JSONObject row = new JSONObject();
		    colNames.forEach(cn -> {
		            try {
						row.put(cn, res.getObject(cn));
					} catch (JSONException | SQLException e) {
						e.printStackTrace();
					}
		    });
		    json.put(row);
		}		
		return json;
	}
}
