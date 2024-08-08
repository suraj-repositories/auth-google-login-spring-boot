package com.on08aug24.service;

import com.on08aug24.entity.User;

public interface UserService {

	User saveUser(User user);
	
	User getUserById(Long id);
	
	User updateUser(User user);
	
	void deleteUserById(Long id);
	
}
