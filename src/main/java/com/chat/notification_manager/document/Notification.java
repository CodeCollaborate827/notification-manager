package com.chat.notification_manager.document;

import com.chat.notification_manager.document.notificationProperties.NotificationProperties;
import com.chat.notification_manager.enums.NotificationType;
import com.chat.notification_manager.enums.Status;
import java.time.Instant;
import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document(collection = "notification")
@Builder
public class Notification {
  @Id private String id;

  private String userId; // User receiving the notification

  private NotificationType type; // Enum for notification type

  private Status status; // Read status of the notification

  @Builder.Default
  private Long createdAt = Instant.now().getEpochSecond(); // When the notification was created

  private NotificationProperties properties; // Additional data for the notification
}
