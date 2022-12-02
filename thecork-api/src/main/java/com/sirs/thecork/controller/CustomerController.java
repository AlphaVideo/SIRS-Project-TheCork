package com.sirs.thecork.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class CustomerController {

	@GetMapping("/")
	public String getDefault() {
		return "Welcome to TheCork web services!";
	}
	
	@GetMapping("/demo/{name}")
	public String getDemo(@PathVariable String name) {
		return String.format("Hello %s!", name);
	}
}