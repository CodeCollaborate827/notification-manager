package com.chat.notification_manager.document;

import com.chat.notification_manager.enums.ConversationType;
import com.chat.notification_manager.model.ConversationMember;
import java.time.OffsetDateTime;
import java.util.Map;
import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document(collection = "conversation")
@Builder
public class Conversation {
  @Id private String id;

  private Map<String, ConversationMember> memberDetails;

  private ConversationType conversationType;

  private String groupConversationName;

  private String groupConversationAvatar;

  @Builder.Default private Long createdAt = OffsetDateTime.now().toEpochSecond();
}
