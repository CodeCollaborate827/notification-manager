package com.chat.notification_manager.event.downstream;

import com.chat.notification_manager.enums.NotificationType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class NotificationEvent {
    private NotificationType messageType;
    private String notificationId;
    private String recipientId;
    private Object data;
}
