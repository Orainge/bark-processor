package com.orainge.bark_processor.forward.client.processor.impl;

import com.alibaba.fastjson.JSON;
import com.orainge.bark_processor.forward.client.bean.BarkMessage;
import com.orainge.bark_processor.forward.client.bean.MacOSMessage;
import com.orainge.bark_processor.forward.client.bean.WindowsMessage;
import com.orainge.bark_processor.forward.client.config.DebugConfig;
import com.orainge.bark_processor.forward.client.notification_util.MacOSNotificationUtil;
import com.orainge.bark_processor.forward.client.notification_util.WindowsNotificationUtil;
import com.orainge.bark_processor.forward.client.processor.OnMessageProcessor;
import com.orainge.bark_processor.forward.client.util.OSUtils;
import com.orainge.websocket_forward.vo.ExchangeMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 收到通知服务端发送过来的信息
 */
@Component
@Slf4j
public class OnBarkProcessorMessageProcessor implements OnMessageProcessor {
    @Autowired
    private DebugConfig debugConfig;

    @Autowired
    private MacOSNotificationUtil macOSNotificationUtil;

    @Autowired
    private WindowsNotificationUtil windowsNotificationUtil;

    @Override
    public void process(ExchangeMessage<?> message) {
        Object body = message.getBody();

        if (debugConfig.isEnableDebug()) {
            log.info("[Bark Processor 客户端] 收到服务端请求: " + JSON.toJSONString(body));
        }

        // {
        //	"isArchive": "1",
        //	"level": "active",
        //	"icon": "http://xxx.com/xxx.png",
        //	"title": "标题",
        //	"body": "通知内容"
        //}

        if (body != null) {
            // 转换 Bark 信息
            BarkMessage barkMessage = JSON.parseObject(JSON.toJSONString(body), BarkMessage.class);

            String title = barkMessage.getTitle();
            String newBody = barkMessage.getBody();

            // MacOS 系统通知
            if (OSUtils.isMacOS() || OSUtils.isMacOSX()) {
                MacOSMessage macOSMessage = new MacOSMessage();
                macOSMessage.setTitle(title);
                macOSMessage.setMessage(newBody);
                macOSMessage.setAppIcon(barkMessage.getIcon());
                macOSNotificationUtil.notify(macOSMessage);
            } else if(OSUtils.isWindows()){
                WindowsMessage windowsMessage = new WindowsMessage();
                windowsNotificationUtil.notify(windowsMessage);
            }
        }
    }
}
