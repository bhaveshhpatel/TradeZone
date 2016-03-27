package com.myapps.tradezone.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class YEquityData
{
	@JsonProperty("query")
	private YEquityQuery query;

    public YEquityQuery getQuery ()
    {
        return query;
    }

    public void setQuery (YEquityQuery query)
    {
        this.query = query;
    }

    @Override
    public String toString()
    {
        return "ClassPojo [query = "+query+"]";
    }
}
