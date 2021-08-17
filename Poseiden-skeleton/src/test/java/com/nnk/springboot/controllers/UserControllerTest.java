package com.nnk.springboot.controllers;

import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import java.util.Optional;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import com.nnk.springboot.domain.User;
import com.nnk.springboot.repositories.UserRepository;
import com.nnk.springboot.services.UserService;

@WebMvcTest(controllers = UserController.class)
@WithMockUser(roles = "ADMIN")
public class UserControllerTest {

	@MockBean
	private UserRepository userRepository;
	@MockBean
	private UserService userService;

	@Autowired
	private MockMvc mockMvc;

	@Test
	public void HomeTest() throws Exception {
		mockMvc.perform(get("/user/list"))
				.andExpect(model().attributeExists("users"))
				.andExpect(view().name("user/list"));
	}

	@Test
	public void addUserTest() throws Exception {
		mockMvc.perform(get("/user/add"))
				.andExpect(view().name("user/add"));
	}

	@Test
	public void validateTest() throws Exception {
		User user = new User("User Name", "Password7&","Full Name", "Role");
		when(userRepository.save(user)).thenReturn(user);
		when(userRepository.findAll()).thenReturn(anyList());
		when(userService.passwordEncoder(user.getPassword())).thenReturn(user.getPassword());

		mockMvc.perform(post("/user/validate").with(csrf())
				.param("username", "User Name")
				.param("password", "Password7&")
				.param("fullname", "Full Name")
				.param("role", "Role")
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(view().name("redirect:/user/list"));
	}
	
	@Test
	public void validate_EmptyUsername_Test() throws Exception {
		mockMvc.perform(post("/user/validate").with(csrf())
				.param("username", "")
				.param("password", "Password7&")
				.param("fullname", "Full Name")
				.param("role", "Role")
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(view().name("user/add"));
	}
	
	@Test
	public void validate_EmptyPassword_Test() throws Exception {
		mockMvc.perform(post("/user/validate").with(csrf())
				.param("username", "User Name")
				.param("password", "")
				.param("fullname", "Full Name")
				.param("role", "Role")
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(view().name("user/add"));
	}
	
	@Test
	public void validate_EmptyFullname_Test() throws Exception {
		mockMvc.perform(post("/user/validate").with(csrf())
				.param("username", "User Name")
				.param("password", "Password7&")
				.param("fullname", "")
				.param("role", "Role")
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(view().name("user/add"));
	}
	
	@Test
	public void validate_EmptyRole_Test() throws Exception {
		mockMvc.perform(post("/user/validate").with(csrf())
				.param("username", "User Name")
				.param("password", "Password7&")
				.param("fullname", "Full Name")
				.param("role", "")
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(view().name("user/add"));
	}


	@Test
	public void showUpdateFormTest() throws Exception {
		Integer id = 1;
		when(userRepository.findById(id)).thenReturn(
				Optional.of(new User ("User Name", "Password7&","Full Name", "Role")));

		mockMvc.perform(get("/user/update/{0}", id))
				.andExpect(model().attributeExists("user"))
				.andExpect(view().name("user/update"));
	}

	@Test
	public void showUpdateForm_NotFoundId_Test() throws Exception {
		Integer id = 1;
		when(userRepository.findById(id)).thenReturn(Optional.empty());

		Assertions
				.assertThatThrownBy(() -> mockMvc.perform(get("/user/update/{0}", id)))
				.hasCause(new IllegalArgumentException("Invalid user Id:" + id));
	}


	@Test
	public void updateUser_Test() throws Exception {
		Integer id = 1;
		User user = new User("UserName", "Password7&","Full Name", "Role");
		user.setId(id);
		when(userRepository.save(user)).thenReturn(user);
		when(userRepository.findAll()).thenReturn(anyList());
		when(userService.passwordEncoder(user.getPassword())).thenReturn(user.getPassword());

		mockMvc.perform(post("/user/update/{id}", id).with(csrf())
				.param("id", "1")
				.param("username", "UserName")
				.param("password", "Password7&")
				.param("fullname", "Full Name")
				.param("role", "Role")
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(view().name("redirect:/user/list"));
	}

	@Test
	public void updateUser_Test_HasError() throws Exception {
		Integer id = 1;

		mockMvc.perform(post("/user/update/{id}", id).with(csrf())
				.param("id", "1")
				.param("username", "")
				.param("password", "Password7&")
				.param("fullname", "Full Name")
				.param("role", "Role")
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(view().name("user/update"));
	}

	@Test
	public void deleteTest() throws Exception {
		Integer id = 1;
		when(userRepository.findById(id)).thenReturn(
				Optional.of(new User("UserName", "Password7&","Full Name", "Role")));
		when(userRepository.findAll()).thenReturn(anyList());

		mockMvc.perform(get("/user/delete/{0}", id))
				.andExpect(view().name("redirect:/user/list"));

	}

	@Test
	public void deleteTest_NotFoundId_() throws Exception {
		Integer id = 1;
		when(userRepository.findById(id)).thenReturn(Optional.empty());
		when(userRepository.findAll()).thenReturn(anyList());

		Assertions
				.assertThatThrownBy(() -> mockMvc.perform(get("/user/delete/{0}", id)))
				.hasCause(new IllegalArgumentException("Invalid user Id:" + id));

	}


}
