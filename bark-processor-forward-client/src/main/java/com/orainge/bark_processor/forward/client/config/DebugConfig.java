package com.orainge.bark_processor.forward.client.config;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;

/**
 * 调试配置
 */
@Configuration
@ConfigurationProperties("debug")
@Data
@Slf4j
public class DebugConfig {
    /**
     * 显示请求的 URL
     */
    private String enable = "false";

    private boolean enableDebug = false;

    @PostConstruct
    public void init() {
        if ("true".equals(enable)) {
            enableDebug = true;
            log.info("[调试模式] - 已开启调试模式");
        }
    }
}
