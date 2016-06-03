package com.myapps.tradezone.workers;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import com.myapps.tradezone.models.EquityOptions;
import com.myapps.tradezone.models.GoogleOptionsData;
import com.myapps.tradezone.models.YEquityData;

@Component
@Configurable
public class TradeWorker {
	   
	   @Autowired
	   private MongoTemplate mongoTemplate;

	   public static final String COLLECTION_NAME_0 = "Equity_Options_Volume_0";

	   public static final String COLLECTION_NAME_1 = "Equity_Options_Volume_1";

	   public static final String COLLECTION_NAME_2 = "Equity_Options_Volume_2";
	
	public YEquityData getYEquityData(String symbol) {
		RestTemplate rTemplate = new RestTemplate();
	    String env = "http://datatables.org/alltables.env";
	  String symbolString = "\"" + symbol + "\"";
	  String fmt="json";
	  String queryStr = "SELECT * from yahoo.finance.quotes where symbol = ";
	  UriComponents uriComponents =
			    UriComponentsBuilder.fromUriString("http://query.yahooapis.com/v1/public/yql?q={qid}{symbol}&format={fmt}&env={senv}").build()
			    .expand(queryStr, symbolString, fmt, env).encode();
	  URI uri = uriComponents.toUri();
	  System.out.println("URL: " + uri.toString());
	  YEquityData yed = rTemplate.getForObject(uri, YEquityData.class);
	  String test = getOpenInterest(symbol);
	  return yed;
	}
	
	public String getOpenInterest(String symbol) {
	    String jsonString = "";
	    RestTemplate rTemplate = new RestTemplate();
	    UriComponents uriComponents =
			    UriComponentsBuilder.fromUriString("http://www.google.com/finance/option_chain?q={sym}&expd={d}&expm={m}&expy={y}&output=json").build()
			    .expand("SWFT", "15", "04", "2016").encode();
	    URI uri = uriComponents.toUri();
	    try {
	    URL url = new URL(uri.toString());
	    BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream()));
	    String s = null;
	    
	    while ((s = reader.readLine()) != null) {
	    jsonString = s.replaceAll("(\\w+:)(\\d+\\.?\\d*)", "$1\"$2\"");
	    jsonString = jsonString.replaceAll("(\\w+):", "\"$1\":");
	    }
	    } catch (IOException io) {
	    	
	    }
	    return jsonString;
	}
	
	public int getAvgDailyOptionsVol(String symbol) {
		int avgDailyVol = 0;
		int count = 0;
		List<EquityOptions> results = null;
		Query query = new Query();
		query.addCriteria(Criteria.where("symbol").is(symbol));
		results = this.mongoTemplate.find(query, EquityOptions.class, COLLECTION_NAME_0);
		if (results != null && !results.isEmpty()) {
			avgDailyVol += Integer.parseInt(((EquityOptions) results.get(0)).getTotalAvgDaily().replace(",", ""));
			count++;
		}
		results = this.mongoTemplate.find(query, EquityOptions.class, COLLECTION_NAME_1);
		if (results != null && !results.isEmpty()) {
			avgDailyVol += Integer.parseInt(((EquityOptions) results.get(0)).getTotalAvgDaily().replace(",", ""));
			count++;
		}
		results = this.mongoTemplate.find(query, EquityOptions.class, COLLECTION_NAME_2);
		if (results != null && !results.isEmpty()) {
			avgDailyVol += Integer.parseInt(((EquityOptions) results.get(0)).getTotalAvgDaily().replace(",", ""));
			count++;
		}
		return avgDailyVol / count;
	}

}
