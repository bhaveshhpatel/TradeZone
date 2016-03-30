package com.myapps.tradezone;

import javax.jms.ConnectionFactory;


import org.apache.activemq.ActiveMQConnectionFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;

import com.myapps.tradezone.listeners.*;

/**
 * This is the main class for the Trade Zone application. It also starts the Twitter listener to listen to tweets.
 * 
 * @author Bhavesh Patel
 */
@SpringBootApplication
public class TradeZoneApplication {
	
private static final String JMS_BROKER_URL = "vm://embedded?broker.persistent=false,useShutdownHook=false";
    
@Bean
    public ConnectionFactory connectionFactory() {
        return new ActiveMQConnectionFactory(JMS_BROKER_URL);
    }
    
    public static void main(String[] args) {
		ConfigurableApplicationContext context = SpringApplication.run(TradeZoneApplication.class, args);
		TwitterListener twitterListener = context.getBean(TwitterListener.class);
		twitterListener.initConfiguration();
		
	}
}
