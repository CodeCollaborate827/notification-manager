package com.chat.notification_manager.dto.response;

import com.chat.notification_manager.enums.NotificationType;
import com.chat.notification_manager.enums.Status;
import lombok.Builder;
import lombok.Data;

import java.time.OffsetDateTime;

@Data
@Builder
public class NotificationDTO {
    private String id;
    private String title;
    private String content;
    private Status status;
    private NotificationType type;
    private String imageUrl;
    private OffsetDateTime createdAt;
}
