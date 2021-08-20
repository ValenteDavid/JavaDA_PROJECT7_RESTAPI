package com.nnk.springboot.controllers;

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

import com.nnk.springboot.domain.RuleName;
import com.nnk.springboot.domain.UserRole;
import com.nnk.springboot.domain.UserRole.typeUser;
import com.nnk.springboot.repositories.RuleNameRepository;

@Controller
public class RuleNameController {
	private static final Logger log = LoggerFactory.getLogger(RuleNameController.class);

	@Autowired
	private RuleNameRepository ruleNameRepository;

	@PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @RequestMapping("/ruleName/list")
    public String home(Model model,HttpServletRequest httpServletRequest) {
		log.info("GET /ruleName/list");
		model.addAttribute("ruleNames", ruleNameRepository.findAll());
		model = allowRole(model, httpServletRequest);
		log.info("GET /ruleName/list response : {}","ruleName/list");
        return "ruleName/list";
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/ruleName/add")
    public String addRuleNameForm(RuleName bid) {
    	log.info("GET /ruleName/add called");
		log.info("GET /ruleName/add response : {}","ruleName/add");
        return "ruleName/add";
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/ruleName/validate")
    public String validate(@Valid RuleName ruleName, BindingResult result, Model model) {
    	log.info("POST /ruleName/validate called params : ruleName {}",ruleName);
		if (!result.hasErrors()) {
			ruleNameRepository.save(ruleName);
			model.addAttribute("ruleNames", ruleNameRepository.findAll());
			log.info("GET /ruleName/validate return : {}","redirect:/ruleName/list");
			return "redirect:/ruleName/list";
		}
		log.debug(result.getAllErrors().toString());
		log.info("POST /ruleName/validate response : {}","ruleName/add");
        return "ruleName/add";
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/ruleName/update/{id}")
    public String showUpdateForm(@PathVariable("id") Integer id, Model model) {
    	log.info("GET /ruleName/update/{} called",id);
		RuleName ruleName = ruleNameRepository.findById(id)
				.orElseThrow(() -> new IllegalArgumentException("Invalid ruleName Id:" + id));
		model.addAttribute("ruleName", ruleName);
		log.info("GET /ruleName/update/{1} response : {2} ",id,"ruleName/update");
        return "ruleName/update";
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/ruleName/update/{id}")
    public String updateRuleName(@PathVariable("id") Integer id, @Valid RuleName ruleName,
                             BindingResult result, Model model) {
    		log.info("POST /ruleName/update/{1} called params : ruleName {2}",id,ruleName);
    		if (result.hasErrors()) {
    			log.debug(result.getAllErrors().toString());
    			log.info("POST /ruleName/update/{1} response  : {2}",id,"ruleName/update");
    			return "ruleName/update";
    		}
    		
    		ruleName.setId(id);
    		ruleNameRepository.save(ruleName);
    		model.addAttribute("ruleNames", ruleNameRepository.findAll());
    		log.info("POST /ruleName/update/{1} response  : {2}",id,"redirect:/ruleName/list");
        return "redirect:/ruleName/list";
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/ruleName/delete/{id}")
    public String deleteRuleName(@PathVariable("id") Integer id, Model model) {
		log.info("GET /ruleName/delete/{} called",id);
		RuleName ruleName = ruleNameRepository.findById(id)
				.orElseThrow(() -> new IllegalArgumentException("Invalid ruleName Id:" + id));
		ruleNameRepository.delete(ruleName);
		model.addAttribute("ruleNames", ruleNameRepository.findAll());
		log.info("GET /ruleName/delete/{1} response : {2}",id,"redirect:/ruleName/list");
        return "redirect:/ruleName/list";
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
