package com.chat.notification_manager.utils;

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
    Notification.NotificationData.NotificationDataBuilder dataBuilder =
        Notification.NotificationData.builder();
    Notification.NotificationBuilder notificationBuilder = Notification.builder();

    if (eventDetails instanceof NewFriendRequestEvent newFriendEvent) {
      notificationBuilder.userId(newFriendEvent.getRecipientId());
      notificationBuilder.type(NotificationType.NEW_FRIEND);
      dataBuilder.fromUser(newFriendEvent.getSenderId());
    } else if (eventDetails instanceof MessageMentionedEvent mentionedEvent) {
      notificationBuilder.userId(mentionedEvent.getRecipientId());
      notificationBuilder.type(NotificationType.MESSAGE_MENTIONED);
      dataBuilder
          .fromUser(mentionedEvent.getSenderId())
          .conversationId(mentionedEvent.getConversationId())
          .messageId(mentionedEvent.getMessageId());
    } else if (eventDetails instanceof MessageReactedEvent reactedEvent) {
      notificationBuilder.userId(reactedEvent.getRecipientId());
      notificationBuilder.type(NotificationType.MESSAGE_REACTED);
      dataBuilder
          .fromUser(reactedEvent.getSenderId())
          .conversationId(reactedEvent.getConversationId())
          .messageId(reactedEvent.getMessageId())
          .reactionType(reactedEvent.getReaction());
    }

    return notificationBuilder
        .status(Status.UNREAD)
        .createdAt(createdAt)
        .data(dataBuilder.build())
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
