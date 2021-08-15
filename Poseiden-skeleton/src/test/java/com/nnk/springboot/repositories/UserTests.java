package com.nnk.springboot.repositories;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.nnk.springboot.Application;
import com.nnk.springboot.domain.User;

@SpringBootTest(classes = Application.class)
public class UserTests {

	@Autowired
	private UserRepository userRepository;

	@Test
	public void userTest() {
		User user = new User("User Name", "Password","Full Name", "Role");

		// Save
		user = userRepository.save(user);
		assertNotNull(user.getId());
		assertTrue(user.getUsername().equals("User Name"));

		// Update
		user.setFullname("FullName");
		user = userRepository.save(user);
		assertTrue(user.getFullname().equals("FullName"));

		// Find
		List<User> listResult = userRepository.findAll();
		assertTrue(listResult.size() > 0);

		// Delete
		Integer id = user.getId();
		userRepository.delete(user);
		Optional<User> userList = userRepository.findById(id);
		assertFalse(userList.isPresent());
	}
}
