package com.nnk.springboot.controllers;

import java.sql.Timestamp;

import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.nnk.springboot.domain.Trade;
import com.nnk.springboot.repositories.TradeRepository;

@Controller
public class TradeController {
	private static final Logger log = LoggerFactory.getLogger(TradeController.class);

	@Autowired
	private TradeRepository tradeRepository;

    @RequestMapping("/trade/list")
    public String home(Model model){
    	log.info("GET /trade/list");
		model.addAttribute("trades", tradeRepository.findAll());
		log.info("GET /trade/list response : {}","trade/list");
        return "trade/list";
    }

    @GetMapping("/trade/add")
    public String addTradeForm(Trade trade) {
    	log.info("GET /trade/add called");
		log.info("GET /trade/add response : {}","trade/add");
        return "trade/add";
    }

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

    @GetMapping("/trade/update/{id}")
    public String showUpdateForm(@PathVariable("id") Integer id, Model model) {
    	log.info("GET /trade/update/{} called",id);
		Trade trade = tradeRepository.findById(id)
				.orElseThrow(() -> new IllegalArgumentException("Invalid trade Id:" + id));
		model.addAttribute("trade", trade);
		log.info("GET /trade/update/{1} response : {2} ",id,"trade/update");
        return "trade/update";
    }

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
}
