package com.on14june.service;

import java.util.List;

import com.on14june.entity.User;

public interface UserService {
	
	List<User> getAllUsers();
	
	User getUserById(String id);
	
	User saveUser(User user);
	 
	User getUserByEmail(String email);
	
	
}
