package com.chat.notification_manager.service.implementation;

import com.chat.notification_manager.docunent.Notification;
import com.chat.notification_manager.dto.response.NotificationDTO;
import com.chat.notification_manager.enums.NotificationType;
import com.chat.notification_manager.enums.Status;
import com.chat.notification_manager.exception.ApplicationException;
import com.chat.notification_manager.exception.ErrorCode;
import com.chat.notification_manager.repository.ConversationRepository;
import com.chat.notification_manager.repository.NotificationRepository;
import com.chat.notification_manager.repository.UserRepository;
import com.chat.notification_manager.service.NotificationService;
import com.chat.notification_manager.utils.NotificationMessageGenerator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {
    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;
    private final ConversationRepository conversationRepository;

    @Override
    public Flux<ResponseEntity<NotificationDTO>> getAllNotifications(String userId) {
        // TODO: return number of new notifications
        return notificationRepository.findAllByUserId(userId)
                .flatMap(notification -> {
                    log.info("Notification found: {}", notification);
                    return mapNotificationToDTO(notification);
                })
                .map(ResponseEntity::ok)
                .switchIfEmpty(Mono.error(new ApplicationException(ErrorCode.NOTIFICATION_ERROR1)));
    }

    @Override
    public Flux<ResponseEntity<NotificationDTO>> getAllReadNotifications(String userId) {
        return notificationRepository.findAllByUserIdAndStatus(userId, Status.READ)
                .flatMap(notification -> {
                    log.info("Read notification found: {}", notification);
                    return mapNotificationToDTO(notification);
                })
                .map(ResponseEntity::ok)
                .switchIfEmpty(Mono.error(new ApplicationException(ErrorCode.NOTIFICATION_ERROR1)));
    }

    private Mono<NotificationDTO> mapNotificationToDTO(Notification notification) {
        Notification.NotificationData data = notification.getData();
        return Mono.zip(
                        userRepository.findById(data.getFromUser()),
                        conversationRepository.findById(data.getConversationId())
                )
                .map(tuple -> {
                    log.info("User and conversation found: {}", tuple);

                    return NotificationDTO.builder()
                            .id(notification.getId())
                            .title(NotificationMessageGenerator.generateTitle(notification.getType(), tuple.getT1(), tuple.getT2()))
                            .content(NotificationMessageGenerator.generateContent(notification.getType(), tuple.getT1(), tuple.getT2()))
                            .status(notification.getStatus())
                            .type(notification.getType())
                            .imageUrl(
                                    notification.getType().equals(NotificationType.NEW_FRIEND) ?
                                            tuple.getT1().getProfilePicture() : tuple.getT2().getConversationPicture()
                            )
                            .createdAt(notification.getCreatedAt())
                            .build();
                })
                .switchIfEmpty(Mono.error(new ApplicationException(ErrorCode.NOTIFICATION_ERROR1)));
    }
}
