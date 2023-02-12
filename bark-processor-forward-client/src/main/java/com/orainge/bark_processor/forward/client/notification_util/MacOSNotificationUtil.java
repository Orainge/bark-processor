package com.orainge.bark_processor.forward.client.notification_util;

import com.orainge.bark_processor.forward.client.bean.MacOSMessage;
import com.orainge.bark_processor.forward.client.config.DebugConfig;
import com.orainge.bark_processor.forward.client.config.MacOSTerminalNotifierConfig;
import com.orainge.bark_processor.forward.client.util.OSCommandProcess;
import com.orainge.bark_processor.forward.client.util.macos.MacOSAlertUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.LinkedList;
import java.util.List;

/**
 * MacOS 通知<br>
 * https://github.com/caronc/apprise<br>
 * https://github.com/julienXX/terminal-notifier
 */
@Component
@Slf4j
public class MacOSNotificationUtil {
    @Autowired
    private DebugConfig debugConfig;

    @Autowired
    private MacOSTerminalNotifierConfig macOSTerminalNotifierConfig;

    @Autowired
    private MacOSAlertUtil macOSAlertUtil;

    public void notify(MacOSMessage macOSMessage) {
        List<String> cmdList = new LinkedList<>();
        cmdList.add(macOSTerminalNotifierConfig.getExecPath());

        String title = macOSMessage.getTitle();
        if (StringUtils.isEmpty(title)) {
            title = "通知";
        }
        cmdList.add("-title");
        cmdList.add(title);

        String subtitle = macOSMessage.getSubtitle();
        if (!StringUtils.isEmpty(subtitle)) {
            cmdList.add("-subtitle");
            cmdList.add(subtitle);
        }

        String message = macOSMessage.getMessage();
        if (StringUtils.isEmpty(message)) {
            message = "";
        }
        cmdList.add("-message");
        cmdList.add(message);

        String appIcon = macOSMessage.getAppIcon();
        if (!StringUtils.isEmpty(appIcon)) {
            cmdList.add("-appIcon");
            cmdList.add(appIcon);
            cmdList.add("-contentImage");
            cmdList.add(appIcon);
        }

        String url = macOSMessage.getUrl();
        if (!StringUtils.isEmpty(url)) {
            cmdList.add("-open");
            cmdList.add(url);
        }

        String activate = macOSMessage.getActivate();
        if (!StringUtils.isEmpty(activate)) {
            cmdList.add("-activate");
            cmdList.add(activate);
        }

        String executeCommand = macOSMessage.getExecuteCommand();
        if (!StringUtils.isEmpty(executeCommand)) {
            cmdList.add("-execute");
            cmdList.add(executeCommand);
        } else {
            // 默认执行弹窗命令
            cmdList.add("-execute");
            cmdList.add(macOSAlertUtil.buildCommandDialog(macOSMessage));
        }

        String[] cmdArray = cmdList.toArray(new String[0]);

        if (debugConfig.isEnableDebug()) {
            log.info("[MacOS 通知服务] - 执行命令 [" + String.join(",", cmdList) + "]");
        }

        // 执行 command
        OSCommandProcess.build().setCommand(cmdArray).start();
    }
}
