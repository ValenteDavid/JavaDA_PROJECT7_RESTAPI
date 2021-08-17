package com.nnk.springboot.services;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {
//TODO 1.Create user service to load user from database and place in package com.nnk.springboot.services
	
	public String passwordEncoder(String password) {
		 return new BCryptPasswordEncoder().encode(password);
	}
 
}
