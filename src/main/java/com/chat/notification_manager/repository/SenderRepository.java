package com.chat.notification_manager.repository;

import com.chat.notification_manager.docunent.User;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SenderRepository extends ReactiveMongoRepository<User, String> {}
