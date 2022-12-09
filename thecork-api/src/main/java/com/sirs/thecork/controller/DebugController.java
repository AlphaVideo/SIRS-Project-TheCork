package com.sirs.thecork.controller;

import java.sql.ResultSet;

import org.json.JSONArray;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import com.sirs.thecork.db.DebugCommander;

@RestController
public class DebugController {

	DebugCommander _commander = null;
	
	public DebugController() {
		_commander = new DebugCommander();
	}

	@GetMapping("/")
	public String getDefault() {
		return "<h1 id=\"thecork\">TheCork™</h1>"
				+ "<p>Welcome to TheCork REST API services!</p>";
	}
	
	@GetMapping("/demo/{name}")
	public String getDemo(@PathVariable String name) {
		return String.format("Hello %s!", name);
	}

	@GetMapping(value="/debug/restaurant/list", produces="application/json")
	public String listRestaurant() {
		return _commander.listRestaurant();
	}

	@GetMapping("/debug/restaurant/add/{name}")
	public String addRestaurant(@PathVariable String name) {
		return _commander.addRestaurant(name) ? "OK" : "NOK";
	}

	@GetMapping("/debug/restaurant/remove/{name}")
	public String removeRestaurant(@PathVariable String name) {
		return _commander.addRestaurant(name) ? "OK" : "NOK";
	}
}
