package com.itzkoictu.gotNow.service.chat;

import com.itzkoictu.gotNow.dto.response.ConversationResponse;
import com.itzkoictu.gotNow.model.Conversation;
import com.itzkoictu.gotNow.model.Message;
import com.itzkoictu.gotNow.model.User;
import com.itzkoictu.gotNow.repository.ConversationRepository;
import com.itzkoictu.gotNow.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ChatService {
    private final ConversationRepository conversationRepository;
    private final UserRepository userRepository;

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

    public List<ConversationResponse> getChatHistory(){
        List<Conversation> conversations= conversationRepository.findAll();
        List<ConversationResponse> conversationResponses=conversations.stream().map(this::fromConversation).toList();
        return conversationResponses;

    }

    public ConversationResponse fromConversation(Conversation conversation){
        User user= userRepository.findById(conversation.getUserId())
                .orElseThrow(() -> new EntityNotFoundException("User not found!"));
        return ConversationResponse.builder()
                .id(conversation.getId())
                .admin("gotNow.com")
                .avatarUser(user.getAvatarUrl())
                .username(user.getFirstName()+ " "+ user.getLastName())
                .adminId(conversation.getAdminId())
                .userId(conversation.getUserId())
                .lastMessage(getLastMessage(conversation.getId()))
                .build();
    }

    public Message getLastMessage(String conversationId){
        Optional<Conversation> conversation= conversationRepository.findById(conversationId);
        if (conversation.isPresent() && !conversation.get().getMessages().isEmpty()) {
            List<Message> messages = conversation.get().getMessages();
            return messages.get(messages.size() - 1); // Lấy tin nhắn cuối cùng
        }
        return null;
    }
}
