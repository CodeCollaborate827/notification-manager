package com.chat.notification_manager.service.implementation;

import com.chat.notification_manager.document.Conversation;
import com.chat.notification_manager.document.Notification;
import com.chat.notification_manager.document.notificationProperties.AddFriendNotificationProperties;
import com.chat.notification_manager.document.notificationProperties.MessageMentionedNotificationProperties;
import com.chat.notification_manager.document.notificationProperties.MessageReactedNotificationProperties;
import com.chat.notification_manager.dto.response.NotificationDTO;
import com.chat.notification_manager.enums.Status;
import com.chat.notification_manager.event.downstream.NotificationEvent;
import com.chat.notification_manager.event.upstream.conversation.ConversationEvent;
import com.chat.notification_manager.event.upstream.conversation.NewConversationEventData;
import com.chat.notification_manager.event.upstream.message.MessageMentionedNotificationTriggerEvent;
import com.chat.notification_manager.event.upstream.message.MessageReactedNotificationTriggerEvent;
import com.chat.notification_manager.event.upstream.userContact.FriendRequestAcceptedEventData;
import com.chat.notification_manager.event.upstream.userContact.NewFriendRequestEventData;
import com.chat.notification_manager.exception.ApplicationException;
import com.chat.notification_manager.exception.ErrorCode;
import com.chat.notification_manager.repository.ConversationRepository;
import com.chat.notification_manager.repository.NotificationRepository;
import com.chat.notification_manager.repository.UserRepository;
import com.chat.notification_manager.service.NotificationService;
import com.chat.notification_manager.utils.ConversationUtils;
import com.chat.notification_manager.utils.NotificationUtils;
import com.chat.notification_manager.utils.Utils;
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
                        userRepository.findById(userId),
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
                        userRepository.findById(userId),
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
      MessageMentionedNotificationTriggerEvent messageMentionedEvent) {
    Notification notification = NotificationUtils.createNotification(messageMentionedEvent);
    return saveNotification(notification).flatMap(this::createMessageMentionedEvent);
  }

  @Override
  public Mono<NotificationEvent> processMessageReactedEvent(
      MessageReactedNotificationTriggerEvent messageReactedEvent) {
    Notification notification = NotificationUtils.createNotification(messageReactedEvent);
    return saveNotification(notification).flatMap(this::createMessageReactedEvent);
  }

  @Override
  public Mono<NotificationEvent> processFriendRequestNotification(
      NewFriendRequestEventData newFriendRequestEvent) {
    Notification notification = NotificationUtils.createNotification(newFriendRequestEvent);
    return saveNotification(notification).flatMap(this::createFriendRequestEvent);
  }

  @Override
  public Mono<NotificationEvent> processFriendRequestAcceptedNotification(
      FriendRequestAcceptedEventData friendRequestAcceptedEvent) {
    Notification notification = NotificationUtils.createNotification(friendRequestAcceptedEvent);
    return saveNotification(notification).flatMap(this::createFriendRequestAcceptedEvent);
  }

  @Override
  public Mono<Conversation> processConversationEvent(ConversationEvent conversationEvent) {
    // TODO: find a design pattern to handle this scenario when there are multiple types of events,
    // each has different processing logic
    if (conversationEvent
        .getMessageType()
        .equals(ConversationEvent.ConversationEventType.CONVERSATION_NEW)) {
      log.info("Processing new conversation event: {}", conversationEvent.getData());
      NewConversationEventData newConversationEventData =
          Utils.convertObject(conversationEvent.getData(), NewConversationEventData.class);
      Conversation conversation = ConversationUtils.createConversation(newConversationEventData);

      // check if conversation already exists
      return conversationRepository
          .findById(conversation.getId())
          .map(
              existedConversation -> {
                log.error("Conversation already exists: {}", existedConversation);
                return existedConversation; // return existed conversation
              })
          .switchIfEmpty(conversationRepository.save(conversation))
          .doOnSuccess(
              savedConversation ->
                  log.info("Conversation saved with id: {}", savedConversation.getId()));
    }

    return Mono.error(
        new ApplicationException(
            ErrorCode.NOTIFICATION_ERROR2)); // TODO: only support new conversation event for now
  }

  private Mono<NotificationEvent> createMessageReactedEvent(Notification notification) {
    MessageReactedNotificationProperties properties =
        (MessageReactedNotificationProperties) notification.getProperties();
    return Mono.zip(
            userRepository.findById(properties.getReactionSenderId()),
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
        .findById(properties.getRequestSenderId())
        .doOnNext(user -> log.info("User found: {}", user))
        .map(user -> NotificationUtils.createNotificationDTO(notification, user, null))
        .map(NotificationUtils::createNotificationEvent);
  }

  private Mono<NotificationEvent> createFriendRequestAcceptedEvent(Notification notification) {
    AddFriendNotificationProperties properties =
        (AddFriendNotificationProperties) notification.getProperties();
    return userRepository
        .findById(properties.getRequestRecipientId())
        .doOnNext(user -> log.info("User found: {}", user))
        .map(user -> NotificationUtils.createNotificationDTO(notification, user, null))
        .map(NotificationUtils::createNotificationEvent);
  }

  private Mono<NotificationEvent> createMessageMentionedEvent(Notification notification) {
    MessageMentionedNotificationProperties properties =
        (MessageMentionedNotificationProperties) notification.getProperties();
    return Mono.zip(
            userRepository.findById(properties.getMessageSenderId()),
            conversationRepository.findById(properties.getConversationId()))
        .map(
            tuple ->
                NotificationUtils.createNotificationDTO(notification, tuple.getT1(), tuple.getT2()))
        .map(NotificationUtils::createNotificationEvent);
  }
}
