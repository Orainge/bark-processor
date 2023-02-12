package com.orainge.bark_processor.server.servlet;

import com.orainge.bark_processor.server.config.DeviceConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.ServletContextInitializer;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import javax.servlet.ServletContext;
import javax.servlet.ServletRegistration;

@Component
public class BarkProcessorServletContextInitializer implements ServletContextInitializer {
    @Resource
    private BarkProcessorServlet barkProcessorServlet;

    @Autowired
    private DeviceConfig deviceConfig;

    @Override
    public void onStartup(ServletContext servletContext) {
        int i = 0;
        for (DeviceConfig.Config config : deviceConfig.getConfig()) {
            String url = config.getDeviceKey();
            if (!url.startsWith("/")) {
                url = "/" + url;
            }

            ServletRegistration initServlet = servletContext.addServlet(
                    "barkProcessorServlet" + (i++),
                    barkProcessorServlet
            );

            // 拦截所有请求 URL
            initServlet.addMapping(url + "/*");
        }
    }
}
