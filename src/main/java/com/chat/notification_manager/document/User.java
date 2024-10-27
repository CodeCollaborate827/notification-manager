package com.chat.notification_manager.document;

import lombok.Builder;
import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Data
@Builder
@Document(collection = "user")
public class User {
  @Field("user_id")
  private String id;

  @Field("display_name")
  private String displayName;

  @Field("profile_picture")
  private String profilePicture;
}
