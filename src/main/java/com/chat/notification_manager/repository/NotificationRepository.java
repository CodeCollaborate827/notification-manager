package com.chat.notification_manager.repository;

import com.chat.notification_manager.docunent.Notification;
import com.chat.notification_manager.enums.Status;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

@Repository
public interface NotificationRepository extends ReactiveMongoRepository<Notification, String> {
    Flux<Notification> findAllByUserId(String receiverId);

    Flux<Notification> findAllByUserIdAndStatus(String receiverId, Status status);
}
