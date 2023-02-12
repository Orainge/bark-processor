package com.orainge.bark_processor.forward.client.util.macos;

import com.orainge.bark_processor.forward.client.bean.MacOSMessage;
import com.orainge.bark_processor.forward.client.config.DebugConfig;
import com.orainge.bark_processor.forward.client.notification_util.MacOSNotificationUtil;
import com.orainge.bark_processor.forward.client.util.OSUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class MacOSNotificationLogUtil {
    @Autowired
    private DebugConfig debugConfig;

    @Autowired
    private MacOSNotificationUtil macOSNotificationUtil;

    public void debug(String str) {
        if (debugConfig.isEnableDebug()) {
            macOSNotificationUtil.notify(new MacOSMessage()
                    .setTitle("统一通知推送服务")
                    .setSubtitle("调试信息")
                    .setMessage(replaceChar(str))
            );
        }
    }

    public void info(String str) {
        macOSNotificationUtil.notify(new MacOSMessage()
                .setTitle("统一通知推送服务")
                .setMessage(replaceChar(str))
        );
    }

    public void error(String str) {
        macOSNotificationUtil.notify(new MacOSMessage()
                .setTitle("统一通知推送服务")
                .setSubtitle("错误")
                .setMessage(replaceChar(str))
        );
    }

    /**
     * 替换特殊字符串
     */
    private String replaceChar(String str) {
        String result = str;
        if (OSUtils.isMacOS() || OSUtils.isMacOSX()) {
            if (str.startsWith("[")) {
                result = "\\[" + result.substring(1);
            }
        }
        return result;
    }
}
