package com.chat.notification_manager.config;

import com.chat.notification_manager.docunent.Notification;
import com.chat.notification_manager.dto.response.NotificationDTO;
import com.chat.notification_manager.event.Event;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.reactivestreams.Publisher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.Message;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.function.Consumer;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class ConsumerBindingConfig {

    @Bean
    public Consumer<Flux<Message<Event>>> messageMentionedDownstreamConsumer() {
        return null;
    }

    private Mono<Notification> processMessage(Event payload) {
    }
}
