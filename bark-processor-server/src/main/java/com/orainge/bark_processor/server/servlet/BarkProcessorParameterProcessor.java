package com.orainge.bark_processor.server.servlet;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

/**
 * Bark 请求参数处理接口
 */
public interface BarkProcessorParameterProcessor {
    Map<String, String> process(HttpServletRequest request,
                                HttpServletResponse response);
}
