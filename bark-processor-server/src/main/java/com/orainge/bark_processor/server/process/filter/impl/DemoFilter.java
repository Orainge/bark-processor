package com.orainge.bark_processor.server.process.filter.impl;

import com.orainge.bark_processor.server.process.filter.Filter;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

/**
 * Demo 拦截器
 */
@Component
public class DemoFilter implements Filter {
    /**
     * 具体的处理业务逻辑
     *
     * @param formDataMap Bark 请求参数
     * @return true: 拦截 false: 不拦截
     */
    public boolean checkIfIntercept(Map<String, String> formDataMap, HttpServletRequest request, HttpServletResponse response) {
        return false;
    }
}
