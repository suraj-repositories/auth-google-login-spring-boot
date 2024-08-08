package com.on14june.controller;

import java.util.Collections;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.on14june.entity.User;
import com.on14june.service.UserService;


@Controller()
public class AuthController {

	@Autowired
	private UserService service;
	
	@Autowired
	private PasswordEncoder passwordEncoder;
	
	@GetMapping("/")
	public String home(Model model, Authentication authentication) {
		if(authentication != null) {
			User user = service.getUserByEmail(authentication.getName());
			model.addAttribute("user", user);
		}
		return "welcome";
	}
	
	@GetMapping("/login")
	public String loginPage(Authentication authentication) {
		if(authentication != null) {
			return "redirect:/";
		}
		return "login";
	}
	
	@PostMapping
	public String doLogin(@RequestParam("email") String email, @RequestParam("password") String password , Model model) {
		User user = service.getUserByEmail(email);
		
		if(user == null && !passwordEncoder.matches(password, user.getPassword())) {
			model.addAttribute("error", "Invalid email or password!");
			return "Login";
		}
		
		SecurityContextHolder.getContext().setAuthentication(
				new UsernamePasswordAuthenticationToken(user, 
						null, 
						Collections.singleton(new SimpleGrantedAuthority("ROLE_" + user.getRole())))
				);
		
		return "redirect:/";
	}
	
	
	@GetMapping("/signup")
	public String signupPage(Model model, Authentication authentication) {
		if(authentication != null) {
			return "redirect:/";
		}
		model.addAttribute("user", new User());
		return "signup";
	}
	
	@PostMapping("/signup")
	public String signup(User user, Authentication authentication) {
		if(user!=null) {
			if(service.getUserByEmail(user.getEmail()) != null) {
				return "redirect:/signup";
			}
			user.setRole("USER");
			service.saveUser(user);
			return "redirect:/login";
		}
		
		return "redirect:/signup";
	}

	

	@GetMapping("/welcome")
	public String welcom() {
		return "redirect:/";
	}
	
	
}
