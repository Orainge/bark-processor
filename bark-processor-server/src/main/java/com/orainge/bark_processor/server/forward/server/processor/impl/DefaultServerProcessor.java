package com.orainge.bark_processor.server.forward.server.processor.impl;

import com.alibaba.fastjson.JSON;
import com.orainge.bark_processor.server.config.DebugConfig;
import com.orainge.bark_processor.server.forward.server.processor.OnMessageProcessor;
import com.orainge.bark_processor.server.forward.server.util.WebsocketServerUtil;
import com.orainge.websocket_forward.vo.ExchangeMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class DefaultServerProcessor implements OnMessageProcessor {
    @Autowired
    private WebsocketServerUtil websocketServerUtil;

    @Autowired
    private DebugConfig debugConfig;

    @Override
    public void process(String clientId, ExchangeMessage<?> exchangeMessage) {
        if (debugConfig.isEnableDebug()) {
            log.info("[Bark Processor 服务端] 收到客户端请求: " + JSON.toJSONString(exchangeMessage));
        }

        if (exchangeMessage.isRequireResponse()) {
            ExchangeMessage<String> replyMessage = new ExchangeMessage<>();
            replyMessage.setBody("OK"); // 回复消息
            websocketServerUtil.send(clientId, replyMessage);
        }
    }
}
