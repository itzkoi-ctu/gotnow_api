package com.itzkoictu.gotNow.controller;

import com.itzkoictu.gotNow.model.Message;
import com.itzkoictu.gotNow.service.chat.ChatService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.user.SimpUserRegistry;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
public class ChatController {
    private static final Logger logger = LoggerFactory.getLogger(ChatController.class);

    private final SimpMessagingTemplate messagingTemplate;
    private final ChatService chatService;
    private final SimpUserRegistry simpUserRegistry;


    @MessageMapping("/chat")
    public void sendMessage(@Payload Message message) {
        logger.info("📩 Tin nhắn nhận được từ {} đến {}: {}",
                message.getSenderId(), message.getReceiverId(), message.getContent());

        Long adminId = 1L;
        Long userId = (message.getSenderId().equals(adminId)) ? message.getReceiverId() : message.getSenderId();

        // 🔹 Lưu tin nhắn
        chatService.sendMessage(adminId, userId, message);

        // 🔹 Xác định topic WebSocket để gửi tin nhắn
        // 🔹 Chỉ gửi tin nhắn đến người nhận
        if (!message.getSenderId().equals(adminId)) {
            messagingTemplate.convertAndSend("/topic/admin/" + message.getSenderId(), message);
            logger.info("📤 Gửi tin nhắn đến WebSocket topic: /topic/admin/"+ message.getReceiverId());

        } else {
            messagingTemplate.convertAndSend("/topic/user/" + message.getReceiverId(), message);
            logger.info("📤 Gửi tin nhắn đến WebSocket topic: /topic/user/"+message.getReceiverId());

        }
    }

}
