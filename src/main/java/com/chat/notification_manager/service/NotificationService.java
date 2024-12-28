package com.chat.notification_manager.service;

import com.chat.notification_manager.document.Conversation;
import com.chat.notification_manager.document.Notification;
import com.chat.notification_manager.dto.response.NotificationDTO;
import com.chat.notification_manager.event.downstream.NotificationEvent;
import com.chat.notification_manager.event.upstream.conversation.ConversationEvent;
import com.chat.notification_manager.event.upstream.message.MessageMentionedEventData;
import com.chat.notification_manager.event.upstream.message.MessageReactedEventData;
import com.chat.notification_manager.event.upstream.userContact.FriendRequestAcceptedEventData;
import com.chat.notification_manager.event.upstream.userContact.NewFriendRequestEventData;
import org.springframework.http.ResponseEntity;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface NotificationService {
  Flux<ResponseEntity<NotificationDTO>> getAllNotifications(String receiverId);

  Flux<ResponseEntity<NotificationDTO>> getAllReadNotifications(String receiverId);

  Mono<Notification> saveNotification(Notification notification);

  Mono<NotificationEvent> processMessageMentionedEvent(
      MessageMentionedEventData messageMentionedEvent);

  Mono<NotificationEvent> processMessageReactedEvent(MessageReactedEventData messageReactedEvent);

  Mono<NotificationEvent> processFriendRequestNotification(
      NewFriendRequestEventData newFriendRequestEvent);

  Mono<NotificationEvent> processFriendRequestAcceptedNotification(
      FriendRequestAcceptedEventData friendRequestAcceptedEvent);

  Mono<Conversation> processConversationEvent(ConversationEvent conversationEvent);
}
