package com.chat.notification_manager.docunent;

import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Data
@Document(collection = "user")
public class User {
  @Field("user_id")
  private String id;

  @Field("display_name")
  private String displayName;

  @Field("profile_picture")
  private String profilePicture;
}
