package com.chat.notification_manager.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class MessageReactedNotificationProperties extends NotificationProperties {
  private String reactionType;
  private String senderId;
  private String messageId;
  private String conversationId;
}
