package com.orainge.bark_processor.server.servlet;

import com.alibaba.fastjson.JSON;
import com.orainge.bark_processor.server.config.DebugConfig;
import com.orainge.bark_processor.server.config.DeviceConfig;
import com.orainge.bark_processor.server.service.BarkProcessorService;
import com.orainge.bark_processor.server.util.HttpClientUtil;
import com.orainge.bark_processor.server.vo.Result;
import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.util.http.fileupload.FileItem;
import org.apache.tomcat.util.http.fileupload.FileUploadBase;
import org.apache.tomcat.util.http.fileupload.disk.DiskFileItemFactory;
import org.apache.tomcat.util.http.fileupload.servlet.ServletFileUpload;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.*;

@Slf4j
@Component("barkProcessorServlet")
public class BarkProcessorServlet extends HttpServlet {
    @Autowired
    private BarkProcessorService barkProcessorService;

    @Autowired
    private HttpClientUtil httpClientUtil;

    @Autowired
    private DebugConfig debugConfig;

    @Autowired
    private DeviceConfig deviceConfig;

    /**
     * Content-Type -> 处理器
     */
    private static final Map<String, BarkProcessorParameterProcessor> PARAMETER_PROCESSOR = new HashMap<>();

    static {
        // Content-Type: application/x-www-form-urlencoded
        PARAMETER_PROCESSOR.put("application/x-www-form-urlencoded", (request, response) -> {
            Map<String, String> formDataMap = new HashMap<>(); // formData 表单
            Map<String, String[]> map = request.getParameterMap();
            for (Map.Entry<String, String[]> entry : map.entrySet()) {
                formDataMap.put(entry.getKey(), entry.getValue()[0]);
            }
            return formDataMap;
        });

//        // Content-Type: application/json
//        PARAMETER_PROCESSOR.put("application/json", (request, response) -> {
//            Map<String, String> formDataMap = new HashMap<>(); // formData 表单
//            Map<String, String[]> map = request.getParameterMap();
//            for (Map.Entry<String, String[]> entry : map.entrySet()) {
//                formDataMap.put(entry.getKey(), entry.getValue()[0]);
//            }
//            return formDataMap;
//        });

        // 表单文件类型 Content-Type: multipart/*
        PARAMETER_PROCESSOR.put(FileUploadBase.MULTIPART, (request, response) -> {
            try {
                Map<String, String> formDataMap = new HashMap<>(); // formData 表单

                DiskFileItemFactory factory = new DiskFileItemFactory();
                ServletFileUpload sfu = new ServletFileUpload(factory);
                Map<String, List<FileItem>> map = sfu.parseParameterMap(request);

                map.forEach((key, value) -> {
                    for (FileItem fileitem : value) {
                        if (fileitem != null && fileitem.isFormField()) { //判读不是普通表单域即是file
                            try {
                                formDataMap.put(fileitem.getFieldName(), fileitem.getString("utf-8"));
                            } catch (UnsupportedEncodingException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                });

                return formDataMap;
            } catch (Exception e) {
                log.error("[请求参数处理] - 错误", e);
            }
            return null;
        });
    }

    @Override
    protected void service(HttpServletRequest request, HttpServletResponse response) {
        try {
            // 获取请求 URL
            String url = request.getRequestURI();
            if (debugConfig.isEnableDebug()) {
                log.info("[调试工具] - 请求 URL: " + url + "]");
            }

            // 获取 Content-Type
            String contentType = request.getContentType();
            if (contentType == null) {
                // 未知请求类型，直接转发
                httpClientUtil.doRequest(request, response);
            } else {
                contentType = contentType.toLowerCase(Locale.ENGLISH);

                // 获取参数处理器
                BarkProcessorParameterProcessor parameterProcessor = null;
                for (Map.Entry<String, BarkProcessorParameterProcessor> entry : PARAMETER_PROCESSOR.entrySet()) {
                    String key = entry.getKey();
                    if (new AntPathMatcher().match(key, contentType)) {
                        // ant匹配成功
                        parameterProcessor = entry.getValue();
                    }
                }

                if (parameterProcessor == null) {
                    // 没有参数处理器，直接转发
                    httpClientUtil.doRequest(request, response);
                } else {
                    // 检查是否包含请求的设备 key
                    boolean isHit = false;
                    for (DeviceConfig.Config config : deviceConfig.getConfig()) {
                        if (url.contains(config.getDeviceKey())) {
                            isHit = true;
                            Map<String, String> formDataMap = parameterProcessor.process(request, response);
                            barkProcessorService.process(config, formDataMap, request, response);
                        }
                    }
                    if (!isHit) {
                        // 没有命中 key，直接转发
                        httpClientUtil.doRequest(request, response);
                    }
                }
            }
        } catch (Exception e) {
            log.error("转发错误", e);
            httpClientUtil.writeBodyJson(response, Result.error());
        }
    }
}
