package com.nnk.springboot.integration;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureOrder;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import com.nnk.springboot.Application;
import com.nnk.springboot.domain.User;
import com.nnk.springboot.repositories.UserRepository;

@SpringBootTest(classes = Application.class)
@AutoConfigureMockMvc
@AutoConfigureOrder
public class UserControllerIntegrationTest {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private UserRepository userRepository;

	private int userId;

	@BeforeEach
	void init() {
		User user = new User("User Name", "Password7&", "Full Name", "USER");
		userId = userRepository.save(user).getId();
	}

	@AfterEach
	void close() {
		if (userRepository.findById(userId).isPresent()) {
			userRepository.deleteById(userId);
		}
		if (userRepository.findByUsername("UserName").isPresent()) {
			userRepository.delete(userRepository.findByUsername("UserName").get());
		}
	}

	@Test
	@WithMockUser(authorities = "ADMIN")
	public void homeTest() throws Exception {
		mockMvc.perform(get("/user/list"))
				.andExpect(model().attributeExists("users"))
				.andExpect(view().name("user/list"));
	}

	@Test
	@WithMockUser(authorities = "ADMIN")
	public void addUserTest() throws Exception {
		mockMvc.perform(get("/user/add"))
				.andExpect(view().name("user/add"));
	}

	@Test
	@WithMockUser(authorities = "ADMIN",  username = "adminTEST")
	public void validateTest() throws Exception {
		mockMvc.perform(post("/user/validate").with(csrf())
				.param("username", "UserName")
				.param("password", "Password7&")
				.param("fullname", "Full Name")
				.param("role", "USER")
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(view().name("redirect:/user/list"));
	}

	@Test
	@WithMockUser(authorities = "ADMIN", username = "adminTEST")
	public void showUpdateFormTest() throws Exception {
		mockMvc.perform(get("/user/update/{0}", userId))
				.andExpect(model().attributeExists("userDTOForm"))
				.andExpect(view().name("user/update"));
	}

	@Test
	@WithMockUser(authorities = "ADMIN", username = "adminTEST")
	public void updateUser_Test() throws Exception {
		mockMvc.perform(post("/user/update/{0}", userId).with(csrf())
				.param("username", "AdminName")
				.param("password", "Password7&")
				.param("fullname", "FullName")
				.param("role", "ADMIN")
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(view().name("redirect:/user/home"));
	}

	@Test
	@WithMockUser(authorities = "ADMIN")
	public void deleteTest() throws Exception {
		mockMvc.perform(get("/user/delete/{0}", userId))
				.andExpect(view().name("redirect:/user/list"));
	}
}
