package com.nnk.springboot.controllers.dto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

import com.nnk.springboot.domain.User;

public class UserDTOForm {

	private Integer id;
	@NotBlank(message = "Username is mandatory")
	private String username;
	@Pattern(regexp = "(^(?=.*?[A-Z])(?=.*?[a-z])(?=.*?[0-9])(?=.*?[#?!@$%^&*-]).{8,}$)|()", message = "Wrong format")
	private String password;
	@NotBlank(message = "FullName is mandatory")
	private String fullname;
	@NotBlank(message = "Role is mandatory")
	private String role;

	public UserDTOForm() {
	}

	public UserDTOForm(String username, String password, String fullname) {
		this.username = username;
		this.password = password;
		this.fullname = fullname;
	}

	public UserDTOForm(String username, String password, String fullname, String role) {
		this.username = username;
		this.password = password;
		this.fullname = fullname;
		this.role = role;
	}

	public UserDTOForm(Integer id, String username,String password,String fullname,String role) {
		this.id = id;
		this.username = username;
		this.password = password;
		this.fullname = fullname;
		this.role = role;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public boolean passwordExist() {
		if(password.isEmpty()||password.equals(null)) {
			return false;
		}else {
			return true;
		}
	}
	
	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getFullname() {
		return fullname;
	}

	public void setFullname(String fullname) {
		this.fullname = fullname;
	}

	public String getRole() {
		return role;
	}

	public void setRole(String role) {
		this.role = role;
	}

	@Override
	public String toString() {
		return "User [id=" + id + ", username=" + username + ", fullname=" + fullname
				+ ", role=" + role + "]";
	}
	
	public static User DTOtoDomain(UserDTOForm userDTOForm) {
		User user = new User();
		user.setUsername(userDTOForm.getUsername());
		if (userDTOForm.passwordExist()) {
			user.setPassword(userDTOForm.getPassword());
		}
		user.setFullname(userDTOForm.getFullname());
		user.setRole(userDTOForm.getRole());
		return user;
	}

	public static User DTOtoDomain(User user, UserDTOForm userDTOForm) {
		if (userDTOForm.passwordExist()) {
			user.setPassword(userDTOForm.getPassword());
		}
		user.setFullname(userDTOForm.getFullname());
		return user;
	}

	public static UserDTOForm DomaintoDTO(User user) {
		return new UserDTOForm(user.getId(),user.getUsername(), "", user.getFullname(), user.getRole());
	}

}
