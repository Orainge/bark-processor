package com.orainge.bark_processor.forward.client.bean;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * MacOS 系统通知 Bean
 */
@Data
@Accessors(chain = true)
public class MacOSMessage {
    /**
     * 通知标题
     */
    private String title;

    /**
     * 通知副标题
     */
    private String subtitle;

    /**
     * 通知内容
     */
    private String message;

    /**
     * 通知 ICON
     */
    private String appIcon;

    /**
     * 点击通知打开的 URL
     */
    private String url;

    /**
     * 点击 "显示" 按钮打开的包名
     */
    private String activate;

    /**
     * 执行的 CMD 命令
     */
    private String executeCommand;
}
