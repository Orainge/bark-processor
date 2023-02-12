package com.orainge.bark_processor.server.process.forwarder.impl;

import com.orainge.bark_processor.server.forward.server.config.WebsocketClientConfig;
import com.orainge.bark_processor.server.forward.server.util.WebsocketServerUtil;
import com.orainge.bark_processor.server.process.forwarder.Forwarder;
import com.orainge.websocket_forward.vo.ExchangeMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

/**
 * 统一通知推送服务 Forwarder
 */
@Component
@Slf4j
public class ForwardServerForwarder implements Forwarder {
    @Autowired
    private WebsocketServerUtil websocketServerUtil;

    @Autowired
    private WebsocketClientConfig websocketClientConfig;

    @Override
    public void process(Map<String, String> formDataMap, HttpServletRequest request, HttpServletResponse response) {
        try {
            // 给每个 ID 的客户端发送消息，不管它是否在线
            for (WebsocketClientConfig.Config config : websocketClientConfig.getList()) {
                ExchangeMessage<Map<String, String>> message = new ExchangeMessage<>();
                message.setBody(formDataMap);
                websocketServerUtil.send(config.getId(), message);
            }
        } catch (Exception e) {
            log.error("[ForwardServer 统一转发服务] - 错误", e);
        }
    }
}
