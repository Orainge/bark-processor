package com.orainge.bark_processor.server.forward.server.processor;

import com.orainge.websocket_forward.vo.ExchangeMessage;

public interface OnMessageProcessor {
    void process(String clientId, ExchangeMessage<?> exchangeMessage);
}
