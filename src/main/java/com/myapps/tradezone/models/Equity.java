package com.myapps.tradezone.models;

public class Equity {
	private String symbol;
	private String AverageDailyVolume;
	
	Equity(){}
	Equity (String symbol, String AverageDailyVolume) {
		this.symbol = symbol;
		this.AverageDailyVolume = AverageDailyVolume;
	}
	
	public String getSymbol() {
		return this.symbol;
	}
	
	public String getAverageDailyVolume() {
		return this.AverageDailyVolume;
	}
}