package com.chat.notification_manager.document.notificationProperties;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class MessageMentionedNotificationProperties extends NotificationProperties {
  private String messageSenderId;
  private String messageId;
  private String conversationId;
}
