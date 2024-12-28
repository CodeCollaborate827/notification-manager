package com.chat.notification_manager.service.implementation;

import com.chat.notification_manager.document.Notification;
import com.chat.notification_manager.dto.response.NotificationDTO;
import com.chat.notification_manager.enums.Status;
import com.chat.notification_manager.event.downstream.NotificationEvent;
import com.chat.notification_manager.event.upstream.FriendRequestAcceptedEvent;
import com.chat.notification_manager.event.upstream.MessageMentionedEvent;
import com.chat.notification_manager.event.upstream.MessageReactedEvent;
import com.chat.notification_manager.event.upstream.NewFriendRequestEvent;
import com.chat.notification_manager.exception.ApplicationException;
import com.chat.notification_manager.exception.ErrorCode;
import com.chat.notification_manager.model.AddFriendNotificationProperties;
import com.chat.notification_manager.model.MessageMentionedNotificationProperties;
import com.chat.notification_manager.model.MessageReactedNotificationProperties;
import com.chat.notification_manager.repository.ConversationRepository;
import com.chat.notification_manager.repository.NotificationRepository;
import com.chat.notification_manager.repository.UserRepository;
import com.chat.notification_manager.service.NotificationService;
import com.chat.notification_manager.utils.NotificationUtils;
import java.util.Objects;
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
                            NotificationUtils.createNotificationDTO(
                                notification, tuple.getT1(), tuple.getT2()))
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
                            NotificationUtils.createNotificationDTO(
                                notification, tuple.getT1(), tuple.getT2()))
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

  @Override
  public Mono<NotificationEvent> processMessageMentionedEvent(
      MessageMentionedEvent messageMentionedEvent) {
    Notification notification = NotificationUtils.createNotification(messageMentionedEvent);
    return saveNotification(notification).flatMap(this::createMessageMentionedEvent);
  }

  @Override
  public Mono<NotificationEvent> processMessageReactedEvent(
      MessageReactedEvent messageReactedEvent) {
    Notification notification = NotificationUtils.createNotification(messageReactedEvent);
    return saveNotification(notification).flatMap(this::createMessageReactedEvent);
  }

  @Override
  public Mono<NotificationEvent> processFriendRequestNotification(
      NewFriendRequestEvent newFriendRequestEvent) {
    Notification notification = NotificationUtils.createNotification(newFriendRequestEvent);
    return saveNotification(notification).flatMap(this::createFriendRequestEvent);
  }

  @Override
  public Mono<NotificationEvent> processFriendRequestAcceptedNotification(
      FriendRequestAcceptedEvent friendRequestAcceptedEvent) {
    Notification notification = NotificationUtils.createNotification(friendRequestAcceptedEvent);
    return saveNotification(notification).flatMap(this::createFriendRequestEvent);
  }

  private Mono<NotificationEvent> createMessageReactedEvent(Notification notification) {
    MessageReactedNotificationProperties properties =
        (MessageReactedNotificationProperties) notification.getProperties();
    return Mono.zip(
            userRepository.findById(properties.getSenderId()),
            conversationRepository.findById(properties.getConversationId()))
        .map(
            tuple ->
                NotificationUtils.createNotificationDTO(notification, tuple.getT1(), tuple.getT2()))
        .map(NotificationUtils::createNotificationEvent);
  }

  private Mono<NotificationEvent> createFriendRequestEvent(Notification notification) {
    AddFriendNotificationProperties properties =
        (AddFriendNotificationProperties) notification.getProperties();
    return userRepository
        .findById(properties.getSenderId())
        .map(user -> NotificationUtils.createNotificationDTO(notification, user, null))
        .map(NotificationUtils::createNotificationEvent);
  }

  private Mono<NotificationEvent> createMessageMentionedEvent(Notification notification) {
    MessageMentionedNotificationProperties properties =
        (MessageMentionedNotificationProperties) notification.getProperties();
    return Mono.zip(
            userRepository.findById(properties.getSenderId()),
            conversationRepository.findById(properties.getConversationId()))
        .map(
            tuple ->
                NotificationUtils.createNotificationDTO(notification, tuple.getT1(), tuple.getT2()))
        .map(NotificationUtils::createNotificationEvent);
  }
}
