package com.myapps.tradezone.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class YEquityResults {
	
	@JsonProperty("quote")
    private YEquityQuote quote;

    public YEquityQuote getQuote ()
    {
        return quote;
    }

    public void setQuote (YEquityQuote quote)
    {
        this.quote = quote;
    }

    @Override
    public String toString()
    {
        return "ClassPojo [quote = "+quote+"]";
    }
}
