package com.myapps.tradezone.workers;

import java.net.URI;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import com.myapps.tradezone.models.EquityOptions;
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
	  return yed;
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
