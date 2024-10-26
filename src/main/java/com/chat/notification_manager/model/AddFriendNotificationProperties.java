package com.chat.notification_manager.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AddFriendNotificationProperties extends NotificationProperties {
  private String senderId;
}
