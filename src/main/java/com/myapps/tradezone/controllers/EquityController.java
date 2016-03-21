package com.myapps.tradezone.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.ModelAndView;

/**
 * This is a controller for equity options data. The equity options information is in MongoDB and this
 * controller fetches the information based on the equity symbol entered in the search box.
 *
 * @author Bhavesh Patel
 */
@Controller
public class EquityController {
    @RequestMapping(value = "/equity", method = RequestMethod.POST)
    public ModelAndView getEquityOptionsData(@RequestParam("equitySymbol") String equitySymbol) {
		RestTemplate restTemplate = new RestTemplate();
		String apiKey = "apiKey=AyMLniRR5T888GpU1ib7sM_bgMBaJGXD";
		String query = "{\"Symbol\":\"" + equitySymbol.toUpperCase() + "\"}";
		String url = "https://api.mlab.com/api/1/databases/uoascannerdb/collections/EquityOptionsVolumeData_1";
		String equities = "";
		url += apiKey + "&q=" + query;
		equities = restTemplate.getForObject(url, String.class);
		System.out.println("Equities for " + equitySymbol + " : " + equities);
		return new ModelAndView("index", "users", equities);
    }
}