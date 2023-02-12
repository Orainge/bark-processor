//package bark_processor.controller;
//
//import bark_processor.config.DeviceConfig;
//import bark_processor.service.BarkProcessorService;
//import bark_processor.util.HttpClientUtil;
//import bark_processor.vo.Result;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Controller;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RequestParam;
//
//import javax.servlet.http.HttpServletRequest;
//import javax.servlet.http.HttpServletResponse;
//import java.util.Map;
//
//@Controller
//@RequestMapping
//@Slf4j
//public class BarkProcessorController {
//    @Autowired
//    private BarkProcessorService barkProcessorService;
//
//    @Autowired
//    private HttpClientUtil httpClientUtil;
//
//    @RequestMapping("/**")
//    public void process(HttpServletRequest request,
//                        HttpServletResponse response,
//                        @RequestParam(required = false) Map<String, String> requestParam) {
//        try {
//            String url = request.getRequestURI();
//            // 查看是不是配置的拦截请求
//            for (String deviceKey : DeviceConfig.ALL_DEVICES_KEY_LIST) {
//                if (url.contains(deviceKey)) {
//                    // 有配置的 key，进行处理
//                    barkProcessorService.process(deviceKey, request, response, requestParam);
//                    return;
//                }
//            }
//
//            // 其它 URL，直接转发
//            httpClientUtil.doRequest(request, response, requestParam);
//        } catch (Exception e) {
//            log.error("转发错误", e);
//            httpClientUtil.writeBodyJson(response, Result.error());
//        }
//    }
//}
