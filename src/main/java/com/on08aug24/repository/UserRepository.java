package com.on08aug24.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.on08aug24.entity.User;

public interface UserRepository extends JpaRepository<User, Long>{

}
