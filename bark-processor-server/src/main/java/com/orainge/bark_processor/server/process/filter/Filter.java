package com.orainge.bark_processor.server.process.filter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

public interface Filter {
    /**
     * 检查是否拦截
     */
    boolean checkIfIntercept(Map<String, String> formDataMap, HttpServletRequest request, HttpServletResponse response);
}
