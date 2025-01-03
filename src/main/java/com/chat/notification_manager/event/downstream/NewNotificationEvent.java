package com.chat.notification_manager.event.downstream;

import com.chat.notification_manager.enums.NotificationType;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NewNotificationEvent {
  private NotificationType type;
  private String id;
  private String title;
  private String image;
  private String content;
  private Long createdAt;
  private Map<String, Object> properties;
}
