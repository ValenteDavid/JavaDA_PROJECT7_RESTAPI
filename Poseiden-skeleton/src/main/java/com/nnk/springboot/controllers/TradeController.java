package com.nnk.springboot.controllers;

import java.sql.Timestamp;

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

import com.nnk.springboot.domain.Trade;
import com.nnk.springboot.domain.UserRole;
import com.nnk.springboot.domain.UserRole.typeUser;
import com.nnk.springboot.repositories.TradeRepository;

@Controller
public class TradeController {
	private static final Logger log = LoggerFactory.getLogger(TradeController.class);

	@Autowired
	private TradeRepository tradeRepository;

	@PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @RequestMapping("/trade/list")
    public String home(Model model,HttpServletRequest httpServletRequest){
    	log.info("GET /trade/list");
		model.addAttribute("trades", tradeRepository.findAll());
		model = allowRole(model, httpServletRequest);
		log.info("GET /trade/list response : {}","trade/list");
        return "trade/list";
    }

	@PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/trade/add")
    public String addTradeForm(Trade trade) {
    	log.info("GET /trade/add called");
		log.info("GET /trade/add response : {}","trade/add");
        return "trade/add";
    }

	@PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/trade/validate")
    public String validate(@Valid Trade trade, BindingResult result, Model model) {
    	log.info("POST /trade/validate called params : trade {}",trade);
		if (!result.hasErrors()) {
			//TODO Set UserName
//			trade.setCreationName(user.getUsername());
			trade.setCreationDate(new Timestamp(System.currentTimeMillis()));
			tradeRepository.save(trade);
			model.addAttribute("trades", tradeRepository.findAll());
			log.info("GET /trade/validate return : {}","redirect:/trade/list");
			return "redirect:/trade/list";
		}
		log.debug(result.getAllErrors().toString());
		log.info("POST /trade/validate response : {}","trade/add");
        return "trade/add";
    }

	@PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/trade/update/{id}")
    public String showUpdateForm(@PathVariable("id") Integer id, Model model) {
    	log.info("GET /trade/update/{} called",id);
		Trade trade = tradeRepository.findById(id)
				.orElseThrow(() -> new IllegalArgumentException("Invalid trade Id:" + id));
		model.addAttribute("trade", trade);
		log.info("GET /trade/update/{1} response : {2} ",id,"trade/update");
        return "trade/update";
    }

	@PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/trade/update/{id}")
    public String updateTrade(@PathVariable("id") Integer id, @Valid Trade trade,
                             BindingResult result, Model model) {
    	log.info("POST /trade/update/{1} called params : trade {2}",id,trade);
		if (result.hasErrors()) {
			log.debug(result.getAllErrors().toString());
			log.info("POST /trade/update/{1} response  : {2}",id,"trade/update");
			return "trade/update";
		}
		
		trade.setTradeId(id);
		//TODO Set UserName
//		trade.setRevisionName(user.getUsername());
		trade.setRevisionDate(new Timestamp(System.currentTimeMillis()));
		tradeRepository.save(trade);
		model.addAttribute("trades", tradeRepository.findAll());
		log.info("POST /trade/update/{1} response  : {2}",id,"redirect:/trade/list");
        return "redirect:/trade/list";
    }

	@PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/trade/delete/{id}")
    public String deleteTrade(@PathVariable("id") Integer id, Model model) {
		log.info("GET /trade/delete/{} called",id);
		Trade trade = tradeRepository.findById(id)
				.orElseThrow(() -> new IllegalArgumentException("Invalid trade Id:" + id));
		tradeRepository.delete(trade);
		model.addAttribute("trades", tradeRepository.findAll());
		log.info("GET /trade/delete/{1} response : {2}",id,"redirect:/trade/list");
        return "redirect:/trade/list";
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
