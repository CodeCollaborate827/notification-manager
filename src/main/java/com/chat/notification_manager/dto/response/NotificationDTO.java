package com.chat.notification_manager.dto.response;

import com.chat.notification_manager.docunent.Notification;
import lombok.Builder;
import lombok.Data;

import java.time.OffsetDateTime;

@Data
@Builder
public class NotificationDTO {
    private String id;
    private String title;
    private String content;
    private Notification.NotificationType type;
    private Notification.Status status;
    private String url;
    private OffsetDateTime createdAt;
}
