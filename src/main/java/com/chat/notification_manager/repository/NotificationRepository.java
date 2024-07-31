package com.chat.notification_manager.repository;

import com.chat.notification_manager.docunent.Notification;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface NotificationRepository extends ReactiveCrudRepository<Notification, String> {}
