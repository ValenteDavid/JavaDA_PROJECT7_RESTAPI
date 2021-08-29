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

import com.nnk.springboot.controllers.dto.CurvePointDTOForm;
import com.nnk.springboot.controllers.dto.CurvePointDTOList;
import com.nnk.springboot.domain.CurvePoint;
import com.nnk.springboot.domain.UserRole;
import com.nnk.springboot.domain.UserRole.typeUser;
import com.nnk.springboot.repositories.CurvePointRepository;

@Controller
public class CurveController {
	private static final Logger log = LoggerFactory.getLogger(CurveController.class);

	@Autowired
	private CurvePointRepository curvePointRepository;

	@PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @RequestMapping("/curvePoint/list")
    public String home(Model model, HttpServletRequest httpServletRequest) {
		log.info("GET /curvePoint/list");
		model.addAttribute("curvePoints",getCurvePointList());
		model = allowRole(model, httpServletRequest);
		log.info("GET /curvePoint/list response : {}","curvePoint/list");
        return "curvePoint/list";
    }

	@PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/curvePoint/add")
    public String addCurvePointForm(CurvePointDTOForm curvePointDTOForm) {
    	log.info("GET /curvePoint/add called");
		log.info("GET /curvePoint/add response : {}","curvePoint/add");
        return "curvePoint/add";
    }

	@PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/curvePoint/validate")
    public String validate(@Valid CurvePointDTOForm curvePointDTOForm, BindingResult result, Model model) {
    	log.info("POST /curvePoint/validate called params : curvePoint {}",curvePointDTOForm);
		if (!result.hasErrors()) {
			CurvePoint curvePoint = CurvePointDTOForm.DTOToDomain(new CurvePoint(), curvePointDTOForm);
			curvePoint.setCreationDate(new Timestamp(System.currentTimeMillis()));
			curvePointRepository.save(curvePoint);
			model.addAttribute("curvePoints",getCurvePointList());
			
			log.info("GET /curvePoint/validate return : {}","redirect:/curvePoint/list");
			return "redirect:/curvePoint/list";
		}
		log.debug(result.getAllErrors().toString());
		log.info("POST /curvePoint/validate response : {}","curvePoint/add");
        return "curvePoint/add";
    }

	@PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/curvePoint/update/{id}")
    public String showUpdateForm(@PathVariable("id") Integer id, Model model) {
    	log.info("GET /curvePoint/update/{} called",id);
		CurvePoint curvePoint = curvePointRepository.findById(id)
				.orElseThrow(() -> new IllegalArgumentException("Invalid curvePoint Id:" + id));
		model.addAttribute("curvePointDTOForm", CurvePointDTOForm.DomaintoDTO(curvePoint));
		log.info("GET /curvePoint/update/{} response : {} ",id,"curvePoint/update");
        return "curvePoint/update";
    }

	@PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/curvePoint/update/{id}")
    public String updateCurvePoint(@PathVariable("id") Integer id, @Valid CurvePointDTOForm curvePointDTOForm,
                             BindingResult result, Model model) {
    		log.info("POST /curvePoint/update/{} called params : curvePoint {}",id,curvePointDTOForm);
    		if (result.hasErrors()) {
    			log.debug(result.getAllErrors().toString());
    			log.info("POST /curvePoint/update/{} response  : {}",id,"curvePoint/update");
    			return "curvePoint/update";
    		}
    		CurvePoint curvePoint = CurvePointDTOForm.DTOToDomain(curvePointRepository.getById(id), curvePointDTOForm);
    		curvePointRepository.save(curvePoint);
    		model.addAttribute("curvePoints", getCurvePointList());
    		log.info("POST /curvePoint/update/{} response  : {}",id,"redirect:/curvePoint/list");
        return "redirect:/curvePoint/list";
    }

	@PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/curvePoint/delete/{id}")
    public String deleteCurvePoint(@PathVariable("id") Integer id, Model model) {
		log.info("GET /curvePoint/delete/{} called",id);
		CurvePoint curvePoint = curvePointRepository.findById(id)
				.orElseThrow(() -> new IllegalArgumentException("Invalid curvePoint Id:" + id));
		curvePointRepository.delete(curvePoint);
		model.addAttribute("curvePoints", getCurvePointList());
		log.info("GET /curvePoint/delete/{} response : {}",id,"redirect:/curvePoint/list");
        return "redirect:/curvePoint/list";
    }
	
	private Collection<CurvePointDTOList> getCurvePointList() {
		return curvePointRepository.findAll().stream()
				.map(curvePoints -> CurvePointDTOList.DomaintoDTO(curvePoints))
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
