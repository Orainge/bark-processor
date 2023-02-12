package com.orainge.bark_processor.server.process.forwarder;

import com.orainge.bark_processor.server.config.DebugConfig;
import com.orainge.bark_processor.server.config.DeviceConfig;
import com.orainge.bark_processor.server.util.ApplicationContextUtils;
import com.orainge.bark_processor.server.util.RegularMatchUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * 数据转发工具类
 */
@Component("forwarderUtils")
@Slf4j
public class ForwarderUtils {
    @Autowired
    private DebugConfig debugConfig;

    public void process(DeviceConfig.Config config,
                        Map<String, String> formDataMap,
                        HttpServletRequest request,
                        HttpServletResponse response) {
        List<DeviceConfig.Forwarder> forwarderList = config.getForwarderList();

        if (forwarderList == null) {
            // 没有配置，不进行任何操作
            return;
        }

        for (DeviceConfig.Forwarder forwarder : forwarderList) {
            process(forwarder, formDataMap, request, response);
        }
    }

    private void process(DeviceConfig.Forwarder config,
                         Map<String, String> formDataMap,
                         HttpServletRequest request,
                         HttpServletResponse response) {
        String titleKeyword = config.getTitleKeyword();
        String contentKeyword = config.getContentKeyword();
        boolean titleKeywordHit = true, contentKeywordHit = true;

        if (!StringUtils.isEmpty(titleKeyword)) {
            String title = formDataMap.get("title");
            titleKeywordHit = RegularMatchUtils.match(title, titleKeyword);

            if (debugConfig.isEnableDebug()) {
                log.info("[调试工具] - ForwarderUtils - 标题" + (titleKeywordHit ? "命中" : "未命中") + "关键字【" + titleKeyword + "】[URL: " + request.getRequestURI() + "]");
            }
        } else {
            if (debugConfig.isEnableDebug()) {
                log.info("[调试工具] - ForwarderUtils - 标题任意匹配 [URL: " + request.getRequestURI() + "]");
            }
        }

        if (!StringUtils.isEmpty(contentKeyword)) {
            String content = formDataMap.get("body");
            contentKeywordHit = RegularMatchUtils.match(content, contentKeyword);

            if (debugConfig.isEnableDebug()) {
                log.info("[调试工具] - ForwarderUtils - 内容" + (contentKeywordHit ? "命中" : "未命中") + "关键字【" + contentKeyword + "】[URL: " + request.getRequestURI() + "]");
            }
        } else {
            if (debugConfig.isEnableDebug()) {
                log.info("[调试工具] - ForwarderUtils - 内容任意匹配 [URL: " + request.getRequestURI() + "]");
            }
        }

        if (!titleKeywordHit || !contentKeywordHit) {
            // 任意一个没有命中，就算未命中
            if (debugConfig.isEnableDebug()) {
                log.info("[调试工具] - ForwarderUtils - 未命中关键字，不处理 [URL: " + request.getRequestURI() + "]");
            }
            return;
        }

        // 获取 Bean 名称
        String beanName = config.getForwarderName();
        if (StringUtils.isEmpty(beanName)) {
            if (debugConfig.isEnableDebug()) {
                log.info("[调试工具] - ForwarderUtils - 未配置 Bean 名称，不处理 [URL: " + request.getRequestURI() + "]");
            }
            return;
        }

        // 获取 Bean
        Forwarder forwarder = ApplicationContextUtils.getBean(beanName, Forwarder.class);
        if (forwarder == null) {
            if (debugConfig.isEnableDebug()) {
                log.info("[调试工具] - ForwarderUtils - 无法获取 Bean，不处理 [bean 名称: " + beanName + ", URL: " + request.getRequestURI() + "]");
            }
            return;
        }

        // 使用线程池进行处理
        Objects.requireNonNull(ApplicationContextUtils.getBean("forwarderUtils", ForwarderUtils.class))
                .addTheadPoolAndProcess(forwarder, formDataMap, request, response);
    }

    @Async("forwarderTaskExecutor")
    public void addTheadPoolAndProcess(Forwarder forwarder,
                                       Map<String, String> formDataMap,
                                       HttpServletRequest request,
                                       HttpServletResponse response) {
        forwarder.process(formDataMap, request, response);
    }
}
