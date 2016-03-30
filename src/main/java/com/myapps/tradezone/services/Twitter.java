package com.myapps.tradezone.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

/**
 * This is the service class which services the application by sending the twitter tweets to the client.
 * The application uses web socket to send the twitter tweets to the client.
 * 
 * @author Bhavesh Patel
 */
@Service
public class Twitter {
	@Autowired
    private SimpMessagingTemplate template;

    @JmsListener(destination = "tweet.queue")
    public void tweet(String tweetJson) {
        // sends the message to /t/twitter
        this.template.convertAndSend("/t/twitter", tweetJson.toString());
        System.out.println("Tweet is: " + tweetJson.toString());
    }

    @JmsListener(destination = "trade.queue")
    public void trade(String tradeJson) {
        // sends the message to /t/twitter
        this.template.convertAndSend("/t/trade", tradeJson.toString());
        System.out.println("Trade is: " + tradeJson.toString());
    }

    @JmsListener(destination = "equity.queue")
    public void equity(String equityJson) {
        // sends the message to /t/twitter
        this.template.convertAndSend("/t/equity", equityJson.toString());
        System.out.println("Equity is: " + equityJson.toString());
    }

}
