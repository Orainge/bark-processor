package com.orainge.bark_processor.server.service;

import com.orainge.bark_processor.server.config.DeviceConfig;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

public interface BarkProcessorService {
    void process(DeviceConfig.Config config, Map<String, String> formDataMap, HttpServletRequest request, HttpServletResponse response);
}
