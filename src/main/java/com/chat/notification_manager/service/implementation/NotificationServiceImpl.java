package com.chat.notification_manager.service.implementation;

import com.chat.notification_manager.document.Notification;
import com.chat.notification_manager.dto.response.NotificationDTO;
import com.chat.notification_manager.enums.Status;
import com.chat.notification_manager.exception.ApplicationException;
import com.chat.notification_manager.exception.ErrorCode;
import com.chat.notification_manager.repository.ConversationRepository;
import com.chat.notification_manager.repository.NotificationRepository;
import com.chat.notification_manager.repository.UserRepository;
import com.chat.notification_manager.service.NotificationService;
import com.chat.notification_manager.utils.NotificationUtils;
import com.chat.notification_manager.utils.Utils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Objects;

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
    return notificationRepository
        .findAllByUserId(userId)
        .flatMap(
            notification ->
                Mono.zip(
                        userRepository.findById(
                            Objects.requireNonNull(
                                NotificationUtils.getSenderIdFromNotificationProps(notification))),
                        conversationRepository.findById(
                            Objects.requireNonNull(
                                NotificationUtils.getConversationIdFromNotificationProps(
                                    notification))))
                    .mapNotNull(
                        tuple ->
                            Utils.mapNotificationToDTO(notification, tuple.getT1(), tuple.getT2()))
                    .map(ResponseEntity::ok)
                    .switchIfEmpty(
                        Mono.error(new ApplicationException(ErrorCode.NOTIFICATION_ERROR1))));
  }

  @Override
  public Flux<ResponseEntity<NotificationDTO>> getAllReadNotifications(String userId) {
    return notificationRepository
        .findAllByUserIdAndStatus(userId, Status.READ)
        .flatMap(
            notification ->
                Mono.zip(
                        userRepository.findById(
                            Objects.requireNonNull(
                                NotificationUtils.getSenderIdFromNotificationProps(notification))),
                        conversationRepository.findById(
                            Objects.requireNonNull(
                                NotificationUtils.getConversationIdFromNotificationProps(
                                    notification))))
                    .mapNotNull(
                        tuple ->
                            Utils.mapNotificationToDTO(notification, tuple.getT1(), tuple.getT2()))
                    .map(ResponseEntity::ok)
                    .switchIfEmpty(
                        Mono.error(new ApplicationException(ErrorCode.NOTIFICATION_ERROR1))));
  }

  @Override
  public Mono<Notification> saveNotification(Notification notification) {
    return notificationRepository
        .save(notification)
        .doOnSuccess(savedNotification -> log.info("Notification saved: {}", savedNotification))
        .doOnError(e -> log.error("Error saving notification: {}", e.getMessage(), e));
  }
}
