package com.on14june.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.on14june.entity.User;

public interface UserRepository extends JpaRepository<User, String>{
	
	User findByEmail(String email);
}
