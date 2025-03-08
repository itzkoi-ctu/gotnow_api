package com.itzkoictu.gotNow.controller;

import com.itzkoictu.gotNow.dto.response.ApiResponse;
import com.itzkoictu.gotNow.model.Conversation;
import com.itzkoictu.gotNow.repository.ConversationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("${api.prefix}/chat")
@RequiredArgsConstructor
public class ChatHistoryController {
    private final ConversationRepository conversationRepository;



    // ✅ API lấy danh sách User đã chat với Admin
    @GetMapping("/history")
    public ResponseEntity<ApiResponse> getChatHistory() {
        List<Conversation> conversations= conversationRepository.findAll();
        return  ResponseEntity.ok().body(new ApiResponse(HttpStatus.OK.value(), "All chats", conversations));
    }
    @GetMapping("/get-chat")
    public ResponseEntity<ApiResponse> getUserChat(@RequestParam Long adminId, @RequestParam Long userId){
        Optional<Conversation> conversation= conversationRepository.findByAdminIdAndUserId(adminId,userId);
        return ResponseEntity.ok().body(new ApiResponse(HttpStatus.OK.value(), "Get successfully", conversation));

    }
}
