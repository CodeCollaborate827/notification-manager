package com.chat.notification_manager.utils;

import com.chat.notification_manager.document.Conversation;
import com.chat.notification_manager.document.Notification;
import com.chat.notification_manager.document.User;
import com.chat.notification_manager.dto.response.NotificationDTO;
import com.chat.notification_manager.enums.NotificationType;
import com.chat.notification_manager.event.upstream.UserRegistrationEvent;
import lombok.extern.slf4j.Slf4j;

import java.time.OffsetDateTime;

@Slf4j
public class Utils {
  private Utils() {}

  public static NotificationDTO mapNotificationToDTO(
      Notification notification, User user, Conversation conversation) {
    return NotificationDTO.builder()
        .id(notification.getId())
        .userId(notification.getUserId())
        .title(
            NotificationMessageGenerator.generateTitle(notification.getType(), user, conversation))
        .content(
            NotificationMessageGenerator.generateContent(
                notification.getType(), user, conversation))
        .status(notification.getStatus())
        .type(notification.getType())
        .imageUrl(
            notification.getType().equals(NotificationType.NEW_FRIEND)
                ? user.getProfilePicture()
                : conversation.getConversationPicture() // Check if conversation exists
            )
        .properties(notification.getProperties())
        .createdAt(notification.getCreatedAt())
        .build();
  }

  public static User convertToUser(UserRegistrationEvent userRegistrationEvent) {
    return User.builder()
        .id(userRegistrationEvent.getUserId())
        .displayName(userRegistrationEvent.getDisplayName())
        .profilePicture(userRegistrationEvent.getAvatar())
        .build();
  }

  public static OffsetDateTime convertEpochToOffsetDateTime(long epoch) {
    return OffsetDateTime.ofInstant(java.time.Instant.ofEpochMilli(epoch), java.time.ZoneOffset.UTC);
  }
}
