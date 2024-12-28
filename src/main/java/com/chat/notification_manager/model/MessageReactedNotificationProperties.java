package com.chat.notification_manager.model;

import com.chat.notification_manager.enums.MessageReaction;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class MessageReactedNotificationProperties extends NotificationProperties {
  private MessageReaction reaction;
  private String senderId;
  private String messageId;
  private String conversationId;
}
