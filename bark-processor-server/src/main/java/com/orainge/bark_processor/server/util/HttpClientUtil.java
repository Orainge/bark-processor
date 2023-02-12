package com.orainge.bark_processor.server.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.orainge.bark_processor.server.config.DebugConfig;
import com.orainge.bark_processor.server.vo.Result;
import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.util.http.fileupload.FileUploadBase;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StreamUtils;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;

@Component
@Slf4j
public class HttpClientUtil {
    @Autowired
    @Qualifier("defaultRestTemplate")
    private RestTemplate restTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    @Value("${api.url}")
    private String apiUrl;

    @Value("${server.servlet.context-path}")
    private String contextPath;

    @Autowired
    private DebugConfig debugConfig;


    public void doRequest(HttpServletRequest request, HttpServletResponse response) {
        doRequest(request, response, null);
    }

    public void doRequest(HttpServletRequest request, HttpServletResponse response, Map<String, String> formDataMap) {
        // 获取 URL
        String uri = request.getRequestURI();
        if (!Objects.equals(contextPath, "/")) {
            uri = uri.replaceAll(contextPath, "");
        }

        StringBuilder url = new StringBuilder(apiUrl + uri);

        String contentType = request.getContentType();
        if (contentType == null) {
            contentType = "";
        } else {
            contentType = contentType.toLowerCase(Locale.ENGLISH);
        }

        // 添加 requestParams
        if (!contentType.startsWith("application/x-www-form-urlencoded")) {
            // Content-Type 不是 application/x-www-form-urlencoded，就添加参数
            if (!url.toString().contains("?")) {
                url.append("?");
            }

            Object[] parameterEntryArray = request.getParameterMap().entrySet().toArray();
            int l1 = parameterEntryArray.length;
            for (int i = 0; i < l1; i++) {
                Map.Entry<String, String[]> entry = (Map.Entry<String, String[]>) parameterEntryArray[i];
                String key = entry.getKey();
                String[] values = entry.getValue();

                int l2 = values.length;
                for (int j = 0; j < l2; j++) {
                    String value = values[j];
                    url.append(key).append("=").append(value);
                    if (i + 1 != l1 || j + 1 != l2) {
                        url.append("&");
                    }
                }
            }
        }

        // 转换 body
        Object body;
        if (contentType.startsWith("application/x-www-form-urlencoded") || contentType.startsWith(FileUploadBase.MULTIPART)) {
            // Content-Type: application/x-www-form-urlencoded
            // Content-Type: multipart/*
            MultiValueMap<String, String> bodyMap = new LinkedMultiValueMap<>();

            if (formDataMap != null && !formDataMap.isEmpty()) {
                for (Map.Entry<String, String> entry : formDataMap.entrySet()) {
                    bodyMap.add(entry.getKey(), entry.getValue());
                }
            }

            body = bodyMap;
        } else {
            try {
                body = StreamUtils.copyToByteArray(request.getInputStream());
            } catch (Exception e) {
                log.error("请求体转换错误 [{}]: {}", url.toString(), e.getMessage());
                writeBodyJson(response, Result.error());
                return;
            }
        }

        // 封装 headers
        LinkedMultiValueMap<String, String> headers = new LinkedMultiValueMap<>();
        for (String name : Collections.list(request.getHeaderNames())) {
            for (String value : Collections.list(request.getHeaders(name))) {
                headers.add(name, value);
            }
        }

        // 发起请求
        ResponseEntity<byte[]> responseEntity = restTemplate.exchange(url.toString(), HttpMethod.resolve(request.getMethod()), new HttpEntity<>(body, headers), byte[].class);

        // 显示日志
        if (debugConfig.isEnableDebug()) {
            try {
                log.info("[调试工具] - 发起请求: [请求方式: " + request.getMethod() +
                        ", url: " + url +
                        ", header: " + objectMapper.writeValueAsString(headers) +
                        ", body: " + objectMapper.writeValueAsString(body)
                );
            } catch (Exception ignore) {
            }
        }

        // 写回请求结果
        response.setStatus(responseEntity.getStatusCodeValue());
        responseEntity.getHeaders().forEach((header, headerValues) -> {
            headerValues.forEach(headerValue -> {
                response.setHeader(header, headerValue);
            });
        });
        writeBody(response, responseEntity.getBody());
    }

    public void writeBody(HttpServletResponse response, byte[] body) {
        try {
            OutputStream stream = response.getOutputStream();
            stream.write(body);
            stream.close();
        } catch (IOException ignored) {
        }
    }

    public void writeBodyJson(HttpServletResponse response, Object obj) {
        try {
            OutputStream stream = response.getOutputStream();
            stream.write(objectMapper.writeValueAsString(obj).getBytes(StandardCharsets.UTF_8));
            stream.close();
        } catch (IOException ignored) {
        }
    }
}
