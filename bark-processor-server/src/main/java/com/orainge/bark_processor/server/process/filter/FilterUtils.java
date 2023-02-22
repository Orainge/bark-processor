package com.orainge.bark_processor.server.process.filter;

import com.orainge.bark_processor.server.config.DebugConfig;
import com.orainge.bark_processor.server.config.DeviceConfig;
import com.orainge.bark_processor.server.process.forwarder.Forwarder;
import com.orainge.bark_processor.server.util.ApplicationContextUtils;
import com.orainge.bark_processor.server.util.RegularMatchUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;

/**
 * 数据内容处理工具类
 */
@Component
@Slf4j
public class FilterUtils {
    @Autowired
    private DebugConfig debugConfig;

    public boolean checkIfIntercept(DeviceConfig.Config config,
                                    Map<String, String> formDataMap,
                                    HttpServletRequest request,
                                    HttpServletResponse response) {
        List<DeviceConfig.Filter> filterList = config.getFilterList();

        if (filterList == null) {
            // 没有配置，直接放行
            return false;
        }

        for (DeviceConfig.Filter filter : filterList) {
            if (checkIfIntercept(filter, formDataMap, request, response)) {
                return true;
            }
        }

        // 所有配置检查通过，不拦截
        return false;
    }

    private boolean checkIfIntercept(DeviceConfig.Filter config,
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
                log.info("[调试工具] - FilterUtils - 标题" + (titleKeywordHit ? "命中" : "未命中") + "关键字【" + titleKeyword + "】[URL: " + request.getRequestURI() + "]");
            }
        } else {
            if (debugConfig.isEnableDebug()) {
                log.info("[调试工具] - FilterUtils - 标题任意匹配 [URL: " + request.getRequestURI() + "]");
            }
        }

        if (!StringUtils.isEmpty(contentKeyword)) {
            String content = formDataMap.get("body");
            contentKeywordHit = RegularMatchUtils.match(content, contentKeyword);

            if (debugConfig.isEnableDebug()) {
                log.info("[调试工具] - FilterUtils - 内容" + (contentKeywordHit ? "命中" : "未命中") + "关键字【" + contentKeyword + "】[URL: " + request.getRequestURI() + "]");
            }
        } else {
            if (debugConfig.isEnableDebug()) {
                log.info("[调试工具] - FilterUtils - 内容任意匹配 [URL: " + request.getRequestURI() + "]");
            }
        }

        if (!titleKeywordHit || !contentKeywordHit) {
            // 任意一个没有命中，就算未命中
            if (debugConfig.isEnableDebug()) {
                log.info("[调试工具] - FilterUtils - 未命中关键字，不拦截 [URL: " + request.getRequestURI() + "]");
            }
            return false;
        }

        // 获取 Bean 名称
        String beanName = config.getFilterName();
        if (StringUtils.isEmpty(beanName)) {
            if (debugConfig.isEnableDebug()) {
                log.info("[调试工具] - FilterUtils - 未配置 Bean 名称，不处理 [URL: " + request.getRequestURI() + "]");
            }
            return false;
        }

        // 获取 Bean
        Filter filter = ApplicationContextUtils.getBean(beanName, Filter.class);
        if (filter == null) {
            if (debugConfig.isEnableDebug()) {
                log.info("[调试工具] - FilterUtils - 无法获取 Bean，不处理 [bean 名称: " + beanName + ", URL: " + request.getRequestURI() + "]");
            }
            return false;
        }

        // 执行判断逻辑
        return filter.checkIfIntercept(formDataMap, request, response);
    }
}
