package com.chat.notification_manager.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class MessageMentionedNotificationProperties extends NotificationProperties {
  private String senderId;
  private String messageId;
  private String conversationId;
}
