package com.omniguard.ai.riskdetector.utils.websocket.config;

import com.omniguard.ai.riskdetector.utils.websocket.config.handler.GlobalWebSocketHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;
import com.omniguard.ai.riskdetector.repository.ServerStatusRepository;


@Component
@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {

    @Autowired
    ServerStatusRepository serverStatusRepository;

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(new GlobalWebSocketHandler(serverStatusRepository),"/websocket").setAllowedOrigins("*");
    }
}
