package com.render.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class home {

	@GetMapping("/")
	public String home() {
		
		return "home";
	}
}
