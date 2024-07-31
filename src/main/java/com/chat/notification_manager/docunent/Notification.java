package com.chat.notification_manager.docunent;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

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
  private String createdAt;

  @Field("updated_at")
  private String updatedAt;

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
