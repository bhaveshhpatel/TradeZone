package com.myapps.tradezone.services;

import java.util.List;
import com.myapps.tradezone.models.Alert;

import org.springframework.data.mongodb.repository.MongoRepository;

public interface AlertRepository extends MongoRepository<Alert, String> {

    public List<Alert> findBySymbol(String symbol);
    
    public List<Alert> findByOrderByDateDesc();

}
