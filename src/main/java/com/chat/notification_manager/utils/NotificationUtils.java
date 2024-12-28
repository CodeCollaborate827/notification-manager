package com.chat.notification_manager.utils;

import com.chat.notification_manager.document.Conversation;
import com.chat.notification_manager.document.Notification;
import com.chat.notification_manager.document.User;
import com.chat.notification_manager.dto.response.NotificationDTO;
import com.chat.notification_manager.enums.MessageReaction;
import com.chat.notification_manager.enums.NotificationType;
import com.chat.notification_manager.enums.Status;
import com.chat.notification_manager.event.downstream.NotificationEvent;
import com.chat.notification_manager.event.upstream.message.MessageMentionedEventData;
import com.chat.notification_manager.event.upstream.message.MessageReactedEventData;
import com.chat.notification_manager.event.upstream.userContact.FriendRequestAcceptedEventData;
import com.chat.notification_manager.event.upstream.userContact.NewFriendRequestEventData;
import com.chat.notification_manager.model.AddFriendNotificationProperties;
import com.chat.notification_manager.model.MessageMentionedNotificationProperties;
import com.chat.notification_manager.model.MessageReactedNotificationProperties;
import com.chat.notification_manager.model.NotificationProperties;
import java.time.Instant;
import java.util.List;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class NotificationUtils {

  private record NotificationDetails(
      String userId, NotificationType type, NotificationProperties properties) {}

  public static NotificationDTO createNotificationDTO(
      Notification notification, User sender, Conversation conversation) {

    String conversationName =
        ConversationUtils.getConversationName(conversation, notification.getUserId(), null);
    List<String> conversationAvatar =
        ConversationUtils.getConversationAvatar(conversation, notification.getUserId(), null);
    String title =
        NotificationMessageGenerator.generateTitle(
            notification.getType(), sender, conversationName);
    String content =
        NotificationMessageGenerator.generateContent(
            notification.getType(), sender, conversationName);

    return NotificationDTO.builder()
        .id(notification.getId())
        .type(notification.getType())
        .userId(notification.getUserId())
        .title(title)
        .content(content)
        .status(notification.getStatus())
        .imageUrl(conversationAvatar)
        .properties(notification.getProperties())
        .createdAt(notification.getCreatedAt())
        .build();
  }

  public static NotificationEvent createNotificationEvent(NotificationDTO notification) {
    NotificationEvent build =
        NotificationEvent.builder()
            .messageType(notification.getType())
            .recipientId(notification.getUserId())
            .notificationId(notification.getId())
            .data(notification)
            .build();

    return build;
  }

  public static Notification createNotification(MessageMentionedEventData messageMentionedEvent) {
    NotificationDetails notificationDetails =
        new NotificationDetails(
            messageMentionedEvent.getRecipientId(),
            NotificationType.NOTIFICATION_MESSAGE_MENTIONED,
            MessageMentionedNotificationProperties.builder()
                .senderId(messageMentionedEvent.getSenderId())
                .conversationId(messageMentionedEvent.getConversationId())
                .messageId(messageMentionedEvent.getMessageId())
                .build());

    return createNotificationFromDetails(notificationDetails);
  }

  public static Notification createNotification(MessageReactedEventData messageReactedEvent) {
    NotificationDetails notificationDetails =
        new NotificationDetails(
            messageReactedEvent.getSenderId(),
            NotificationType.NOTIFICATION_MESSAGE_REACTED,
            MessageReactedNotificationProperties.builder()
                .senderId(messageReactedEvent.getSenderId())
                .conversationId(messageReactedEvent.getConversationId())
                .messageId(messageReactedEvent.getMessageId())
                .reaction(
                    MessageReaction.getMessageReaction(messageReactedEvent.getReaction().name()))
                .build());
    return createNotificationFromDetails(notificationDetails);
  }

  public static Notification createNotification(NewFriendRequestEventData event) {
    NotificationDetails notificationDetails =
        new NotificationDetails(
            event.getRecipientId(),
            NotificationType.NOTIFICATION_NEW_FRIEND_REQUEST,
            AddFriendNotificationProperties.builder().senderId(event.getSenderId()).build());
    return createNotificationFromDetails(notificationDetails);
  }

  public static Notification createNotification(FriendRequestAcceptedEventData event) {
    NotificationDetails notificationDetails =
        new NotificationDetails(
            event.getSenderId(),
            NotificationType.NOTIFICATION_FRIEND_REQUEST_ACCEPTED,
            AddFriendNotificationProperties.builder().senderId(event.getSenderId()).build());
    return createNotificationFromDetails(notificationDetails);
  }

  public static String getSenderIdFromNotificationProps(Notification notification) {
    return switch (notification.getType()) {
      case NOTIFICATION_NEW_FRIEND_REQUEST ->
          ((AddFriendNotificationProperties) notification.getProperties()).getSenderId();
      case NOTIFICATION_MESSAGE_MENTIONED ->
          ((MessageMentionedNotificationProperties) notification.getProperties()).getSenderId();
      case NOTIFICATION_MESSAGE_REACTED ->
          ((MessageReactedNotificationProperties) notification.getProperties()).getSenderId();
      default -> null;
    };
  }

  public static String getConversationIdFromNotificationProps(Notification notification) {
    return switch (notification.getType()) {
      case NOTIFICATION_NEW_FRIEND_REQUEST -> null;
      case NOTIFICATION_MESSAGE_MENTIONED ->
          ((MessageMentionedNotificationProperties) notification.getProperties())
              .getConversationId();
      case NOTIFICATION_MESSAGE_REACTED ->
          ((MessageReactedNotificationProperties) notification.getProperties()).getConversationId();
      default -> null;
    };
  }

  private static Notification createNotificationFromDetails(
      NotificationDetails notificationDetails) {
    return Notification.builder()
        .userId(notificationDetails.userId())
        .type(notificationDetails.type())
        .properties(notificationDetails.properties())
        .status(Status.UNREAD)
        .createdAt(Instant.now().getEpochSecond())
        .build();
  }
}
