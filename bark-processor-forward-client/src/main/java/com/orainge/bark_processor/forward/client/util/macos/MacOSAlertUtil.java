package com.orainge.bark_processor.forward.client.util.macos;

import com.orainge.bark_processor.forward.client.bean.MacOSMessage;
import com.orainge.bark_processor.forward.client.config.MacOSAlertConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Objects;

/**
 * MacOS 弹窗通知
 */
@Component
public class MacOSAlertUtil {
    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private MacOSAlertConfig macOSAlertConfig;

    /**
     * 使用 osascript 执行弹窗
     */
    public String buildCommandDialog(MacOSMessage macOSMessage) {
        return "osascript -e 'display dialog \"" +
                macOSMessage.getMessage() + // 内容
                "\" buttons {\"确定\"} default button 1 with title \"" +
                macOSMessage.getTitle() + // 标题
                "\"'";
    }

    /**
     * 使用 macos-alert 弹窗<br>
     * <p>
     * https://gitee.com/xiaozhuai/macos-alert/
     */
    public String buildCommandAlert(MacOSMessage macOSMessage) {
        // 处理 ICON
        String iconPath = null;
        String appIconUrl = macOSMessage.getAppIcon();
        if (!StringUtils.isEmpty(appIconUrl)) {
            // 下载 ICON 文件到本地
            iconPath = downloadFile(appIconUrl);
        }

        // 处理内容
        String message = macOSMessage.getMessage();
        message = message.replaceAll("\\n", "\\\\r\\\\n");

        StringBuilder command = new StringBuilder();
        command.append(macOSAlertConfig.getExecPath())
                .append(" <<EOF\n")
                .append("title = ").append(macOSMessage.getTitle()).append("\n") // 标题
                .append("message = ").append(macOSMessage.getTitle()).append("\n") // 标题
                .append("subMessage = ").append(message).append("\n"); // 内容
        if (iconPath != null) {
            command.append("icon = ").append(iconPath).append("\n");
        }
        command.append("buttons = 确定\n")
                .append("EOF");
        return command.toString();
    }

    private String downloadFile(String url) {
        try {
            String fileName = url.substring(url.lastIndexOf("/") + 1);
            String filePath = macOSAlertConfig.getIconFolderPath() + fileName;
            File file = new File(filePath);
            if (file.exists()) {
                // 文件存在，就不下载
                return filePath;
            }

            ResponseEntity<byte[]> forEntity = restTemplate.getForEntity(url, byte[].class);
            Files.write(Paths.get(filePath), Objects.requireNonNull(forEntity.getBody(), "未获取到下载文件"));
            return filePath;

            // 大文件下载
//        RequestCallback requestCallback = request -> request.getHeaders().setAccept(Arrays.asList(MediaType.APPLICATION_OCTET_STREAM, MediaType.ALL));
//        restTemplate.execute(url, HttpMethod.GET, requestCallback, clientHttpResponse -> {
//            Files.copy(clientHttpResponse.getBody(), Paths.get(filePath));
//            return null;
//        });
        } catch (Exception ignore) {
        }
        return null;
    }
}
