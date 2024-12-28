package com.chat.notification_manager.event.upstream.conversation;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ConversationEvent {

  public enum ConversationEventType {
    CONVERSATION_NEW,
    CONVERSATION_GROUP_MEMBER_ADDED,
    CONVERSATION_GROUP_MEMBER_REMOVED,
    CONVERSATION_NAME_UPDATED,
    CONVERSATION_IMAGE_UPDATED
  }

  private ConversationEventType messageType;
  private String conversationId;
  private Object data; // depends on the messageType the data would be different
  private List<String> conversationMemberIds;
  private long timestamp;
}
