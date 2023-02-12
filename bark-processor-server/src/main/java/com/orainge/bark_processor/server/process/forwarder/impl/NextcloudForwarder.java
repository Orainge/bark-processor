package com.orainge.bark_processor.server.process.forwarder.impl;

import com.orainge.bark_processor.server.config.DebugConfig;
import com.orainge.bark_processor.server.process.forwarder.Forwarder;
import com.orainge.bark_processor.server.process.forwarder.config.NextcloudConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Base64;
import java.util.Map;

/**
 * NextCloud 通知服务 Forwarder
 */
@Component
@Slf4j
public class NextcloudForwarder implements Forwarder {
    @Autowired
    @Qualifier("defaultRestTemplate")
    private RestTemplate restTemplate;

    @Autowired
    private DebugConfig debugConfig;

    @Autowired
    private NextcloudConfig nextcloudConfig;

    @Override
    public void process(Map<String, String> formDataMap, HttpServletRequest request, HttpServletResponse response) {
        try {
            String shortMessage = formDataMap.get("title"); // 标题
            String longMessage = formDataMap.get("body"); // 内容

            for (NextcloudConfig.Config config : nextcloudConfig.getHostList()) {
                HttpHeaders headers = new HttpHeaders();
                String userPass = config.getAdminUser() + ":" + config.getAdminPassword();
                headers.add("OCS-APIREQUEST", "true");
                headers.add("User-Agent", "curl/7.58.0");
                headers.add("Authorization", "Basic " + Base64.getEncoder().encodeToString(userPass.getBytes()));

                for (String notificationUser : config.getNotificationUserList()) {
                    String url = "https://" + config.getHost() + "/ocs/v2.php/apps/notifications/api/v2/admin_notifications/" + notificationUser +
                            "?format=json" +
                            "&shortMessage=" + shortMessage +
                            "&longMessage=" + longMessage;

                    ResponseEntity<String> responseEntity = restTemplate.exchange(url, HttpMethod.POST, new HttpEntity<>(headers), String.class);

                    if (debugConfig.isEnableDebug()) {
                        log.info("[调试工具] - NextcloudForwarder - 转发结果 [url: " + url + ", 请求结果: " + responseEntity.getBody() + " ]");
                    }
                }
            }
        } catch (Exception e) {
            log.error("[Nextcloud 转发服务] - 错误", e);
        }
    }
}
