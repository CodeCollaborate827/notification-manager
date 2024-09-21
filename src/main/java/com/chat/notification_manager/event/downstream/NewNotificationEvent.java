package com.chat.notification_manager.event.downstream;

import com.chat.notification_manager.enums.NotificationType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NewNotificationEvent {
    private NotificationType notificationType;
    private String notificationId;
    private String notificationTitle;
    private String notificationImage;
    private String notificationContent;
    private OffsetDateTime createdAt;


}
