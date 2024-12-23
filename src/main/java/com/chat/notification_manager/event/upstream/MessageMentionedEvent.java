package com.chat.notification_manager.event.upstream;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MessageMentionedEvent {
  private String senderId;
  private String recipientId;
  private String messageId;
  private String conversationId;
  private Long createdAt;
  private String messageContent;
}
