package com.nnk.springboot.controllers;

import java.sql.Timestamp;
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

import com.nnk.springboot.controllers.dto.BidListDTOForm;
import com.nnk.springboot.controllers.dto.BidListDTOList;
import com.nnk.springboot.domain.BidList;
import com.nnk.springboot.domain.UserRole;
import com.nnk.springboot.domain.UserRole.typeUser;
import com.nnk.springboot.repositories.BidListRepository;

@Controller
public class BidListController {
	private static final Logger log = LoggerFactory.getLogger(BidListController.class);

	@Autowired
	private BidListRepository bidListRepository;

	@PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
	@RequestMapping("/bidList/list")
	public String home(Model model, HttpServletRequest httpServletRequest) {
		log.info("GET /bidList/list called");
		model.addAttribute("bidLists", getBidListList());
		model = allowRole(model, httpServletRequest);
		log.info("GET /bidList/list response : {}", "bidList/list");
		return "bidList/list";
	}

	@PreAuthorize("hasRole('ADMIN')")
	@GetMapping("/bidList/add")
	public String addBidForm(BidListDTOForm bidListDTOForm) {
		log.info("GET /bidList/add called");
		log.info("GET /bidList/add response : {}", "bidList/add");
		return "bidList/add";
	}

	@PreAuthorize("hasRole('ADMIN')")
	@PostMapping("/bidList/validate")
	public String validate(@Valid BidListDTOForm bidListDTOForm,BindingResult result, Model model,HttpServletRequest httpServletRequest) {
		log.info("POST /bidList/validate called params : bidList {}", bidListDTOForm);
		if (!result.hasErrors()) {
			BidList bidList = BidListDTOForm.DTOToDomain(new BidList(), bidListDTOForm);
			bidList.setCreationName(httpServletRequest.getRemoteUser());
			bidList.setCreationDate(new Timestamp(System.currentTimeMillis()));
			bidListRepository.save(bidList);
			model.addAttribute("bidLists", getBidListList());

			log.info("GET /bidList/validate return : {}", "redirect:/bidList/list");
			return "redirect:/bidList/list";
		} else {
			log.debug(result.getAllErrors().toString());
			log.info("POST /bidList/validate response : {}", "bidList/add");
			return "bidList/add";
		}
	}

	@PreAuthorize("hasRole('ADMIN')")
	@GetMapping("/bidList/update/{id}")
	public String showUpdateForm(@PathVariable("id") Integer id, Model model) {
		log.info("GET /bidList/update/{} called", id);
		BidList bidList = bidListRepository.findById(id)
				.orElseThrow(() -> new IllegalArgumentException("Invalid bidList Id:" + id));
		model.addAttribute("bidListDTOForm", BidListDTOForm.DomainToDTO(bidList));
		log.info("GET /bidList/update/{} response : {} ", id, "bidList/update");
		return "bidList/update";
	}

	@PreAuthorize("hasRole('ADMIN')")
	@PostMapping("/bidList/update/{id}")
	public String updateBid(@PathVariable("id") Integer id, @Valid BidListDTOForm bidListDTOForm,
			BindingResult result, Model model, HttpServletRequest httpServletRequest) {
		log.info("POST /bidList/update/{} called params : bidList {}", id, bidListDTOForm);
		if (result.hasErrors()) {
			log.debug(result.getAllErrors().toString());
			log.info("POST /bidList/update/{} response  : {}", id, "bidList/update");
			return "bidList/update";
		}
		BidList bidList = BidListDTOForm.DTOToDomain(bidListRepository.getById(id), bidListDTOForm);
		bidList.setRevisionName(httpServletRequest.getRemoteUser());
		bidList.setRevisionDate(new Timestamp(System.currentTimeMillis()));
		bidListRepository.save(bidList);
		model.addAttribute("bidLists", getBidListList());
		log.info("POST /bidList/update/{} response  : {}", id, "redirect:/bidList/list");
		return "redirect:/bidList/list";
	}

	@PreAuthorize("hasRole('ADMIN')")
	@GetMapping("/bidList/delete/{id}")
	public String deleteBid(@PathVariable("id") Integer id, Model model) {
		log.info("GET /bidList/delete/{} called", id);
		BidList bidList = bidListRepository.findById(id)
				.orElseThrow(() -> new IllegalArgumentException("Invalid bidList Id:" + id));
		bidListRepository.delete(bidList);
		model.addAttribute("bidLists", getBidListList());
		log.info("GET /bidList/delete/{} response : {}", id, "redirect:/bidList/list");
		return "redirect:/bidList/list";
	}

	private Collection<BidListDTOList> getBidListList() {
		return bidListRepository.findAll().stream()
				.map(bidLists -> BidListDTOList.DomainToDTO(bidLists))
				.collect(Collectors.toList());
	}

	private Model allowRole(Model model, HttpServletRequest httpServletRequest) {
		UserRole.typeUser userRoleType;

		if (httpServletRequest.isUserInRole(UserRole.typeUser.USER.getName())) {
			userRoleType = typeUser.USER;
		} else if (httpServletRequest.isUserInRole(UserRole.typeUser.ADMIN.getName())) {
			userRoleType = typeUser.ADMIN;
		} else {
			return model;
		}

		model.addAttribute("userRole_CREATE", userRoleType.create());
		model.addAttribute("userRole_READ", userRoleType.read());
		model.addAttribute("userRole_UPDATE", userRoleType.update());
		model.addAttribute("userRole_DELETE", userRoleType.delete());
		return model;
	}

}
