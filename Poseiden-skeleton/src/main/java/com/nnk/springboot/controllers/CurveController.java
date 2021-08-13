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

import com.nnk.springboot.domain.CurvePoint;
import com.nnk.springboot.repositories.CurvePointRepository;

@Controller
public class CurveController {
	private static final Logger log = LoggerFactory.getLogger(CurveController.class);

	@Autowired
	private CurvePointRepository curvePointRepository;

    @RequestMapping("/curvePoint/list")
    public String home(Model model) {
		log.info("GET /curvePoint/list");
		model.addAttribute("curvePoints", curvePointRepository.findAll());
		log.info("GET /curvePoint/list response : {}","curvePoint/list");
        return "curvePoint/list";
    }

    @GetMapping("/curvePoint/add")
    public String addCurvePointForm(CurvePoint bid) {
    	log.info("GET /curvePoint/add called");
		log.info("GET /curvePoint/add response : {}","curvePoint/add");
        return "curvePoint/add";
    }

    @PostMapping("/curvePoint/validate")
    public String validate(@Valid CurvePoint curvePoint, BindingResult result, Model model) {
    	log.info("POST /curvePoint/validate called params : curvePoint {}",curvePoint);
		if (!result.hasErrors()) {
			curvePoint.setCreationDate(new Timestamp(System.currentTimeMillis()));
			curvePointRepository.save(curvePoint);
			model.addAttribute("curvePoints", curvePointRepository.findAll());
			log.info("GET /curvePoint/validate return : {}","redirect:/curvePoint/list");
			return "redirect:/curvePoint/list";
		}
		log.debug(result.getAllErrors().toString());
		log.info("POST /curvePoint/validate response : {}","curvePoint/add");
        return "curvePoint/add";
    }

    @GetMapping("/curvePoint/update/{id}")
    public String showUpdateForm(@PathVariable("id") Integer id, Model model) {
    	log.info("GET /curvePoint/update/{} called",id);
		CurvePoint curvePoint = curvePointRepository.findById(id)
				.orElseThrow(() -> new IllegalArgumentException("Invalid curvePoint Id:" + id));
		model.addAttribute("curvePoint", curvePoint);
		log.info("GET /curvePoint/update/{1} response : {2} ",id,"curvePoint/update");
        return "curvePoint/update";
    }

    @PostMapping("/curvePoint/update/{id}")
    public String updateCurvePoint(@PathVariable("id") Integer id, @Valid CurvePoint curvePoint,
                             BindingResult result, Model model) {
    		log.info("POST /curvePoint/update/{1} called params : curvePoint {2}",id,curvePoint);
    		if (result.hasErrors()) {
    			log.debug(result.getAllErrors().toString());
    			log.info("POST /curvePoint/update/{1} response  : {2}",id,"curvePoint/update");
    			return "curvePoint/update";
    		}
    		
    		curvePoint.setId(id);
    		curvePointRepository.save(curvePoint);
    		model.addAttribute("curvePoints", curvePointRepository.findAll());
    		log.info("POST /curvePoint/update/{1} response  : {2}",id,"redirect:/curvePoint/list");
        return "redirect:/curvePoint/list";
    }

    @GetMapping("/curvePoint/delete/{id}")
    public String deleteCurvePoint(@PathVariable("id") Integer id, Model model) {
		log.info("GET /curvePoint/delete/{} called",id);
		CurvePoint curvePoint = curvePointRepository.findById(id)
				.orElseThrow(() -> new IllegalArgumentException("Invalid curvePoint Id:" + id));
		curvePointRepository.delete(curvePoint);
		model.addAttribute("curvePoints", curvePointRepository.findAll());
		log.info("GET /curvePoint/delete/{1} response : {2}",id,"redirect:/curvePoint/list");
        return "redirect:/curvePoint/list";
    }
}
