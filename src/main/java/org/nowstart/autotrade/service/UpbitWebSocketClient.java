package org.nowstart.autotrade.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import java.net.URI;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.nowstart.autotrade.data.dto.CandleDto;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.BinaryMessage;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketHttpHeaders;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.client.WebSocketClient;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.handler.AbstractWebSocketHandler;

@Slf4j
@Service
@RequiredArgsConstructor
public class UpbitWebSocketClient {

    private final UpbitAuthService upbitAuthService;
    private final ObjectMapper objectMapper;
    private final CandleCacheService candleCacheService;
    private final StrategyService strategyService;
    private final WebSocketClient webSocketClient = new StandardWebSocketClient();

    @PostConstruct
    public void connect() {
        String jwtToken = upbitAuthService.createJwt();

        WebSocketHttpHeaders headers = new WebSocketHttpHeaders();
        headers.add("Authorization", "Bearer " + jwtToken);

        try {
            webSocketClient.execute(new AbstractWebSocketHandler() {
                @Override
                public void afterConnectionEstablished(WebSocketSession session) throws Exception {
                    log.info("Connected to Upbit WebSocket");
                    String subscribeMessage = "[{\"ticket\":\"test\"},{\"type\":\"ticker\",\"codes\":[\"KRW-BTC\"]}]";
                    session.sendMessage(new TextMessage(subscribeMessage));
                }

                @Override
                protected void handleTextMessage(WebSocketSession session, TextMessage message) {
                    processMessage(message.getPayload());
                }

                @Override
                protected void handleBinaryMessage(WebSocketSession session, BinaryMessage message) {
                    ByteBuffer payload = message.getPayload();
                    processMessage(new String(payload.array(), StandardCharsets.UTF_8));
                }

                @Override
                public void handleTransportError(WebSocketSession session, Throwable exception) {
                    log.error("Upbit WebSocket error", exception);
                }
            }, headers, URI.create("wss://api.upbit.com/websocket/v1"));
        } catch (Exception e) {
            log.error("Failed to connect to Upbit WebSocket", e);
        }
    }

    private void processMessage(String text) {
        try {
            CandleDto candle = objectMapper.readValue(text, CandleDto.class);
            if (candle.getCode() != null) {
                candleCacheService.addCandle(candle.getCode(), candle);
                strategyService.execute(candle.getCode());
            }
        } catch (Exception e) {
            log.error("Error parsing Upbit message: {}", text, e);
        }
    }
}
