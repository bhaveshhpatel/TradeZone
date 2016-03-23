package com.myapps.tradezone.models;

import org.springframework.data.annotation.Id;

public class Trade {
	@Id
	private String id;
	private String date;
	private String symbol;
	private String equityName;
	private String expiration;
	private String strike;
	private String option;
	private String action;
	private String volume;
	private int avgDailyOptionsVol;
	private double multipleOfDailyOptionsVol; 
	
	public String getDate() {
		return this.date;
	}
	
	public void setDate(String date) {
		this.date = date;
	}
	
	public String getSymbol() {
		return this.symbol;
	}
	
	public void setSymbol(String symbol) {
		this.symbol = symbol;
	}
	
	public String getEquityName() {
		return this.equityName;
	}
	
	public void setEquityName(String equityName) {
		this.equityName = equityName;
	}
	
	public String getExpiration() {
		return this.expiration;
	}
	
	public void setExpiration(String expiration) {
		this.expiration = expiration;
	}
	
	public String getStrike() {
		return this.strike;
	}
	
	public void setStrike(String strike) {
		this.strike = strike;
	}
	
	public String getOption() {
		return this.option;
	}
	
	public void setOption(String option) {
		this.option = option;
	}
	
	public String getAction() {
		return this.action;
	}
	
	public void setAction(String action) {
		this.action = action;
	}
	
	public String getVolume() {
		return this.volume;
	}
	
	public void setVolume(String volume) {
		this.volume = volume;
	}
	
	public int getAvgDailyOptionsVol() {
		return this.avgDailyOptionsVol;
	}
	
	public void setAvgDailyOptionsVol(int avgDailyOptionsVol) {
		this.avgDailyOptionsVol = avgDailyOptionsVol;
	}
	
	public double getMultipleOfDailyOptionsVol() {
		return this.multipleOfDailyOptionsVol;
	}
	
	public void setMultipleOfDailyOptionsVol(double multipleOfDailyOptionsVol) {
		this.multipleOfDailyOptionsVol = multipleOfDailyOptionsVol;
	}
	
	public String toString() {
		StringBuilder trade = new StringBuilder("Date: ").append(date)
				.append("Symbol: ").append(symbol)
				.append(" Equity Name: ").append(equityName)
				.append(" Expiration: ").append(expiration)
				.append(" Strike: ").append(strike)
				.append(" Option: ").append(option)
				.append(" Action: ").append(action)
				.append(" Volume: ").append(volume)
				.append(" AvgDailyOptionsVol: ").append(avgDailyOptionsVol)
				.append(" MultipleOfDailyOptionsVol: ").append(multipleOfDailyOptionsVol);
		return trade.toString();
	}

}
