package com.chat.notification_manager.service;

import com.chat.notification_manager.document.Notification;
import com.chat.notification_manager.dto.response.NotificationDTO;
import org.springframework.http.ResponseEntity;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface NotificationService {
  Flux<ResponseEntity<NotificationDTO>> getAllNotifications(String receiverId);

  Flux<ResponseEntity<NotificationDTO>> getAllReadNotifications(String receiverId);

  Mono<Notification> saveNotification(Notification notification);
}
