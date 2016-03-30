package com.myapps.tradezone.controllers;

import org.springframework.stereotype.Controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.ui.ModelMap;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.myapps.tradezone.models.Trade;
import com.myapps.tradezone.models.YEquityData;
import com.myapps.tradezone.models.YEquityQuote;
import com.myapps.tradezone.services.*;
import com.myapps.tradezone.workers.TradeWorker;

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
	
	ObjectMapper mapper = new ObjectMapper();
	
	@RequestMapping(value = "/", method = RequestMethod.GET)
	public ModelAndView index() {
		ModelMap model = new ModelMap();
		Map<String,String> equityMap = new HashMap<String,String>();
		model.addAttribute("tweets", repository.findByOrderByDateDesc());
		List<Trade> tradeList = tradeRepository.findByOrderByDateDesc();
		model.addAttribute("trades", tradeList);
		String symbol = tradeList.get(0).getSymbol();
		TradeWorker tWorker = new TradeWorker();
		YEquityData yed = tWorker.getYEquityData(symbol);
		YEquityQuote yeq = yed.getQuery().getResults().getQuote();
		equityMap.put("symbol", symbol);
		equityMap.put("name", tradeList.get(0).getEquityName());
		equityMap.put("AverageDailyVolume", yeq.getAverageDailyVolume());
		equityMap.put("YearLow", yeq.getYearLow());
		equityMap.put("YearHigh", yeq.getYearHigh());
		equityMap.put("MarketCapitalization", yeq.getMarketCapitalization());
		equityMap.put("Open", yeq.getOpen());
		equityMap.put("PreviousClose", yeq.getPreviousClose());
		//equityMap.put("AverageOptionsVolume", "" + tWorker.getAvgDailyOptionsVol(symbol));
		model.addAttribute("equity", equityMap);
		return new ModelAndView("index", model);
    }
}
