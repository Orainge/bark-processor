package com.orainge.bark_processor.forward.client.config;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;

/**
 * MacOS Alert 配置
 */
@Configuration
@ConfigurationProperties("macos-alert")
@Data
@Slf4j
public class MacOSAlertConfig {
    /**
     * ICON 文件夹路径
     */
    private String iconFolderPath;

    /**
     * macos-alert 执行文件路径
     */
    private String execPath;

    @PostConstruct
    public void init() {
        if (iconFolderPath == null || "".equals(iconFolderPath)) {
            iconFolderPath = "~";
        }

        if (!iconFolderPath.endsWith("/")) {
            iconFolderPath += "/";
        }

        if (execPath == null || "".equals(execPath)) {
            execPath = "macos-alert";
        }
    }
}
