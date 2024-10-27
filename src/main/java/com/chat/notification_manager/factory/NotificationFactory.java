package com.chat.notification_manager.factory;

import com.chat.notification_manager.document.Notification;
import com.chat.notification_manager.enums.NotificationType;
import com.chat.notification_manager.enums.Status;
import com.chat.notification_manager.event.upstream.*;
import com.chat.notification_manager.model.*;
import java.time.OffsetDateTime;
import java.util.Map;
import java.util.function.Function;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class NotificationFactory {

  private static final Map<Class<?>, Function<Object, NotificationDetails>> EVENT_HANDLERS =
      Map.of(
          NewFriendRequestEvent.class, event -> handleFriendRequest((NewFriendRequestEvent) event),
          MessageMentionedEvent.class, event -> handleMessageMention((MessageMentionedEvent) event),
          MessageReactedEvent.class, event -> handleMessageReaction((MessageReactedEvent) event));

  public static <T> Notification createNotification(T eventDetails, OffsetDateTime createdAt) {
    if (eventDetails == null || createdAt == null) {
      throw new IllegalArgumentException("Event details and creation time cannot be null");
    }

    NotificationDetails details =
        EVENT_HANDLERS
            .getOrDefault(
                eventDetails.getClass(),
                event -> {
                  log.warn("Unsupported event type: {}", event.getClass().getSimpleName());
                  return null;
                })
            .apply(eventDetails);

    if (details == null) {
      throw new IllegalArgumentException(
          "Unable to process event type: " + eventDetails.getClass().getSimpleName());
    }

    return Notification.builder()
        .userId(details.userId())
        .type(details.type())
        .properties(details.properties())
        .status(Status.UNREAD)
        .createdAt(createdAt)
        .build();
  }

  private static NotificationDetails handleFriendRequest(NewFriendRequestEvent event) {
    return new NotificationDetails(
        event.getRecipientId(),
        NotificationType.NEW_FRIEND,
        AddFriendNotificationProperties.builder().senderId(event.getSenderId()).build());
  }

  private static NotificationDetails handleMessageMention(MessageMentionedEvent event) {
    return new NotificationDetails(
        event.getRecipientId(),
        NotificationType.MESSAGE_MENTIONED,
        MessageMentionedNotificationProperties.builder()
            .senderId(event.getSenderId())
            .conversationId(event.getConversationId())
            .messageId(event.getMessageId())
            .build());
  }

  private static NotificationDetails handleMessageReaction(MessageReactedEvent event) {
    return new NotificationDetails(
        event.getRecipientId(),
        NotificationType.MESSAGE_REACTED,
        MessageReactedNotificationProperties.builder()
            .senderId(event.getSenderId())
            .conversationId(event.getConversationId())
            .messageId(event.getMessageId())
            .reactionType(event.getReaction())
            .build());
  }

  private record NotificationDetails(
      String userId, NotificationType type, NotificationProperties properties) {}
}
