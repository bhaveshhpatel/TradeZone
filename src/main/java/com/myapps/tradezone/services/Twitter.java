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

    @JmsListener(destination = "simple.queue")
    public void tweet(String tweetJson) {
        // sends the message to /t/twitter
        this.template.convertAndSend("/t/twitter", tweetJson.toString());
        System.out.println("Tweet is: " + tweetJson.toString());
    }

}
