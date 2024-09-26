package com.chat.notification_manager.service.implementation;

import com.chat.notification_manager.config.ProducerBindingConfig;
import com.chat.notification_manager.docunent.Notification;
import com.chat.notification_manager.dto.response.NotificationDTO;
import com.chat.notification_manager.event.Event;
import com.chat.notification_manager.repository.ConversationRepository;
import com.chat.notification_manager.repository.UserRepository;
import com.chat.notification_manager.service.KafkaProducerService;
import com.chat.notification_manager.utils.DecodeUtil;
import com.chat.notification_manager.utils.Utils;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Sinks;

@Slf4j
@Service
@RequiredArgsConstructor
public class KafkaProducerServiceImpl implements KafkaProducerService {
  private final UserRepository userRepository;
  private final ConversationRepository conversationRepository;
  private final DecodeUtil<NotificationDTO> decodeUtil;

  @Override
  public void sendNewRegistryEvent(Notification notification) {
    Utils.mapNotificationToDTO(notification, userRepository, conversationRepository)
        .flatMap(
            notificationDTO -> {
              log.info("NotificationDTO: {}", notificationDTO);
              return emitEvent(notification, notificationDTO);
            })
        .subscribe(
            result -> log.info("Message emitted successfully"),
            e -> log.error("Error sending message: {}", e.getMessage(), e));
  }

  private Mono<Void> emitEvent(Notification notification, NotificationDTO notificationDTO) {
    return Mono.fromCallable(() -> {
      Event event = createEvent(notification, notificationDTO);
      Message<Event> message = MessageBuilder.withPayload(event).build();
      Sinks.EmitResult result = ProducerBindingConfig.newNotificationUpstreamSink.tryEmitNext(message);

      if (result.isFailure()) {
        log.error("Failed to emit message: {}", result);
        throw new RuntimeException("Failed to emit message");
      }

      log.info("Message emitted successfully: {}", message);
      return Mono.empty();
    }).then();
  }

  private Event createEvent(Notification notification, NotificationDTO notificationDTO) throws JsonProcessingException {
    return Event.builder()
        .userId(notification.getUserId())
        .type(notification.getType())
        .payloadBase64(decodeUtil.encode(notificationDTO))
        .build();
  }
}
