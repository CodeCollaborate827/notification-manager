package com.chat.notification_manager.service.implementation;

import com.chat.notification_manager.docunent.Notification;
import com.chat.notification_manager.dto.response.NotificationDTO;
import com.chat.notification_manager.exception.ApplicationException;
import com.chat.notification_manager.exception.ErrorCode;
import com.chat.notification_manager.repository.NotificationRepository;
import com.chat.notification_manager.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {
    private final NotificationRepository notificationRepository;

    @Override
    public Flux<ResponseEntity<NotificationDTO>> getAllNotifications(String receiverId) {
        // TODO: return number of new notifications
        return notificationRepository.findAllByReceiverId(receiverId)
                .flatMap(notification -> {
                    log.info("Notification found: {}", notification);
                    return Flux.just(ResponseEntity.ok(mapNotificationToDTO(notification)));
                })
                .switchIfEmpty(Flux.error(new ApplicationException(ErrorCode.NOTIFICATION_ERROR1)));
    }

    @Override
    public Flux<ResponseEntity<NotificationDTO>> getAllReadNotifications(String receiverId) {
        return notificationRepository.findAllByReceiverIdAndStatus(receiverId, Notification.Status.READ)
                .flatMap(notification -> {
                    log.info("Read notification found: {}", notification);
                    return Flux.just(ResponseEntity.ok(mapNotificationToDTO(notification)));
                })
                .switchIfEmpty(Flux.error(new ApplicationException(ErrorCode.NOTIFICATION_ERROR1)));
    }

    private NotificationDTO mapNotificationToDTO(Notification notification) {
        return NotificationDTO.builder()
                .id(notification.getId())
                .title(notification.getTitle())
                .content(notification.getContent())
                .type(notification.getType())
                .status(notification.getStatus())
                .createdAt(notification.getCreatedAt())
                .build();
    }
}
