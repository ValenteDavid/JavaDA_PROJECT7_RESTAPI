package com.nnk.springboot.services;

import org.springframework.stereotype.Service;

import com.nnk.springboot.config.SpringSecurityConfig;

@Service
public class UserService {
//TODO 1.Create user service to load user from database and place in package com.nnk.springboot.services
	
	public String passwordEncoder(String password) {
		return SpringSecurityConfig.passwordEncoder().encode(password);
	}
 
}
