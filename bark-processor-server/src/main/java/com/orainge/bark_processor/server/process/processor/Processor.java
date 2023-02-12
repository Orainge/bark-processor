package com.orainge.bark_processor.server.process.processor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

public interface Processor {
    /**
     * 具体的处理业务逻辑
     */
    void process(Map<String, String> formDataMap, HttpServletRequest request, HttpServletResponse response);
}
