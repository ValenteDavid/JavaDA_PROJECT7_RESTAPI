package com.nnk.springboot.controllers.dto;

import javax.validation.constraints.NotBlank;

import com.nnk.springboot.domain.User;

public class UserDTOList {
	private Integer id;
	@NotBlank(message = "Username is mandatory")
	private String username;
	@NotBlank(message = "FullName is mandatory")
	private String fullname;
	@NotBlank(message = "Role is mandatory")
	private String role;
	
	public UserDTOList() {
	}

	public UserDTOList(String username,String fullname, String role) {
		this.username = username;
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

	public static UserDTOList DomainToDTO(User user) {
		return new UserDTOList(user.getUsername(), user.getFullname(), user.getRole());
	}

}
