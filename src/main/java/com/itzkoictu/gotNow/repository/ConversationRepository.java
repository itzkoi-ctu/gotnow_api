package com.itzkoictu.gotNow.repository;

import com.itzkoictu.gotNow.model.Conversation;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.Optional;

public interface ConversationRepository extends MongoRepository<Conversation, Long> {
    Optional<Conversation> findByAdminIdAndUserId(Long adminId, Long userId);
}
