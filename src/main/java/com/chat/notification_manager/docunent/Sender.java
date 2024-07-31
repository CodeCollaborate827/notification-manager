package com.chat.notification_manager.docunent;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Data
@Document(collection = "sender")
public class Sender {
  @Field("sender_id")
  private String senderId;

  @Field("display_name")
  private String displayName;

  @Field("profile_picture")
  private String profilePicture;
}
