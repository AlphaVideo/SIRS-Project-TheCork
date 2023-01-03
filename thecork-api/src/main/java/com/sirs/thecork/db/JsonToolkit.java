package com.sirs.thecork.db;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;

import org.json.JSONArray;
import org.json.JSONObject;


public class JsonToolkit {

    public static JSONArray processResult(ResultSet res) throws SQLException {
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
    
    public static JSONObject generateStatus(String status){
        JSONObject json = new JSONObject();

        json.put("status", status);

        return json;
    }
    public static JSONObject generateStatus(String status, String reason){
        JSONObject json = new JSONObject();

        json.put("status", status);
        json.put("reason", reason);

        return json;
    }
    public static JSONObject generateStatus(String status, String field, String val){
        JSONObject json = new JSONObject();

        json.put("status", status);
        json.put(field, val);

        return json;
    }
}
