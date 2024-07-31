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

  @Field("sender_id")
  private String senderId;

  @Field("receiver_id")
  private String receiverId;

  @Field("conversation_id")
  private String conversationId;

  @Field("content")
  private String content;

  @Field("title")
  private String title;

  @Field("status")
  private Status status; // unread, read

  @Field("type")
  private NotificationType type; // message, friend_request, reaction, mention, etc.

  @Field("created_at")
  @CreatedDate
  private OffsetDateTime createdAt;

  @Field("updated_at")
  @LastModifiedDate
  private OffsetDateTime updatedAt;

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
