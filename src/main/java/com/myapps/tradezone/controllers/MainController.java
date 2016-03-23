package com.myapps.tradezone.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.ui.ModelMap;

import com.myapps.tradezone.services.*;

/**
 * This is the main controller for the program. It directs the client to the index page of the application.
 *
 * @author Bhavesh Patel
 */
@Controller
public class MainController {

	@Autowired
	private TweetRepository repository;
	
	@Autowired
	private TradeRepository tradeRepository;
	
	@RequestMapping(value = "/", method = RequestMethod.GET)
	public ModelAndView index() {
		ModelMap model = new ModelMap();
		model.addAttribute("tweets", repository.findByOrderByDateDesc());
		model.addAttribute("trades", tradeRepository.findAll());
		return new ModelAndView("index", model);
    }
}
