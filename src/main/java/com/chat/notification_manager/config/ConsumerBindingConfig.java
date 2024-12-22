package com.chat.notification_manager.config;

import com.chat.notification_manager.event.Event;
import com.chat.notification_manager.event.upstream.*;
import com.chat.notification_manager.service.NotificationService;
import com.chat.notification_manager.service.UserService;
import com.chat.notification_manager.utils.DecodeUtil;
import com.chat.notification_manager.utils.Utils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.Message;
import reactor.core.publisher.Flux;

import java.util.function.Consumer;
import java.util.function.Function;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class ConsumerBindingConfig {
    private final NotificationService notificationService;
    private final UserService userService;

    @Bean
    public Function<Flux<Message<Event>>, Flux<Message<Event>>> handleMessageMentionedEvent() {
        return flux ->
                flux.mapNotNull(msg -> DecodeUtil.decode(msg.getPayload(), MessageMentionedEvent.class))
                        .flatMap(notificationService::processMessageMentionedEvent) // process message mentioned event and save notification to db
                        .map(Utils::createGenericMessage); // create generic message and publish to kafka
    }


    @Bean
    public Function<Flux<Message<Event>>, Flux<Message<Event>>> handleMessageReactedEvent() {
        return flux ->
                flux.mapNotNull(msg -> DecodeUtil.decode(msg.getPayload(), MessageReactedEvent.class))
                        .flatMap(notificationService::processMessageReactedEvent) // process message reacted event and save notification to db
                        .map(Utils::createGenericMessage); // create generic message and publish to kafka
    }

    @Bean
    public Function<Flux<Message<Event>>, Flux<Message<Event>>> handleNewFriendRequestEvent() {
        return flux ->
                flux.mapNotNull(msg -> DecodeUtil.decode(msg.getPayload(), NewFriendRequestEvent.class))
                        .flatMap(notificationService::processFriendRequestNotification) // process friend request event and save notification to db
                        .map(Utils::createGenericMessage); // create generic message and publish to kafka
    }

    @Bean
    public Function<Flux<Message<Event>>, Flux<Message<Event>>> handleFriendRequestAcceptedEvent() {
        return flux ->
                flux.mapNotNull(msg -> DecodeUtil.decode(msg.getPayload(), FriendRequestAcceptedEvent.class))
                        .flatMap(notificationService::processFriendRequestAcceptedNotification) // process friend request accepted event and save notification to db
                        .map(Utils::createGenericMessage); // create generic message and publish to kafka
    }

    @Bean
    public Consumer<Flux<Message<Event>>> userRegistrationUpstreamConsumer() {
        return flux ->
                flux.mapNotNull(msg -> DecodeUtil.decode(msg.getPayload(), UserRegistrationEvent.class))
                    .flatMap(userService::processUserRegistrationEvent) // process user registration event and save user to db
                    .doOnNext(user -> log.info("User created: {}", user));
    }


}
