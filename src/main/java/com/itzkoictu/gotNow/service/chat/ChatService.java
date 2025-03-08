package com.itzkoictu.gotNow.service.chat;

import com.itzkoictu.gotNow.model.Conversation;
import com.itzkoictu.gotNow.model.Message;
import com.itzkoictu.gotNow.repository.ConversationRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ChatService {
    private final ConversationRepository conversationRepository;

    public Conversation getOrCreateConversation(Long adminId, Long userId) {
        return conversationRepository.findByAdminIdAndUserId(adminId, userId)
                .orElseGet(() -> {
                    Conversation newConversation = new Conversation();
                    newConversation.setAdminId(adminId);
                    newConversation.setUserId(userId);
                    return conversationRepository.save(newConversation);
                });
    }

    public void sendMessage(Long adminId, Long userId, Message message) {
        Conversation conversation = getOrCreateConversation(adminId, userId);
        conversation.getMessages().add(message);
        conversationRepository.save(conversation);
    }

    public Conversation getConversationByUserId(Long adminId, Long userId){
        return conversationRepository.findByAdminIdAndUserId(adminId, userId)
                .orElseThrow(() -> new EntityNotFoundException("Conversation not found!"));
    }
}
