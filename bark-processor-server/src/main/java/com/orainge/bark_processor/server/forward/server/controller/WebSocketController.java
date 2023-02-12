//package com.orainge.bark_processor.server.forward.server.controller;
//
//import com.orainge.bark_processor.server.forward.server.service.WebSocketService;
//import com.orainge.websocket_forward.vo.Result;
//import org.springframework.http.HttpStatus;
//import org.springframework.web.bind.annotation.PathVariable;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RequestParam;
//import org.springframework.web.bind.annotation.RestController;
//
//import javax.annotation.Resource;
//import javax.servlet.http.HttpServletResponse;
//
//@RestController
//public class WebSocketController {
//    @Resource
//    private WebSocketService websocketService;
//
//    @RequestMapping("/send/{clientId}")
//    public Result send(@PathVariable String clientId,
//                       @RequestParam(name = "requireReply", required = false) String requireReply,
//                       @RequestParam(name = "text") String text,
//                       HttpServletResponse response) {
//        try {
//            return websocketService.send(clientId, requireReply, text);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        response.setStatus(HttpStatus.BAD_GATEWAY.value());
//        return null;
//    }
//}
