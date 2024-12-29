package com.chat.notification_manager.event.upstream.message;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MessageMentionedNotificationTriggerEvent {
  private String messageSenderId;
  private String mentionedMemberId;
  private String messageId;
  private String conversationId;
  private Long createdAt;
  private String messageContent;
}
