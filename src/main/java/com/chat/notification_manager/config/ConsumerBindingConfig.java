package com.chat.notification_manager.config;

import com.chat.notification_manager.document.Notification;
import com.chat.notification_manager.document.User;
import com.chat.notification_manager.enums.NotificationType;
import com.chat.notification_manager.event.Event;
import com.chat.notification_manager.event.upstream.MessageMentionedEvent;
import com.chat.notification_manager.event.upstream.MessageReactedEvent;
import com.chat.notification_manager.event.upstream.NewFriendRequestEvent;
import com.chat.notification_manager.event.upstream.UserRegistrationEvent;
import com.chat.notification_manager.repository.ConversationRepository;
import com.chat.notification_manager.repository.UserRepository;
import com.chat.notification_manager.service.NotificationService;
import com.chat.notification_manager.service.UserService;
import com.chat.notification_manager.utils.DecodeUtil;
import com.chat.notification_manager.utils.NotificationUtils;
import com.chat.notification_manager.utils.Utils;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.function.Consumer;
import java.util.function.Function;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.GenericMessage;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

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
    return genericEventConsumer(NotificationType.MESSAGE_MENTIONED);
  }

  @Bean
  public Function<Flux<Message<Event>>, Flux<Message<Event>>> messageReacted() {
    return genericEventConsumer(NotificationType.MESSAGE_REACTED);
  }

  @Bean
  public Function<Flux<Message<Event>>, Flux<Message<Event>>> newFriendRequest() {
    return genericEventConsumer(NotificationType.NEW_FRIEND);
  }

  @Bean
  public Consumer<Flux<Message<Event>>> userRegistrationUpstreamConsumer() {
    return flux ->
        flux.flatMap(message -> processCommonMessage(message.getPayload()))
            .doOnNext(user -> log.info("User created: {}", user))
            .onErrorResume(NotificationUtils::handleError)
            .subscribe();
  }

  private Function<Flux<Message<Event>>, Flux<Message<Event>>> genericEventConsumer(
      NotificationType notificationType) {
    return flux ->
        flux.flatMap(msg -> processNotificationMessage(msg, notificationType))
            .flatMap(notificationService::saveNotification)
            .flatMap(
                notification ->
                    Utils.mapNotificationToDTO(
                        notification, userRepository, conversationRepository))
            .map(notificationDTO -> NotificationUtils.createEvent(notificationDTO, objectMapper))
            .onErrorResume(NotificationUtils::handleError)
            .doOnNext(event -> log.info("Event created: {}", event))
            .map(GenericMessage::new);
  }

  private Mono<Notification> processNotificationMessage(
      Message<Event> message, NotificationType notificationType) {
    Event event = message.getPayload();
    String payloadBase64 = event.getPayloadBase64();

    log.info("Processing message with type: {}", notificationType);

    return switch (notificationType) {
      case NotificationType.NEW_FRIEND ->
          NotificationUtils.decodeAndCreateNotification(
              payloadBase64, NewFriendRequestEvent.class, objectMapper);
      case NotificationType.MESSAGE_MENTIONED ->
          NotificationUtils.decodeAndCreateNotification(
              payloadBase64, MessageMentionedEvent.class, objectMapper);
      case NotificationType.MESSAGE_REACTED ->
          NotificationUtils.decodeAndCreateNotification(
              payloadBase64, MessageReactedEvent.class, objectMapper);
      default -> {
        log.warn("Unknown notification type: {}", notificationType);
        yield Mono.empty();
      }
    };
  }

  private Mono<User> processCommonMessage(Event payload) {
    String payloadBase64 = payload.getPayloadBase64();
    DecodeUtil<UserRegistrationEvent> decodeUtil1 = new DecodeUtil<>(objectMapper);

    return Mono.fromCallable(() -> decodeUtil1.decode(payloadBase64, UserRegistrationEvent.class))
        .flatMap(NotificationUtils::mapToUser)
        .flatMap(userService::save);
  }
}
