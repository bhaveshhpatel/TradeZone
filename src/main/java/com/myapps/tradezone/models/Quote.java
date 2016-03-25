package com.myapps.tradezone.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Quote
{
    private String symbol;

    private String AverageDailyVolume;

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

    @Override
    public String toString()
    {
        return "ClassPojo [symbol = "+symbol+", AverageDailyVolume = "+AverageDailyVolume+"]";
    }
}