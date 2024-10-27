package com.chat.notification_manager.config;

import com.chat.notification_manager.document.Notification;
import com.chat.notification_manager.document.User;
import com.chat.notification_manager.enums.NotificationType;
import com.chat.notification_manager.event.Event;
import com.chat.notification_manager.event.upstream.MessageMentionedEvent;
import com.chat.notification_manager.event.upstream.MessageReactedEvent;
import com.chat.notification_manager.event.upstream.NewFriendRequestEvent;
import com.chat.notification_manager.event.upstream.UserRegistrationEvent;
import com.chat.notification_manager.model.AddFriendNotificationProperties;
import com.chat.notification_manager.model.MessageMentionedNotificationProperties;
import com.chat.notification_manager.model.MessageReactedNotificationProperties;
import com.chat.notification_manager.repository.ConversationRepository;
import com.chat.notification_manager.repository.UserRepository;
import com.chat.notification_manager.service.NotificationService;
import com.chat.notification_manager.service.UserService;
import com.chat.notification_manager.utils.DecodeUtil;
import com.chat.notification_manager.utils.NotificationUtils;
import com.chat.notification_manager.utils.Utils;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.GenericMessage;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.function.Consumer;
import java.util.function.Function;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class ConsumerBindingConfig {
  private final NotificationService notificationService;
  private final ConversationRepository conversationRepository;
  private final UserRepository userRepository;
  private final UserService userService;
  private final ObjectMapper objectMapper;

  @Bean
  public Function<Flux<Message<Event>>, Flux<Message<Event>>> messageMentioned() {
    return createNotificationProcessor(NotificationType.MESSAGE_MENTIONED);
  }

  @Bean
  public Function<Flux<Message<Event>>, Flux<Message<Event>>> messageReacted() {
    return createNotificationProcessor(NotificationType.MESSAGE_REACTED);
  }

  @Bean
  public Function<Flux<Message<Event>>, Flux<Message<Event>>> newFriendRequest() {
    return createNotificationProcessor(NotificationType.NEW_FRIEND);
  }

  @Bean
  public Consumer<Flux<Message<Event>>> userRegistrationUpstreamConsumer() {
    return flux ->
        flux.flatMap(message -> processUserRegistration(message.getPayload()))
            .doOnNext(user -> log.info("User created: {}", user))
            .onErrorResume(NotificationUtils::handleError)
            .subscribe();
  }

  private Function<Flux<Message<Event>>, Flux<Message<Event>>> createNotificationProcessor(
      NotificationType type) {
    return flux ->
        flux.flatMap(
                msg -> {
                  log.info("Processing notification of type: {}", msg);
                  return processNotification(msg, type);
                })
            .flatMap(notificationService::saveNotification)
            .flatMap(notification -> createNotificationEvent(notification, type))
            .onErrorResume(NotificationUtils::handleError)
            .map(GenericMessage::new);
  }

  private Flux<Event> createNotificationEvent(Notification notification, NotificationType type) {
    return switch (type) {
      case NEW_FRIEND -> createFriendRequestEvent(notification);
      case MESSAGE_MENTIONED -> createMessageMentionedEvent(notification);
      case MESSAGE_REACTED -> createMessageReactedEvent(notification);
      default -> {
        log.warn("Unsupported notification type: {}", type);
        yield Flux.empty();
      }
    };
  }

  private Flux<Event> createMessageReactedEvent(Notification notification) {
    MessageReactedNotificationProperties properties =
        (MessageReactedNotificationProperties) notification.getProperties();
    return Mono.zip(
            userRepository.findById(properties.getSenderId()),
            conversationRepository.findById(properties.getConversationId()))
        .map(tuple -> Utils.mapNotificationToDTO(notification, tuple.getT1(), tuple.getT2()))
        .map(dto -> NotificationUtils.createEvent(dto, objectMapper))
        .doOnNext(event -> log.info("Message react event created: {}", event))
        .flatMapMany(Flux::just);
  }

  private Flux<Event> createFriendRequestEvent(Notification notification) {
    AddFriendNotificationProperties properties =
        (AddFriendNotificationProperties) notification.getProperties();
    return userRepository
        .findById(properties.getSenderId())
        .map(user -> Utils.mapNotificationToDTO(notification, user, null))
        .map(dto -> NotificationUtils.createEvent(dto, objectMapper))
        .doOnNext(event -> log.info("Friend request event created: {}", event))
        .flatMapMany(Flux::just);
  }

  private Flux<Event> createMessageMentionedEvent(Notification notification) {
    MessageMentionedNotificationProperties properties =
        (MessageMentionedNotificationProperties) notification.getProperties();
    return Mono.zip(
            userRepository.findById(properties.getSenderId()),
            conversationRepository.findById(properties.getConversationId()))
        .map(tuple -> Utils.mapNotificationToDTO(notification, tuple.getT1(), tuple.getT2()))
        .map(dto -> NotificationUtils.createEvent(dto, objectMapper))
        .doOnNext(event -> log.info("Message mentioned event created: {}", event))
        .flatMapMany(Flux::just);
  }

  private Mono<Notification> processNotification(Message<Event> message, NotificationType type) {
    Event event = message.getPayload();
    String payloadBase64 = event.getPayloadBase64();
    log.info("Processing notification of type: {}", type);

    return switch (type) {
      case NEW_FRIEND -> decodeNotification(payloadBase64, NewFriendRequestEvent.class);
      case MESSAGE_MENTIONED -> decodeNotification(payloadBase64, MessageMentionedEvent.class);
      case MESSAGE_REACTED -> decodeNotification(payloadBase64, MessageReactedEvent.class);
      default -> {
        log.warn("Unsupported notification type: {}", type);
        yield Mono.empty();
      }
    };
  }

  private <T> Mono<Notification> decodeNotification(String payloadBase64, Class<T> eventClass) {
    return NotificationUtils.decodeAndCreateNotification(payloadBase64, eventClass, objectMapper);
  }

  private Mono<User> processUserRegistration(Event payload) {
    DecodeUtil<UserRegistrationEvent> decoder = new DecodeUtil<>(objectMapper);
    return Mono.fromCallable(
            () -> decoder.decode(payload.getPayloadBase64(), UserRegistrationEvent.class))
        .flatMap(NotificationUtils::mapToUser)
        .flatMap(userService::save);
  }
}
