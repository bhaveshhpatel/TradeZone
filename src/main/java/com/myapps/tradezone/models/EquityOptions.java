package com.myapps.tradezone.models;

import org.springframework.data.annotation.Id;

public class EquityOptions {
	@Id
	private String id;
	private String symbol;
	private String equityName;
	private String call;
	private String put;
	private String total;
	private String days;
	private String totalAvgDaily;
	private String callAvgDaily;
	private String putAvgDaily;
	
	EquityOptions( String symbol, String equityName, String call, String put, String total, String days,
			String totalAvgDaily, String callAvgDaily, String putAvgDaily) {
		this.symbol = symbol;
		this.equityName = equityName;
		this.call = call;
		this.put = put;
		this.total = total;
		this.days = days;
		this.totalAvgDaily = totalAvgDaily;
		this.callAvgDaily = callAvgDaily;
		this.putAvgDaily = putAvgDaily;
	}
	
	public String getSymbol() {
		return this.symbol;
	}
	
	public String getEquityName() {
		return this.equityName;
	}
	
	public String getTotalAvgDaily() {
		return this.totalAvgDaily;
	}
}
