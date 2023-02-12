package com.orainge.bark_processor.server.process.processor.impl;

import com.orainge.bark_processor.server.process.processor.Processor;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

/**
 * Demo 通知处理器
 */
@Component
public class DemoProcessor implements Processor {
    /**
     * 具体的处理业务逻辑
     *
     * @param formDataMap Bark 请求参数
     */
    public void process(Map<String, String> formDataMap, HttpServletRequest request, HttpServletResponse response) {

    }
}
