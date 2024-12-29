package com.chat.notification_manager.event.upstream.message;

import com.chat.notification_manager.enums.MessageReaction;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MessageReactedEventData {
  private String messageSenderId;
  private String reactionSenderId;
  private String messageId;
  private String conversationId;
  private Long createdAt;
  private String messageContent;
  private MessageReaction reaction;
}
