package com.myapps.tradezone.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.AbstractWebSocketMessageBrokerConfigurer;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;

/**
 * This class is for web socket configuration. Web socket is used to pass messages from server to the client.
 * 
 * @author Bhavesh Patel
 */
@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig extends AbstractWebSocketMessageBrokerConfigurer {

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        // the endpoint for websocket connections
        registry.addEndpoint("/stomp").withSockJS();
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        // use the /topic prefix for outgoing WebSocket communication
        config.enableSimpleBroker("/t"); 
        config.setApplicationDestinationPrefixes("/app");
    }
}
