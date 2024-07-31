package com.chat.notification_manager.repository;

import com.chat.notification_manager.docunent.Sender;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SenderRepository extends ReactiveMongoRepository<Sender, String> {}
