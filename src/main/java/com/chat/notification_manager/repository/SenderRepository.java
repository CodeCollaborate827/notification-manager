package com.chat.notification_manager.repository;

import com.chat.notification_manager.docunent.Sender;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SenderRepository extends ReactiveCrudRepository<Sender, String> {}
