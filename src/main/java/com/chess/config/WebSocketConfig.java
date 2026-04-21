package com.chess.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.*;

/**
 * WebSocketConfig - Cấu hình WebSocket với STOMP protocol.
 * Sử dụng SockJS để fallback khi WebSocket không khả dụng.
 */
@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        // Kích hoạt in-memory broker với các prefix
        config.enableSimpleBroker("/topic", "/queue");
        // Prefix cho client gửi message lên server
        config.setApplicationDestinationPrefixes("/app");
        // Prefix để gửi tin nhắn riêng tư đến từng user
        config.setUserDestinationPrefix("/user");
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        // Endpoint WebSocket, SockJS fallback
        registry.addEndpoint("/ws").withSockJS();
    }
}
