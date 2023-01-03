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

	@PostMapping(value="/login/customer", produces="application/json")
	public String loginCustomer(@RequestParam("user") String user, @RequestParam("pass") String pass) {
		return _commander.loginCustomer(user, pass);
	}

	@PostMapping(value="/reservation/create", produces="application/json")
	public String reservation(@RequestParam("auth_token") String auth_token, @RequestParam("restarurant") String restarurant, @RequestParam("nPeople") int nPeople, @RequestParam("datetime") String datetime) {
		return _commander.reservation(auth_token, restarurant, nPeople, datetime);
	}

	@PostMapping(value="/giftcard/buy", produces="application/json")
	public String buy_giftcard(@RequestParam("auth_token") String auth_token, @RequestParam("value") int value) {
		return _commander.buy_giftcard(auth_token, value);
	}

	@PostMapping(value="/giftcard/redeem", produces="application/json")
	public String redeem_giftcard(@RequestParam("auth_token") String auth_token, @RequestParam("id") int id, @RequestParam("nonce") String nonce) {
		return _commander.redeem_giftcard(auth_token, id, nonce);
	}

	@PostMapping(value="/giftcard/give", produces="application/json")
	public String give_giftcard(@RequestParam("auth_token") String auth_token, @RequestParam("target") String target, @RequestParam("id") int id, @RequestParam("nonce") String nonce) {
		return _commander.give_giftcard(auth_token, target, id, nonce);
	}

	@PostMapping(value="/check_balance", produces="application/json")
	public String check_balance(@RequestParam("auth_token") String auth_token) {
		return _commander.check_balance(auth_token);
	}
}
