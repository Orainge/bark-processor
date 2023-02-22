package com.orainge.bark_processor.server.process.repeat_filter;

import com.orainge.bark_processor.server.config.DebugConfig;
import com.orainge.bark_processor.server.config.DeviceConfig;
import com.orainge.bark_processor.server.util.RegularMatchUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 时间间隔重复处理工具类
 */
@Component
@Slf4j
public class RepeatFilterUtils {
    private static final Map<String, Long> TIME_MAP = new HashMap<>();

    @Autowired
    private DebugConfig debugConfig;

    public boolean checkIfIntercept(DeviceConfig.Config config,
                                    Map<String, String> formDataMap,
                                    HttpServletRequest request,
                                    HttpServletResponse response) {
        List<DeviceConfig.RepeatFilter> repeatFilterList = config.getRepeatFilterList();

        if (repeatFilterList == null) {
            // 没有配置，直接放行
            return false;
        }

        for (DeviceConfig.RepeatFilter repeatFilter : repeatFilterList) {
            if (checkIfIntercept(repeatFilter, config.getDeviceKey(), formDataMap, request, response)) {
                return true;
            }
        }

        // 所有配置检查通过，不拦截
        return false;
    }

    private boolean checkIfIntercept(DeviceConfig.RepeatFilter config,
                                     String deviceKey,
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
                log.info("[调试工具] - RepeatFilterUtils - 标题" + (titleKeywordHit ? "命中" : "未命中") + "关键字【" + titleKeyword + "】[URL: " + request.getRequestURI() + "]");
            }
        } else {
            if (debugConfig.isEnableDebug()) {
                log.info("[调试工具] - RepeatFilterUtils - 标题任意匹配 [URL: " + request.getRequestURI() + "]");
            }
        }

        if (!StringUtils.isEmpty(contentKeyword)) {
            String content = formDataMap.get("body");
            contentKeywordHit = RegularMatchUtils.match(content, contentKeyword);

            if (debugConfig.isEnableDebug()) {
                log.info("[调试工具] - RepeatFilterUtils - 内容" + (contentKeywordHit ? "命中" : "未命中") + "关键字【" + contentKeyword + "】[URL: " + request.getRequestURI() + "]");
            }
        } else {
            if (debugConfig.isEnableDebug()) {
                log.info("[调试工具] - RepeatFilterUtils - 内容任意匹配 [URL: " + request.getRequestURI() + "]");
            }
        }

        if (!titleKeywordHit || !contentKeywordHit) {
            // 任意一个没有命中，就算未命中
            if (debugConfig.isEnableDebug()) {
                log.info("[调试工具] - RepeatFilterUtils - 未命中关键字，不拦截 [URL: " + request.getRequestURI() + "]");
            }
            return false;
        }

        // 时间间隔
        Integer intervalTime = config.getInterval();
        if (intervalTime == null) {
            // 没有配置该 title，直接转发
            if (debugConfig.isEnableDebug()) {
                log.info("[调试工具] - RepeatFilterUtils - 未配置时间间隔，不拦截 [URL: " + request.getRequestURI() + "]");
            }
            return true;
        }

        // 时间判断
        if (checkIfIntercept(deviceKey, intervalTime)) {
            // 未到时间间隔，拦截
            if (debugConfig.isEnableDebug()) {
                log.info("[调试工具] - RepeatFilterUtils - 没有超时，拦截 [URL: " + request.getRequestURI() + "]");
            }
            return false;
        } else {
            // 已过时间间隔，不拦截
            if (debugConfig.isEnableDebug()) {
                log.info("[调试工具] - RepeatFilterUtils - 超时，不拦截 [URL: " + request.getRequestURI() + "]");
            }
            return true;
        }
    }

    /**
     * 判断是否拦截
     *
     * @param deviceKey    设备 Key
     * @param intervalTime 间隔时间
     * @return true: 拦截; false: 不拦截
     */
    private boolean checkIfIntercept(String deviceKey, int intervalTime) {
        if (intervalTime == 0) {
            // 间隔时间 = 0，不拦截
            return false;
        } else if (intervalTime < 0) {
            // 间隔时间 < 0，一直拦截
            return true;
        }

        synchronized (deviceKey.intern()) {
            Long lastTime = TIME_MAP.get(deviceKey);
            if (lastTime == null) {
                TIME_MAP.put(deviceKey, System.currentTimeMillis());
                return false;
            } else {
                // 比较时间
                long nowTime = System.currentTimeMillis();
                boolean tag = nowTime - lastTime <= intervalTime * 1000L;
                if (!tag) {
                    TIME_MAP.put(deviceKey, nowTime);
                }
                return tag;
            }
        }
    }
}
