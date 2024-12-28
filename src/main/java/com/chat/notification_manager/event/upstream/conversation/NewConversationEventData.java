package com.chat.notification_manager.event.upstream.conversation;

import com.chat.notification_manager.enums.ConversationType;
import com.chat.notification_manager.model.ConversationMember;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NewConversationEventData {
  private String conversationId;
  private Map<String, ConversationMember> memberDetails;
  private ConversationType conversationType;
  private String groupConversationName;
  private String groupConversationAvatar;
  private Long createdAt;
}
