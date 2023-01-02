package com.sirs.thecork.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.sirs.thecork.db.CustomerCommander;

@RestController
public class CustomerController {

	CustomerCommander _commander = null;
	
	CustomerController(){
		_commander = new CustomerCommander();
	}

	@PostMapping("/customer_login")
	public String loginCustomer(@RequestParam("user") String user, @RequestParam("pass") String pass) {
		return _commander.loginCustomer(user, pass) ? "OK" : "NOK";
	}
}