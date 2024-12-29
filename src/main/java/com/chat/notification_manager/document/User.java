package com.chat.notification_manager.document;

import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Builder
@Document(collection = "user")
public class User {
  @Id private String id;
  private String displayName;
  private String avatar;
}
