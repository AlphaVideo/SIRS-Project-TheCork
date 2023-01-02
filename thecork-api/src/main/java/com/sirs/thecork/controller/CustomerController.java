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

	@PostMapping("/reservation")
	public String reservation(@RequestParam("user") String user, @RequestParam("restarurant") String restarurant, @RequestParam("nPeople") int nPeople, @RequestParam("datetime") String datetime) {
		return _commander.reservation(user, restarurant, nPeople, datetime) ? "OK" : "NOK";
	}

	@PostMapping("/buy_giftcard")
	public String buy_giftcard(@RequestParam("user") String user, @RequestParam("value") int value) {
		return _commander.buy_giftcard(user, value) ? "OK" : "NOK";
	}

	@PostMapping("/redeem_giftcard")
	public String redeem_giftcard(@RequestParam("user") String user, @RequestParam("id") int id, @RequestParam("nonce") int nonce) {
		return _commander.redeem_giftcard(user, id, nonce) ? "OK" : "NOK";
	}

	@PostMapping("/gift_giftcard")
	public String gift_giftcard(@RequestParam("user") String user, @RequestParam("target") String target, @RequestParam("id") int id, @RequestParam("nonce") int nonce) {
		return _commander.gift_giftcard(user, target, id, nonce) ? "OK" : "NOK";
	}

	@PostMapping("/check_balance")
	public String check_balance(@RequestParam("user") String user) {
		return _commander.check_balance(user) ? "OK" : "NOK";
	}
}