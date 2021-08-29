package com.nnk.springboot.controllers;

import java.util.Collection;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.nnk.springboot.controllers.dto.UserDTOForm;
import com.nnk.springboot.controllers.dto.UserDTOList;
import com.nnk.springboot.domain.User;
import com.nnk.springboot.repositories.UserRepository;
import com.nnk.springboot.services.UserService;

@Controller
public class UserController {
	private static final Logger log = LoggerFactory.getLogger(UserController.class);

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private UserService userService;

	@PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
	@GetMapping("/user/home")
	public String home(HttpServletRequest httpServletRequest, Model model) {
		log.info("GET /user/home called");
		if (httpServletRequest.isUserInRole("ADMIN")) {
			model.addAttribute("users", getUserList());
			log.info("GET /user/list response : {}", "user/list");
			return "redirect:/user/list";
		} else {
			Integer id = userRepository.findByUsername(httpServletRequest.getUserPrincipal().getName()).get().getId();
			log.info("GET /user/list response : {}", "user/update/" + id);
			return "redirect:/user/update/" + id;
		}
	}

	@PreAuthorize("hasRole('ADMIN')")
	@RequestMapping("/user/list")
	public String home(Model model) {
		log.info("GET /user/list called");
		model.addAttribute("users", getUserList());
		log.info("GET /user/list response : {}", "user/list");
		return "user/list";
	}

	@PreAuthorize("hasRole('ADMIN')")
	@GetMapping("/user/add")
	public String addUser(UserDTOForm userDTOForm) {
		log.info("GET /user/add called");
		log.info("GET /user/add response : {}", "user/add");
		return "user/add";
	}

	@PreAuthorize("hasRole('ADMIN')")
	@PostMapping("/user/validate")
	public String validate(@Valid UserDTOForm userDTOForm, BindingResult result, Model model) {
		log.info("POST /user/validate called params : user {}", userDTOForm);
		if (!result.hasErrors() && !userRepository.findByUsername(userDTOForm.getUsername()).isPresent()
				&& userDTOForm.passwordExist()) {
			User user = UserDTOForm.DTOtoDomain(userDTOForm);
			user.setPassword(userService.passwordEncoder(user.getPassword()));
			userRepository.save(user);
			model.addAttribute("users", getUserList());
			log.info("GET /user/validate return : {}", "redirect:/user/list");
			return "redirect:/user/list";
		}
		log.debug(result.getAllErrors().toString());
		log.info("POST /user/validate response : {}", "user/add");
		return "user/add";
	}

	@PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
	@GetMapping("/user/update/{id}")
	public String showUpdateForm(@PathVariable("id") Integer id, Model model,
			HttpServletRequest httpServletRequest) {
		log.info("GET /user/update/{} called", id);
		
		User user = userRepository.findById(id)
				.orElseThrow(() -> new IllegalArgumentException("Invalid user Id:" + id));
		user.setPassword("");

		Integer userId = userRepository.findByUsername(httpServletRequest.getUserPrincipal().getName()).get().getId();
		if (id.compareTo(userId) != 0 && !httpServletRequest.isUserInRole("ADMIN")) {
			log.warn("id different : Have : {} Expect : {}", userId, id);
			log.warn("GET /user/update/{} response : {} ", id, "redirect:/user/update/" + userId);
			return "redirect:/user/update/" + userId;
		}

		model.addAttribute("userDTOForm", UserDTOForm.DomaintoDTO(user));
		
		if(httpServletRequest.isUserInRole("ADMIN")) {
			model.addAttribute("allowRole", true);
		}else {
			model.addAttribute("allowRole", false);
		}
		log.info("GET /user/update/{} response : {} ", id, "user/update");
		return "user/update";
	}

	@PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
	@PostMapping("/user/update/{id}")
	public String updateUser(@PathVariable("id") Integer id, @Valid UserDTOForm userDTOForm, BindingResult result, Model model,
			HttpServletRequest httpServletRequest) {
		log.info("POST /user/update/{} called params : user {}", id, userDTOForm);

		if (result.hasErrors()) {
			log.debug(result.getAllErrors().toString());
			log.info("POST /user/update/{} response  : {}", id, "user/update");
			return "user/update";
		}
		
		Integer userId = userRepository.findByUsername(httpServletRequest.getUserPrincipal().getName()).get()
				.getId();
		
		if (id.compareTo(userId) != 0 && !httpServletRequest.isUserInRole("ADMIN")) {
			log.warn("id different : Have : {} Expect : {}", userId, id);
			log.warn("GET /user/update/{} response : {} ", id, "redirect:/user/update/" + userId);
			return "redirect:/user/update/" + userId;
		}
		
		User user = UserDTOForm.DTOtoDomain(
				userRepository.findByUsername(httpServletRequest.getUserPrincipal().getName()).get(),
				userDTOForm);
		
		if (!httpServletRequest.isUserInRole("ADMIN")) {
			user.setRole(userRepository.findByUsername(httpServletRequest.getUserPrincipal().getName()).get().getRole());
		}else {
			switch (userDTOForm.getRole()) {
			case "USER":
				user.setRole(userDTOForm.getRole());
				break;
			case "ADMIN":
				user.setRole(userDTOForm.getRole());
				break;
			default:
				log.warn("Error assign role");
				return "user/update";
			}
		}
		
		if(userDTOForm.passwordExist()) {
			user.setPassword(userService.passwordEncoder(user.getPassword()));
		}
		
		userRepository.save(user);
		model.addAttribute("users", getUserList());
		log.info("POST /user/update/{} response  : {}", id, "redirect:/user/home");
		return "redirect:/user/home";
	}

	@PreAuthorize("hasRole('ADMIN')")
	@GetMapping("/user/delete/{id}")
	public String deleteUser(@PathVariable("id") Integer id, Model model) {
		log.info("GET /user/delete/{} called", id);
		User user = userRepository.findById(id)
				.orElseThrow(() -> new IllegalArgumentException("Invalid user Id:" + id));
		userRepository.delete(user);
		model.addAttribute("users", getUserList());
		log.info("GET /user/delete/{} response : {}", id, "redirect:/user/list");
		return "redirect:/user/list";
	}

	private Collection<UserDTOList> getUserList() {
		return userRepository.findAll().stream()
				.map(users -> UserDTOList.DomainToDTO(users))
				.collect(Collectors.toList());
	}

}
