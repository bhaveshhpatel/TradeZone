package com.myapps.tradezone.services;

import java.util.List;
import com.myapps.tradezone.models.Tweet;

import org.springframework.data.mongodb.repository.MongoRepository;

public interface TweetRepository extends MongoRepository<Tweet, String> {

    public List<Tweet> findByUserName(String userName);
    
    public List<Tweet> findByOrderByDateDesc();

}