package com.chat.notification_manager.utils;

import com.chat.notification_manager.document.Notification;
import com.chat.notification_manager.document.User;
import com.chat.notification_manager.dto.response.NotificationDTO;
import com.chat.notification_manager.event.Event;
import com.chat.notification_manager.event.upstream.MessageMentionedEvent;
import com.chat.notification_manager.event.upstream.MessageReactedEvent;
import com.chat.notification_manager.event.upstream.NewFriendRequestEvent;
import com.chat.notification_manager.event.upstream.UserRegistrationEvent;
import com.chat.notification_manager.exception.ApplicationException;
import com.chat.notification_manager.exception.ErrorCode;
import com.chat.notification_manager.factory.NotificationFactory;
import com.chat.notification_manager.model.AddFriendNotificationProperties;
import com.chat.notification_manager.model.MessageMentionedNotificationProperties;
import com.chat.notification_manager.model.MessageReactedNotificationProperties;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.OffsetDateTime;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Slf4j
public class NotificationUtils {

  private NotificationUtils() {}

  public static Mono<User> mapToUser(UserRegistrationEvent userRegistrationEvent) {
    return Mono.just(Utils.convertToUser(userRegistrationEvent));
  }

  public static Event createEvent(NotificationDTO notificationDTO, ObjectMapper objectMapper) {
    DecodeUtil<NotificationDTO> decodeUtil = new DecodeUtil<>(objectMapper);
    try {
      return Event.builder().payloadBase64(decodeUtil.encode(notificationDTO)).build();
    } catch (JsonProcessingException e) {
      throw new ApplicationException(ErrorCode.EVENT_ERROR1);
    }
  }

  public static <T> Mono<Notification> decodeAndCreateNotification(
      String payloadBase64, Class<T> clazz, ObjectMapper objectMapper) {
    DecodeUtil<T> decodeUtilClazz = new DecodeUtil<>(objectMapper);

    return Mono.fromCallable(
            () -> {
              T decodedEvent = decodeUtilClazz.decode(payloadBase64, clazz);
              OffsetDateTime eventCreatedAt = getCreatedAtFromEvent(decodedEvent);
              return NotificationFactory.createNotification(decodedEvent, eventCreatedAt);
            })
        .onErrorResume(
            JsonProcessingException.class,
            e -> {
              log.error("Error decoding even {}", e.getMessage(), e);
              return Mono.empty();
            });
  }

  private static <T> OffsetDateTime getCreatedAtFromEvent(T eventDetails) {
    return switch (eventDetails) {
      case NewFriendRequestEvent newFriendRequestEvent -> newFriendRequestEvent.getCreatedAt();
      case MessageMentionedEvent messageMentionedEvent -> messageMentionedEvent.getCreatedAt();
      case MessageReactedEvent messageReactedEvent -> messageReactedEvent.getCreatedAt();
      case null, default -> OffsetDateTime.now();
    };
  }

  public static String getSenderIdFromNotificationProps(Notification notification) {
    return switch (notification.getType()) {
      case NEW_FRIEND ->
          ((AddFriendNotificationProperties) notification.getProperties()).getSenderId();
      case MESSAGE_MENTIONED ->
          ((MessageMentionedNotificationProperties) notification.getProperties()).getSenderId();
      case MESSAGE_REACTED ->
          ((MessageReactedNotificationProperties) notification.getProperties()).getSenderId();
      default -> null;
    };
  }

  public static String getConversationIdFromNotificationProps(Notification notification) {
    return switch (notification.getType()) {
      case NEW_FRIEND -> null;
      case MESSAGE_MENTIONED ->
          ((MessageMentionedNotificationProperties) notification.getProperties())
              .getConversationId();
      case MESSAGE_REACTED ->
          ((MessageReactedNotificationProperties) notification.getProperties()).getConversationId();
      default -> null;
    };
  }

  public static <T> Flux<T> handleError(Throwable e) {
    log.error("Error when consuming event: {}", e.getMessage(), e);
    return Flux.empty();
  }
}
