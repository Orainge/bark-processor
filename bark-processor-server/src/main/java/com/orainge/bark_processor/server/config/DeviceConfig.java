package com.orainge.bark_processor.server.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

//import javax.annotation.PostConstruct;
//import java.util.LinkedList;
import java.util.List;

@Configuration
@ConfigurationProperties("device")
@Data
public class DeviceConfig {
    /**
     * 每个设备的配置
     */
    private List<Config> config;

//    /**
//     * 所有设备的 Key
//     */
//    public static final List<String> ALL_DEVICES_KEY_LIST = new LinkedList<>();

//    public List<RepeatFilter> getRuleListByDeviceKey(String deviceKey) {
//        for (Config configItem : config) {
//            if (configItem.getKey().equals(deviceKey)) {
//                return configItem.getRepeatFilterList();
//            }
//        }
//        return new LinkedList<>();
//    }

//    @PostConstruct
//    public void init() {
//        config.forEach(item -> {
//            // 添加设备 key 列表
//            ALL_DEVICES_KEY_LIST.add(item.getKey());
//
//            // 初始化处理器 Bean
//            List<Processor> processorList = item.getProcessorList();
//            if (processorList != null) {
//                processorList.forEach(processorItem -> {
//                    String beanName = processorItem.getProcessorName();
//
//                });
//            }
//
//            // 初始化处理器 Bean
//            List<Forwarder> forwarderList = item.getForwarderList();
//            if (forwarderList != null) {
//                forwarderList.forEach(forwarderItem -> {
//                    String beanName = forwarderItem.getForwarderName();
//
//                });
//            }
//        });
//    }

    @Data
    public static class Config {
        /**
         * 设备 Key
         */
        private String deviceKey;

        /**
         * 拦截规则
         */
        private List<RepeatFilter> repeatFilterList;

        /**
         * 处理规则
         */
        private List<Processor> processorList;

        /**
         * 转发规则
         */
        private List<Forwarder> forwarderList;
    }

    @Data
    public static class RepeatFilter {
        /**
         * 标题里包含什么字符串就进行拦截
         */
        private String titleKeyword;

        /**
         * 内容里包含什么字符串就进行拦截
         */
        private String contentKeyword;

        /**
         * 间隔几秒内只能重复发送一次
         */
        private Integer interval;
    }

    @Data
    public static class Processor {
        /**
         * 标题里包含什么字符串就进行拦截
         */
        private String titleKeyword;

        /**
         * 内容里包含什么字符串就进行拦截
         */
        private String contentKeyword;

        /**
         * 处理器 Bean 名称
         */
        private String processorName;
    }

    @Data
    public static class Forwarder {
        /**
         * 标题里包含什么字符串就进行拦截
         */
        private String titleKeyword;

        /**
         * 内容里包含什么字符串就进行拦截
         */
        private String contentKeyword;

        /**
         * 转发器 Bean 名称
         */
        private String forwarderName;
    }
}
