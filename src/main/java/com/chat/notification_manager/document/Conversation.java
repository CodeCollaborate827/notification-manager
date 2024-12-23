package com.chat.notification_manager.document;

import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Data
@Document(collection = "conversation")
public class Conversation {
  @Field("conversation_id")
  private String id;

  @Field("conversation_name")
  private String conversationName;

  @Field("conversation_picture")
  private String conversationPicture;
}
