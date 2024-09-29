package com.chat.notification_manager.config;

import com.chat.notification_manager.docunent.Notification;
import com.chat.notification_manager.enums.NotificationType;
import com.chat.notification_manager.enums.Status;
import com.chat.notification_manager.event.Event;
import com.chat.notification_manager.event.upstream.MessageMentionedEvent;
import com.chat.notification_manager.event.upstream.MessageReactedEvent;
import com.chat.notification_manager.event.upstream.NewFriendRequestEvent;
import com.chat.notification_manager.service.KafkaProducerService;
import com.chat.notification_manager.service.NotificationService;
import com.chat.notification_manager.utils.DecodeUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.GenericMessage;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.OffsetDateTime;
import java.util.function.Consumer;
import java.util.function.Function;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class ConsumerBindingConfig {
    private final ObjectMapper objectMapper;
    private final NotificationService notificationService;
    private final KafkaProducerService kafkaProducerService;

    @Bean
    public Consumer<Flux<Message<Event>>> messageMentionedDownstreamConsumer() {
        return genericEventConsumer();
    }

    @Bean
    public Consumer<Flux<Message<Event>>> messageReactedDownstreamConsumer() {
        return genericEventConsumer();
    }

    @Bean
    public Consumer<Flux<Message<Event>>> newFriendRequestDownstreamConsumer() {
        return genericEventConsumer();
    }

    private Consumer<Flux<Message<Event>>> genericEventConsumer() {
        return flux ->
                flux.flatMap(this::processMessage)
                        .doOnNext(notificationService::saveNotification)
//                        .doOnNext(kafkaProducerService::sendNewRegistryEvent)
                        .onErrorResume(this::handleError)
                        .subscribe();
    }

    @Bean
    public Function<Flux<Message<String>>, Flux<Message<String>>> func1() {
        return flux -> flux.map(msg -> {
            String payload = msg.getPayload();

            return new GenericMessage<>(payload.toUpperCase());
        });
    }

    private Mono<Notification> handleError(Throwable throwable) {
        log.error("Error when consuming message: {}", throwable.getMessage(), throwable);
        return Mono.empty();
    }

    private Mono<Notification> processMessage(Message<Event> message) {
        Event event = message.getPayload();
        String payloadBase64 = event.getPayloadBase64();

        log.info("Processing message with type: {}", event.getType());

        return switch (event.getType()) {
            case NotificationType.NEW_FRIEND ->
                    decodeAndCreateNotification(payloadBase64, NewFriendRequestEvent.class, event);
            case NotificationType.MESSAGE_MENTIONED ->
                    decodeAndCreateNotification(payloadBase64, MessageMentionedEvent.class, event);
            case NotificationType.MESSAGE_REACTED ->
                    decodeAndCreateNotification(payloadBase64, MessageReactedEvent.class, event);
            default -> {
                log.warn("Unknown notification type: {}", event.getType());
                yield Mono.empty();
            }
        };
    }

    private <T> Mono<Notification> decodeAndCreateNotification(
            String payloadBase64, Class<T> clazz, Event event) {
        DecodeUtil<T> decodeUtil = new DecodeUtil<>(objectMapper);

        return Mono.fromCallable(
                        () -> {
                            T decodedEvent = decodeUtil.decode(payloadBase64, clazz);
                            OffsetDateTime eventCreatedAt = getCreatedAtFromEvent(decodedEvent);
                            return createNotification(decodedEvent, event, eventCreatedAt);
                        })
                .onErrorResume(
                        JsonProcessingException.class,
                        e -> {
                            log.error("Error decoding event of type {}: {}", event.getType(), e.getMessage(), e);
                            return Mono.empty();
                        });
    }

    private <T> Notification createNotification(
            T eventDetails, Event event, OffsetDateTime createdAt) {
        Notification.NotificationData.NotificationDataBuilder dataBuilder =
                Notification.NotificationData.builder();

        if (eventDetails instanceof NewFriendRequestEvent newFriendEvent) {
            dataBuilder.fromUser(newFriendEvent.getSenderId());
        } else if (eventDetails instanceof MessageMentionedEvent mentionedEvent) {
            dataBuilder
                    .fromUser(mentionedEvent.getSenderId())
                    .conversationId(mentionedEvent.getConversationId())
                    .messageId(mentionedEvent.getMessageId());
        } else if (eventDetails instanceof MessageReactedEvent reactedEvent) {
            dataBuilder
                    .fromUser(reactedEvent.getSenderId())
                    .conversationId(reactedEvent.getConversationId())
                    .messageId(reactedEvent.getMessageId())
                    .reactionType(reactedEvent.getReaction());
        }

        return Notification.builder()
                .userId(event.getUserId())
                .type(event.getType())
                .status(Status.UNREAD)
                .createdAt(createdAt)
                .data(dataBuilder.build())
                .build();
    }

    private <T> OffsetDateTime getCreatedAtFromEvent(T eventDetails) {
        return switch (eventDetails) {
            case NewFriendRequestEvent newFriendRequestEvent -> newFriendRequestEvent.getCreatedAt();
            case MessageMentionedEvent messageMentionedEvent -> messageMentionedEvent.getCreatedAt();
            case MessageReactedEvent messageReactedEvent -> messageReactedEvent.getCreatedAt();
            case null, default -> OffsetDateTime.now();
        };
    }
}
