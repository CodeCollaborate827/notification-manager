package com.chat.notification_manager.service;

import com.chat.notification_manager.docunent.Notification;
import com.chat.notification_manager.dto.response.NotificationDTO;
import org.springframework.http.ResponseEntity;
import reactor.core.publisher.Flux;

public interface NotificationService {
  Flux<ResponseEntity<NotificationDTO>> getAllNotifications(String receiverId);

  Flux<ResponseEntity<NotificationDTO>> getAllReadNotifications(String receiverId);

  void saveNotification(Notification notification);
}
