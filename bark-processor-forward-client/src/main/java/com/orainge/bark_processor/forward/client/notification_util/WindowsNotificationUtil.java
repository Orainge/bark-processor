package com.orainge.bark_processor.forward.client.notification_util;

import com.orainge.bark_processor.forward.client.bean.WindowsMessage;
import com.orainge.bark_processor.forward.client.config.DebugConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Windows 通知<br>
 */
@Slf4j
@Component
public class WindowsNotificationUtil {
    @Autowired
    private DebugConfig debugConfig;

    public void notify(WindowsMessage windowsMessage) {
    }
}
