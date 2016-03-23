package com.myapps.tradezone.models;

import java.util.List;

public class EquityData {
	private String count;
	private String created;
	private String lang;
	private List<Equity> results;
	
	EquityData() {}
	EquityData(String count, String created, String lang, List<Equity> results) {
		this.count = count;
		this.created = created;
		this.lang = lang;
		this.results = results;
	}
	
	public String getCount() {
		return this.count;
	}
	
	public void setCount(String count) {
		this.count = count;
	}
	
	public String getCreated() {
		return this.created;
	}
	
	public void setCreated(String created) {
		this.created = created;
	}
	
	public String getLang() {
		return this.lang;
	}
	
	public void setLang(String lang) {
		this.lang = lang;
	}
	
	public List<Equity> getResults() {
		return this.results;
	}
	
	public void setResults(List<Equity> results) {
		this.results = results;
	}

}
