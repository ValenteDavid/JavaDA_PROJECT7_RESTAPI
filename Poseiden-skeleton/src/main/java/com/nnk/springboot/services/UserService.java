package com.nnk.springboot.services;

import org.springframework.stereotype.Service;

import com.nnk.springboot.config.SpringSecurityConfig;

@Service
public class UserService {
	
	public String passwordEncoder(String password) {
		return SpringSecurityConfig.passwordEncoder().encode(password);
	}
 
}
