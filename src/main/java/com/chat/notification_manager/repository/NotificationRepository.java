package com.chat.notification_manager.repository;

import com.chat.notification_manager.docunent.Notification;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

@Repository
public interface NotificationRepository extends ReactiveCrudRepository<Notification, String> {
    Flux<Notification> findAllByReceiverId(String receiverId);

    Flux<Notification> findAllByReceiverIdAndStatus(String receiverId, Notification.Status status);
}
