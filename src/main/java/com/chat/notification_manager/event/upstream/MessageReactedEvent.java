package com.chat.notification_manager.event.upstream;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MessageReactedEvent {
  private String senderId;
  private String recipientId;
  private String messageId;
  private String conversationId;
  private OffsetDateTime createdAt;
  private String messageContent;
  private String reaction;
}
