package com.chat.notification_manager.repository;

import com.chat.notification_manager.docunent.Conversation;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ConversationRepository extends ReactiveMongoRepository<Conversation, String> {}
