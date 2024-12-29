package com.chat.notification_manager.dto.response;

import com.chat.notification_manager.document.notificationProperties.NotificationProperties;
import com.chat.notification_manager.enums.NotificationType;
import com.chat.notification_manager.enums.Status;
import java.util.List;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class NotificationDTO {
  private String id;
  private String userId;
  private String title;
  private String content;
  private Status status;
  private NotificationType type;
  private List<String> imageUrl;
  private NotificationProperties properties;
  private Long createdAt;
}
