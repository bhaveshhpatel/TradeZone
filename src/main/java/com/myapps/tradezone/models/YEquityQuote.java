package com.myapps.tradezone.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class YEquityQuote {
    
	@JsonProperty("symbol")
	private String symbol;

	@JsonProperty("AverageDailyVolume")
	private String AverageDailyVolume;

	@JsonProperty("YearLow")
	private String YearLow;

	@JsonProperty("YearHigh")
	private String YearHigh;

	@JsonProperty("MarketCapitalization")
	private String MarketCapitalization;

    public String getSymbol ()
    {
        return symbol;
    }

    public void setSymbol (String symbol)
    {
        this.symbol = symbol;
    }

    public String getAverageDailyVolume ()
    {
        return AverageDailyVolume;
    }

    public void setAverageDailyVolume (String AverageDailyVolume)
    {
        this.AverageDailyVolume = AverageDailyVolume;
    }

    public String getYearLow ()
    {
        return YearLow;
    }

    public void setYearLow (String YearLow)
    {
        this.YearLow = YearLow;
    }

    public String getYearHigh ()
    {
        return YearHigh;
    }

    public void setYearHigh (String YearHigh)
    {
        this.YearHigh = YearHigh;
    }

    public String getMarketCapitalization ()
    {
        return MarketCapitalization;
    }

    public void setMarketCapitalization (String MarketCapitalization)
    {
        this.MarketCapitalization = MarketCapitalization;
    }

    @Override
    public String toString()
    {
        return "ClassPojo [symbol = "+symbol+", AverageDailyVolume = "+AverageDailyVolume+"]";
    }
}