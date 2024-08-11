package com.on14june.controller;

import java.util.Collections;
import java.util.Map;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.on14june.entity.User;
import com.on14june.service.UserService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Controller
public class AuthController {

	@Autowired
	private UserService service;

	@Autowired
	private PasswordEncoder passwordEncoder;

	private Logger LOGGER = LoggerFactory.getLogger(AuthController.class);

	@GetMapping("/login")
	public String loginPage(Authentication authentication) {
		if (authentication != null) {
			return "redirect:/";
		}
		return "login";
	}

	@GetMapping("/")
	public String home(Model model, Authentication authentication, HttpServletRequest request,
			HttpServletResponse response) {
		if (authentication != null) {
			User user = service.getUserByEmail(authentication.getName());
			if (user == null) {
				Authentication auth = SecurityContextHolder.getContext().getAuthentication();
				if (auth != null) {
					new SecurityContextLogoutHandler().logout(request, response, auth);
				}
			}
			model.addAttribute("user", user);
		}
		return "welcome";
	}

	@GetMapping("/login/google")
	public String loginWithGoogle(OAuth2AuthenticationToken authentication) {
		try {
			Map<String, Object> attributes = authentication.getPrincipal().getAttributes();

			String email = (String) attributes.get("email");
			String name = (String) attributes.get("name");
			String picture = (String) attributes.get("picture");

			LOGGER.info("{} - {} - {}", email, name, picture);
			User user;

			user = service.getUserByEmail(email);

			if (user == null) {
				String unique = UUID.randomUUID().toString();

				LOGGER.info("{}", unique);
				User loginUser = User.builder().name(name).email(email).picture(picture).password(unique)
						.confirmPassword(unique).dob(null).role("USER").id(null).build();

				user = service.saveUser(loginUser);
			}

			Authentication auth = new UsernamePasswordAuthenticationToken(email, null,
					Collections.singleton(new SimpleGrantedAuthority("ROLE_" + user.getRole())));

			SecurityContextHolder.getContext().setAuthentication(auth);

		} catch (Exception e) {
			LOGGER.error("Error while login with Google: " + e.getMessage());
			return "redirect:/error";
		}
		return "redirect:/";
	}

	@PostMapping("/login")
	public String doLogin(@RequestParam("email") String email, @RequestParam("password") String password, Model model) {
		User user = service.getUserByEmail(email);

		if (user == null) {
			model.addAttribute("error", "Invalid email or password!");
			return "Login";
		} else if (!passwordEncoder.matches(password, user.getPassword())) {
			model.addAttribute("error", "Invalid email or password!");
			return "Login";

		}

		SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken(user, null,
				Collections.singleton(new SimpleGrantedAuthority("ROLE_" + user.getRole()))));
		return "redirect:/";
	}

	@GetMapping("/signup")
	public String signupPage(Model model, Authentication authentication) {
		if (authentication != null) {
			return "redirect:/";
		}
		model.addAttribute("user", new User());
		return "signup";
	}

	@PostMapping("/signup")
	public String signup(User user, Authentication authentication) {
		if (user != null) {
			if (service.getUserByEmail(user.getEmail()) != null) {
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
