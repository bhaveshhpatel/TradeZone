package com.myapps.tradezone.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class YEquityQuery {
    
	@JsonProperty("results")
	private YEquityResults results;

    private String count;

    private String created;

    private String lang;

    public YEquityResults getResults ()
    {
        return results;
    }

    public void setResults (YEquityResults results)
    {
        this.results = results;
    }

    public String getCount ()
    {
        return count;
    }

    public void setCount (String count)
    {
        this.count = count;
    }

    public String getCreated ()
    {
        return created;
    }

    public void setCreated (String created)
    {
        this.created = created;
    }

    public String getLang ()
    {
        return lang;
    }

    public void setLang (String lang)
    {
        this.lang = lang;
    }

    @Override
    public String toString()
    {
        return "ClassPojo [results = "+results+", count = "+count+", created = "+created+", lang = "+lang+"]";
    }
}
