package com.chat.notification_manager.document;

import com.chat.notification_manager.enums.NotificationType;
import com.chat.notification_manager.enums.Status;
import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.OffsetDateTime;
import java.util.Map;

@Data
@Document(collection = "notification")
@Builder
public class Notification {
  @Id private String id;

  @Field("user_id")
  private String userId; // User receiving the notification

  @Field("type")
  private NotificationType type; // Enum for notification type

  @Field("status")
  private Status status; // Read status of the notification

  @Field("created_at")
  @CreatedDate
  private OffsetDateTime createdAt; // When the notification was created

  @Field("properties")
  private Map<String, Object> properties; // Additional data for the notification
}
