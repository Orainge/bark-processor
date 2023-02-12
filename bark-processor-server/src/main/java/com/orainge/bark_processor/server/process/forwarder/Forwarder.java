package com.orainge.bark_processor.server.process.forwarder;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

public interface Forwarder {
    /**
     * 具体的处理业务逻辑
     */
    void process(Map<String, String> formDataMap, HttpServletRequest request, HttpServletResponse response);
}
