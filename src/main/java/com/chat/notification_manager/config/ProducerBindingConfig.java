package com.chat.notification_manager.config;

import com.chat.notification_manager.event.Event;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.Message;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Sinks;

import java.util.function.Supplier;

@Slf4j
@Configuration
public class ProducerBindingConfig {
    public static final Sinks.Many<Message<Event>> userRegistrationDownstreamSink =
            Sinks.many().unicast().onBackpressureBuffer();

    @Bean("userRegistrationDownstream")
    public Supplier<Flux<Message<Event>>> userRegistrationDownstream() {
        return userRegistrationDownstreamSink::asFlux;
    }
}
