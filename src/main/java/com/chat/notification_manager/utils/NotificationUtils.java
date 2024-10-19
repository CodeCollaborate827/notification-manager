package com.chat.notification_manager.utils;

import com.chat.notification_manager.constant.NotificationProperties;
import com.chat.notification_manager.document.Notification;
import com.chat.notification_manager.document.User;
import com.chat.notification_manager.dto.response.NotificationDTO;
import com.chat.notification_manager.enums.NotificationType;
import com.chat.notification_manager.enums.Status;
import com.chat.notification_manager.event.Event;
import com.chat.notification_manager.event.upstream.MessageMentionedEvent;
import com.chat.notification_manager.event.upstream.MessageReactedEvent;
import com.chat.notification_manager.event.upstream.NewFriendRequestEvent;
import com.chat.notification_manager.event.upstream.UserRegistrationEvent;
import com.chat.notification_manager.exception.ApplicationException;
import com.chat.notification_manager.exception.ErrorCode;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.OffsetDateTime;
import java.util.HashMap;
import java.util.Map;
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
              return createNotification(decodedEvent, eventCreatedAt);
            })
        .onErrorResume(
            JsonProcessingException.class,
            e -> {
              log.error("Error decoding even {}", e.getMessage(), e);
              return Mono.empty();
            });
  }

  private static <T> Notification createNotification(T eventDetails, OffsetDateTime createdAt) {
    Notification.NotificationBuilder notificationBuilder = Notification.builder();
    Map<String, Object> properties = new HashMap<>();

    if (eventDetails instanceof NewFriendRequestEvent newFriendEvent) {
      notificationBuilder.userId(newFriendEvent.getRecipientId());
      notificationBuilder.type(NotificationType.NEW_FRIEND);

      properties.put(NotificationProperties.FROM_USER, newFriendEvent.getSenderId());
    } else if (eventDetails instanceof MessageMentionedEvent mentionedEvent) {
      notificationBuilder.userId(mentionedEvent.getRecipientId());
      notificationBuilder.type(NotificationType.MESSAGE_MENTIONED);

      properties.put(NotificationProperties.FROM_USER, mentionedEvent.getSenderId());
      properties.put(NotificationProperties.CONVERSATION_ID, mentionedEvent.getConversationId());
      properties.put(NotificationProperties.MESSAGE_ID, mentionedEvent.getMessageId());
    } else if (eventDetails instanceof MessageReactedEvent reactedEvent) {
      notificationBuilder.userId(reactedEvent.getRecipientId());
      notificationBuilder.type(NotificationType.MESSAGE_REACTED);

      properties.put(NotificationProperties.FROM_USER, reactedEvent.getSenderId());
      properties.put(NotificationProperties.CONVERSATION_ID, reactedEvent.getConversationId());
      properties.put(NotificationProperties.MESSAGE_ID, reactedEvent.getMessageId());
      properties.put(NotificationProperties.REACTION_TYPE, reactedEvent.getReaction());
    }

    return notificationBuilder
        .status(Status.UNREAD)
        .createdAt(createdAt)
        .properties(properties)
        .build();
  }

  private static <T> OffsetDateTime getCreatedAtFromEvent(T eventDetails) {
    return switch (eventDetails) {
      case NewFriendRequestEvent newFriendRequestEvent -> newFriendRequestEvent.getCreatedAt();
      case MessageMentionedEvent messageMentionedEvent -> messageMentionedEvent.getCreatedAt();
      case MessageReactedEvent messageReactedEvent -> messageReactedEvent.getCreatedAt();
      case null, default -> OffsetDateTime.now();
    };
  }

  public static <T> Flux<T> handleError(Throwable e) {
    log.error("Error when consuming event: {}", e.getMessage(), e);
    return Flux.empty();
  }
}
