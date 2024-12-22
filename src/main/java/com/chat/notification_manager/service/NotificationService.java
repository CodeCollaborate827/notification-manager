package com.chat.notification_manager.service;

import com.chat.notification_manager.document.Notification;
import com.chat.notification_manager.dto.response.NotificationDTO;
import com.chat.notification_manager.event.downstream.NotificationEvent;
import com.chat.notification_manager.event.upstream.FriendRequestAcceptedEvent;
import com.chat.notification_manager.event.upstream.MessageMentionedEvent;
import com.chat.notification_manager.event.upstream.MessageReactedEvent;
import com.chat.notification_manager.event.upstream.NewFriendRequestEvent;
import org.springframework.http.ResponseEntity;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface NotificationService {
  Flux<ResponseEntity<NotificationDTO>> getAllNotifications(String receiverId);

  Flux<ResponseEntity<NotificationDTO>> getAllReadNotifications(String receiverId);

  Mono<Notification> saveNotification(Notification notification);

  Mono<NotificationEvent> processMessageMentionedEvent(MessageMentionedEvent messageMentionedEvent);

  Mono<NotificationEvent> processMessageReactedEvent(MessageReactedEvent messageReactedEvent);

  Mono<NotificationEvent> processFriendRequestNotification(NewFriendRequestEvent newFriendRequestEvent);

  Mono<NotificationEvent> processFriendRequestAcceptedNotification(FriendRequestAcceptedEvent friendRequestAcceptedEvent);

}
