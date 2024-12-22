package com.chat.notification_manager.document;

import com.chat.notification_manager.enums.NotificationType;
import com.chat.notification_manager.enums.Status;
import com.chat.notification_manager.model.NotificationProperties;
import java.time.Instant;
import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

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
  @Builder.Default
  private Long createdAt = Instant.now().getEpochSecond(); // When the notification was created

  @Field("properties")
  private NotificationProperties properties; // Additional data for the notification
}
