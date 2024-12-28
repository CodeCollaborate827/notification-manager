package com.chat.notification_manager.model;

import com.chat.notification_manager.event.upstream.MessageReactedEvent;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class MessageReactedNotificationProperties extends NotificationProperties {
  private MessageReactedEvent.Reaction reaction;
  private String senderId;
  private String messageId;
  private String conversationId;
}
