package com.on08aug24.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class UserController {

	@GetMapping("/")
	@ResponseBody
	public String getloginPage() {
		return "Login page";
	} 
	
	
}
