package com.orainge.bark_processor.server.process.forwarder.config;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * 调试配置
 */
@Configuration
@ConfigurationProperties("nextcloud")
@Data
@Slf4j
public class NextcloudConfig {
    private List<Config> hostList;

    @Data
    public static class Config {
        private String host;
        private String adminUser;
        private String adminPassword;
        private List<String> notificationUserList;
    }
}
