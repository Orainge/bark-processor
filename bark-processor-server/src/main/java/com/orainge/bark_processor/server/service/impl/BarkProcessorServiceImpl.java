package com.orainge.bark_processor.server.service.impl;

import com.orainge.bark_processor.server.config.DeviceConfig;
import com.orainge.bark_processor.server.process.filter.FilterUtils;
import com.orainge.bark_processor.server.process.forwarder.ForwarderUtils;
import com.orainge.bark_processor.server.process.processor.ProcessorUtils;
import com.orainge.bark_processor.server.process.repeat_filter.RepeatFilterUtils;
import com.orainge.bark_processor.server.service.BarkProcessorService;
import com.orainge.bark_processor.server.util.HttpClientUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

@Service
@Slf4j
public class BarkProcessorServiceImpl implements BarkProcessorService {
    @Autowired
    private FilterUtils filterUtils;

    @Autowired
    private RepeatFilterUtils repeatFilterUtils;

    @Autowired
    private HttpClientUtil httpClientUtil;

    @Autowired
    private ProcessorUtils processorUtils;

    @Autowired
    private ForwarderUtils forwarderUtils;

    @Override
    public void process(DeviceConfig.Config config, Map<String, String> formDataMap, HttpServletRequest request, HttpServletResponse response) {
        /**
         * formDataMap 示例数据
         *
         * {
         *     	"isArchive": "1",
         *     	"level": "active",
         *     	"icon": "http://xxx.com/xxx.png",
         *     	"title": "标题",
         *     	"body": "内容"
         *     }
         */

        // 判断是不是推送通知
        String title = formDataMap.get("title");

        if (title == null) {
            // 不是推送通知，直接转发
            httpClientUtil.doRequest(request, response, formDataMap);
            return;
        }

        // RepeatFilterUtils 执行【拦截判断】操作
        if (repeatFilterUtils.checkIfIntercept(config, formDataMap, request, response)) {
            // 拦截，不转发
            return;
        }

        // FilterUtils 执行【拦截判断】操作
        if (filterUtils.checkIfIntercept(config, formDataMap, request, response)) {
            // 拦截，不转发
            return;
        }

        // ProcessorUtils 执行【处理数据】操作
        processorUtils.process(config, formDataMap, request, response);

        // ForwarderUtils 执行【转发】操作
        forwarderUtils.process(config, formDataMap, request, response);

        // 执行【转发到 Bark 服务】操作
        httpClientUtil.doRequest(request, response, formDataMap);
    }
}
