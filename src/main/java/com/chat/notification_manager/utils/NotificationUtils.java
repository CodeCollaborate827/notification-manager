package com.chat.notification_manager.utils;

import com.chat.notification_manager.document.Conversation;
import com.chat.notification_manager.document.Notification;
import com.chat.notification_manager.document.User;
import com.chat.notification_manager.document.notificationProperties.AddFriendNotificationProperties;
import com.chat.notification_manager.document.notificationProperties.MessageMentionedNotificationProperties;
import com.chat.notification_manager.document.notificationProperties.MessageReactedNotificationProperties;
import com.chat.notification_manager.document.notificationProperties.NotificationProperties;
import com.chat.notification_manager.dto.response.NotificationDTO;
import com.chat.notification_manager.enums.MessageReaction;
import com.chat.notification_manager.enums.NotificationType;
import com.chat.notification_manager.enums.Status;
import com.chat.notification_manager.event.downstream.NotificationEvent;
import com.chat.notification_manager.event.upstream.message.MessageMentionedNotificationTriggerEvent;
import com.chat.notification_manager.event.upstream.message.MessageReactedNotificationTriggerEvent;
import com.chat.notification_manager.event.upstream.userContact.FriendRequestAcceptedEventData;
import com.chat.notification_manager.event.upstream.userContact.NewFriendRequestEventData;
import java.time.Instant;
import java.util.List;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class NotificationUtils {

  private record NotificationDetails(
      String userId, NotificationType type, NotificationProperties properties) {}

  public static NotificationDTO createNotificationDTO(
      Notification notification, User sender, Conversation conversation) {
    String conversationName;
    List<String> conversationAvatar;

    // If the notification is not related to a conversation, the conversation will be null
    // In this case, we will use the sender's name and profile picture as the conversation name and
    // avatar
    if (conversation != null) {
      conversationName =
          ConversationUtils.getConversationName(conversation, notification.getUserId(), null);
      conversationAvatar =
          ConversationUtils.getConversationAvatar(conversation, notification.getUserId(), null);
    } else {
      conversationName = sender.getDisplayName();
      conversationAvatar = List.of(sender.getAvatar());
    }

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

  public static Notification createNotification(MessageMentionedNotificationTriggerEvent messageMentionedEvent) {
    NotificationDetails notificationDetails =
        new NotificationDetails(
            messageMentionedEvent.getMentionedMemberId(),
            NotificationType.NOTIFICATION_MESSAGE_MENTIONED,
            MessageMentionedNotificationProperties.builder()
                .messageSenderId(messageMentionedEvent.getMessageSenderId())
                .conversationId(messageMentionedEvent.getConversationId())
                .messageId(messageMentionedEvent.getMessageId())
                .build());

    return createNotificationFromDetails(notificationDetails);
  }

  public static Notification createNotification(MessageReactedNotificationTriggerEvent messageReactedEvent) {
    NotificationDetails notificationDetails =
        new NotificationDetails(
            messageReactedEvent.getMessageSenderId(),
            NotificationType.NOTIFICATION_MESSAGE_REACTED,
            MessageReactedNotificationProperties.builder()
                .reactionSenderId(messageReactedEvent.getReactionSenderId())
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
            event.getRequestRecipientId(),
            NotificationType.NOTIFICATION_NEW_FRIEND_REQUEST,
            AddFriendNotificationProperties.builder()
                .requestSenderId(event.getRequestSenderId())
                .requestRecipientId(event.getRequestRecipientId())
                .build());
    return createNotificationFromDetails(notificationDetails);
  }

  public static Notification createNotification(FriendRequestAcceptedEventData event) {
    NotificationDetails notificationDetails =
        new NotificationDetails(
            event.getRequestSenderId(),
            NotificationType.NOTIFICATION_FRIEND_REQUEST_ACCEPTED,
            AddFriendNotificationProperties.builder()
                .requestSenderId(event.getRequestSenderId())
                .requestRecipientId(event.getRequestRecipientId())
                .build());
    return createNotificationFromDetails(notificationDetails);
  }

  public static String getSenderIdFromNotificationProps(Notification notification) {
    return switch (notification.getType()) {
      case NOTIFICATION_NEW_FRIEND_REQUEST ->
          ((AddFriendNotificationProperties) notification.getProperties()).getRequestSenderId();
      case NOTIFICATION_MESSAGE_MENTIONED ->
          ((MessageMentionedNotificationProperties) notification.getProperties())
              .getMessageSenderId();
      case NOTIFICATION_MESSAGE_REACTED -> notification.getUserId();
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
