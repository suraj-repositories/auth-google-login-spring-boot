package com.on08aug24.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.on08aug24.entity.User;
import com.on08aug24.repository.UserRepository;
import com.on08aug24.service.UserService;

@Service
public class UserServiceImpl implements UserService{

	@Autowired
	private UserRepository repository;
	
	
	@Override
	public User saveUser(User user) {
		
		return repository.save(user);
	}

	@Override
	public User getUserById(Long id) {
		return repository.findById(id).orElseThrow(()-> new RuntimeException("User not found!"));
		
	}

	@Override
	public User updateUser(User user) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void deleteUserById(Long id) {
		// TODO Auto-generated method stub
		
	}

}
