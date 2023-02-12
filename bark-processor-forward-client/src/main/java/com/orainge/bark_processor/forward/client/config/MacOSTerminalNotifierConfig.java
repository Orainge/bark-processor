package com.orainge.bark_processor.forward.client.config;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;

/**
 * terminal-notifier 配置
 */
@Configuration
@ConfigurationProperties("terminal-notifier")
@Data
@Slf4j
public class MacOSTerminalNotifierConfig {
    /**
     * macos-alert 执行文件路径
     */
    private String execPath;

    @PostConstruct
    public void init() {
        if (execPath == null || "".equals(execPath)) {
            execPath = "terminal-notifier";
        }
    }
}
