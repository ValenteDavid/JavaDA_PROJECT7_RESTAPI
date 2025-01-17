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

import com.nnk.springboot.controllers.dto.RatingDTOForm;
import com.nnk.springboot.controllers.dto.RatingDTOList;
import com.nnk.springboot.domain.Rating;
import com.nnk.springboot.domain.UserRole;
import com.nnk.springboot.domain.UserRole.typeUser;
import com.nnk.springboot.repositories.RatingRepository;

@Controller
public class RatingController {
	private static final Logger log = LoggerFactory.getLogger(RatingController.class);

	@Autowired
	private RatingRepository ratingRepository;

	@PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @RequestMapping("/rating/list")
    public String home(Model model, HttpServletRequest httpServletRequest){
    	log.info("GET /rating/list");
		model.addAttribute("ratings", getBidListList());
		model = allowRole(model, httpServletRequest);
		log.info("GET /rating/list response : {}","rating/list");
        return "rating/list";
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/rating/add")
    public String addRatingForm(RatingDTOForm ratingDTOForm) {
    	log.info("GET /rating/add called");
		log.info("GET /rating/add response : {}","rating/add");
        return "rating/add";
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/rating/validate")
    public String validate(@Valid RatingDTOForm ratingDTOForm, BindingResult result, Model model) {
    	log.info("POST /rating/validate called params : rating {}",ratingDTOForm);
		if (!result.hasErrors()) {
			Rating rating = RatingDTOForm.DTOToDomain(new Rating(), ratingDTOForm);
			ratingRepository.save(rating);
			model.addAttribute("ratings", getBidListList());
			
			log.info("GET /rating/validate return : {}","redirect:/rating/list");
			return "redirect:/rating/list";
		}else {
			log.debug(result.getAllErrors().toString());
			log.info("POST /rating/validate response : {}","rating/add");
	        return "rating/add";
		}
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/rating/update/{id}")
    public String showUpdateForm(@PathVariable("id") Integer id, Model model) {
    	log.info("GET /rating/update/{} called",id);
		Rating rating = ratingRepository.findById(id)
				.orElseThrow(() -> new IllegalArgumentException("Invalid rating Id:" + id));
		model.addAttribute("ratingDTOForm", RatingDTOForm.DomainToDTO(rating));
		log.info("GET /rating/update/{} response : {} ",id,"rating/update");
        return "rating/update";
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/rating/update/{id}")
    public String updateRating(@PathVariable("id") Integer id, @Valid RatingDTOForm ratingDTOForm,
                             BindingResult result, Model model) {
    	log.info("POST /rating/update/{} called params : rating {}",id,ratingDTOForm);
		if (result.hasErrors()) {
			log.debug(result.getAllErrors().toString());
			log.info("POST /rating/update/{} response  : {}",id,"rating/update");
			return "rating/update";
		}
		Rating rating = RatingDTOForm.DTOToDomain(ratingRepository.getById(id), ratingDTOForm);
		ratingRepository.save(rating);
		model.addAttribute("ratings", getBidListList());
		log.info("POST /rating/update/{} response  : {}",id,"redirect:/rating/list");
        return "redirect:/rating/list";
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/rating/delete/{id}")
    public String deleteRating(@PathVariable("id") Integer id, Model model) {
		log.info("GET /rating/delete/{} called",id);
		Rating rating = ratingRepository.findById(id)
				.orElseThrow(() -> new IllegalArgumentException("Invalid rating Id:" + id));
		ratingRepository.delete(rating);
		model.addAttribute("ratings", getBidListList());
		log.info("GET /rating/delete/{} response : {}",id,"redirect:/rating/list");
        return "redirect:/rating/list";
    }
    
    private Collection<RatingDTOList> getBidListList() {
		return ratingRepository.findAll().stream()
				.map(bidLists -> RatingDTOList.DomainToDTO(bidLists))
				.collect(Collectors.toList());
	}
    
	private Model allowRole(Model model, HttpServletRequest httpServletRequest) {
		UserRole.typeUser userRoleType;
		
		if (httpServletRequest.isUserInRole(UserRole.typeUser.USER.getName())) {
			userRoleType = typeUser.USER;
		}
		else if (httpServletRequest.isUserInRole(UserRole.typeUser.ADMIN.getName())) {
			userRoleType = typeUser.ADMIN;
		}
		else {
			return model;
		}
		
		model.addAttribute("userRole_CREATE",userRoleType.create());
		model.addAttribute("userRole_READ",userRoleType.read());
		model.addAttribute("userRole_UPDATE",userRoleType.update());
		model.addAttribute("userRole_DELETE",userRoleType.delete());
		return model;
	}
}
