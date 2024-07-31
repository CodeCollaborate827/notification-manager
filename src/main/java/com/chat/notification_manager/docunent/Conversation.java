package com.chat.notification_manager.docunent;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Data
@Document(collection = "conversation")
public class Conversation {
  @Field("conversation_id")
  private String conversationId;

  @Field("conversation_picture")
  private String conversationPicture;
}
