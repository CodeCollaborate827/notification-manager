package com.chat.notification_manager.docunent;

import lombok.Data;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.OffsetDateTime;

@Data
@Document(collection = "notification")
public class Notification {
  @Id private String id;

  @Field("user_id")
  private String userId;

  @Field("content")
  private String content;

  @Field("title")
  private String title;

  @Field("status")
  private Status status; // unread, read

  @Field("type")
  private NotificationType type; // message, friend_request, reaction, mention, etc.

  @Field("properties")
  private Object properties;

  @Field("created_at")
  @CreatedDate
  private OffsetDateTime createdAt;

  @Field("updated_at")
  @LastModifiedDate
  private OffsetDateTime updatedAt;

  private Object data;



  public enum Status {
    UNREAD,
    READ
  }

  public enum NotificationType {
    MESSAGE,
    FRIEND_REQUEST,
    REACTION,
    MENTION
  }
}
