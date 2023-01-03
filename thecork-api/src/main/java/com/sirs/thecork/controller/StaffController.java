package com.sirs.thecork.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.sirs.thecork.db.StaffCommander;

@RestController
public class StaffController {
    StaffCommander _commander = null;

	StaffController(){
		_commander = new StaffCommander();
	}

    @PostMapping(value="/login/staff", produces="application/json")
	public String loginCustomer(@RequestParam("user") String user, @RequestParam("pass") String pass) {
		return _commander.loginStaff(user, pass);
	}
}
