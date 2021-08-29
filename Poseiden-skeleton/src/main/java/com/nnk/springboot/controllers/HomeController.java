package com.nnk.springboot.controllers;

import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class HomeController {

	@RequestMapping("/")
	public String home(HttpServletRequest httpServletRequest, Model model) 	{
		if (httpServletRequest.isUserInRole("ADMIN")) {
			return "redirect:/user/list";
		} else if (httpServletRequest.isUserInRole("USER")) {
			return "redirect:/bidList/list";
		} else {
			return "redirect:/login";
		}
	}

}
