package com.chat.notification_manager.document.notificationProperties;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AddFriendNotificationProperties extends NotificationProperties {
  private String requestSenderId;
  private String requestRecipientId;
}
