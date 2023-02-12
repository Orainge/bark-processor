package com.orainge.bark_processor.forward.client.websocket;

import com.orainge.bark_processor.forward.client.config.WebsocketClientConfig;
import com.orainge.bark_processor.forward.client.config.WebsocketServerConfig;
import com.orainge.bark_processor.forward.client.util.macos.MacOSNotificationLogUtil;
import com.orainge.bark_processor.forward.client.util.WebsocketClientUtil;
import com.orainge.websocket_forward.consts.AuthHeader;
import com.orainge.websocket_forward.consts.MessageCode;
import com.orainge.websocket_forward.util.JSONUtil;
import com.orainge.websocket_forward.vo.ExchangeMessage;
import lombok.extern.slf4j.Slf4j;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.drafts.Draft_6455;
import org.java_websocket.handshake.ServerHandshake;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;

/**
 * Websocket 服务器对象容器
 */
@Component
@EnableScheduling
@Slf4j
public class WebsocketServerContainer {
    @Resource
    private ApplicationContext appContext;

    @Resource
    private WebsocketServerConfig websocketServerConfig;

    @Resource
    private WebsocketClientConfig websocketClientConfig;

    @Resource
    private WebsocketClientUtil websocketClientUtil;

    @Resource
    private MacOSNotificationLogUtil nLog;

    /**
     * 当前正在使用的 Bark Processor 客户端
     */
    public static WebSocketClient webSocketClient = null;

    /**
     * 是否为重复连接的标识
     */
    private boolean isDuplicateConnect = false;

    /**
     * 是否在系统上显示断开连接的提示
     */
    private boolean showDisconnectPop = true;

    /**
     * 创建一个客户端对象
     */
    @PostConstruct
    public void build() {
        // 获取客户端信息
        String id = websocketClientConfig.getId();
        String key = websocketClientConfig.getSecurityKey();

        // 添加请求头
        Map<String, String> headers = new HashMap<>();
        headers.put(AuthHeader.ID_HEADER_NAME, id); // 客户端 ID
        headers.put(AuthHeader.KEY_HEADER_NAME, key); // 客户端 KEY

        try {
            WebsocketServerContainer.webSocketClient = new WebSocketClient(
                    new URI(websocketServerConfig.getUrl()),
                    new Draft_6455(),
                    headers,
                    0
            ) {
                @Override
                public void onOpen(ServerHandshake serverHandshake) {
                    String keepAliveTag = MessageCode.KEEP_ALIVE_TAG;
                    WebsocketServerContainer.webSocketClient.send(keepAliveTag);
                    log.info("[Bark Processor 客户端] 连接成功 [ID: " + id + "]");
                    nLog.info("服务器连接成功");
                    showDisconnectPop = true;
                }

                @Override
                public void onMessage(String message) {
                    // 检查是否为服务器发送的心跳检测包
                    if (MessageCode.KEEP_ALIVE_TAG.equals(message)) {
                        try {
                            WebsocketServerContainer.webSocketClient.send(MessageCode.KEEP_ALIVE_TAG_RESPONSE);
                            log.debug("[Bark Processor 客户端] 发送心跳响应包成功");
                            return;
                        } catch (Exception e) {
                            log.debug("[Bark Processor 客户端] 发送心跳响应包失败", e);
                            return;
                        }
                    }

                    // 检查是否为服务器发送的心跳检测响应包
                    if (MessageCode.KEEP_ALIVE_TAG_RESPONSE.equals(message)) {
                        // 收到心跳包，不做任何处理
                        log.debug("[Bark Processor 客户端] 收到来自服务器的心跳检测响应");
                        return;
                    }

                    // 检查是否为客户端重复登录检测响应
                    if (MessageCode.DUPLICATE_CLIENT_TAG.equals(message)) {
                        isDuplicateConnect = true;
                        log.error("[Bark Processor 客户端] 连接错误: 已有相同的 ID 的客户端连接到服务器");
                        nLog.error("已有相同的 ID 的客户端连接到服务器");
                        return;
                    }

                    if (!StringUtils.isEmpty(message)) {
                        websocketClientUtil.onMessage(JSONUtil.parseObject(message, ExchangeMessage.class));
                    }
                }

                @Override
                public void onClose(int code, String reason, boolean remote) {
                    String logStr;
                    String nLogStr;
                    if (remote) {
                        logStr = "[Bark Processor 客户端] 连接关闭 - 服务器断开连接";
                        nLogStr = "与服务器连接断开";
                    } else {
                        logStr = "[Bark Processor 客户端] 连接关闭 - 客户端断开连接";
                        nLogStr = "与服务器连接断开";
                    }
                    log.error(logStr);
                    if (showDisconnectPop) {
                        showDisconnectPop = false;
                        nLog.info(nLogStr);
                    }
                    reconnect();
                }

                @Override
                public void onError(Exception ex) {
                    log.error("[Bark Processor 客户端] 连接错误 - {}", ex.getMessage());
                }
            };

            // 建立连接
            webSocketClient.connect();
        } catch (Exception e) {
            log.error("[Bark Processor 客户端] - 连接失败", e);
            nLog.error("连接失败");
            reconnect();
        }
    }

    /**
     * 重新连接
     */
    public void reconnect() {
        // 如果当前连接不是被标记为重复连接，则重试连接
        // 否则将不重新连接
        if (isDuplicateConnect) {
            // 重新连接，不重试
            // 重置标记状态
            isDuplicateConnect = false;

            // 关闭项目
            shutdown();
        } else {
            Integer reconnectWait = websocketServerConfig.getReconnectWait();
            WebsocketServerContainer.webSocketClient = null;
            new Thread(() -> {
                try {
                    Thread.sleep(reconnectWait * 1000L);
                } catch (Exception ignore) {
                }
                log.warn("[Bark Processor 客户端] 正在重新连接服务器");
                build();
            }).start();
        }
    }

    /**
     * 关闭整个项目
     */
    public void shutdown() {
        SpringApplication.exit(appContext, () -> 0);
    }

    /**
     * 每分钟第 0 秒和第 30 秒发送心跳包给服务端
     */
    @Scheduled(cron = "0,30 * * * * ?")
    public void sendKeepAlive() {
        try {
            if (WebsocketServerContainer.webSocketClient != null &&
                    WebsocketServerContainer.webSocketClient.isOpen()) {
                webSocketClient.send(MessageCode.KEEP_ALIVE_TAG);
                log.debug("[Bark Processor 客户端] 发送心跳检测包成功");
            }
        } catch (Exception e) {
            log.error("[Bark Processor 客户端] 发送心跳检测包失败");
        }
    }
}
