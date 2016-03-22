package com.myapps.tradezone.services;

import java.util.List;
import com.myapps.tradezone.models.Trade;

import org.springframework.data.mongodb.repository.MongoRepository;

public interface TradeRepository extends MongoRepository<Trade, String> {

    public List<Trade> findBySymbol(String symbol);

}