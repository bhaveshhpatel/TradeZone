package com.myapps.tradezone.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class JQuote {

  public String Symbol;
  public String LastTradeDate;
  public String Open;
  public String PreviousClose;
  public String Ask;

//more fields declared here

}
