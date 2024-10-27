package com.chat.notification_manager.config;

import com.chat.notification_manager.event.Event;
import java.util.function.Supplier;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.Message;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Sinks;

@Slf4j
@Configuration
public class ProducerBindingConfig {
  public static final Sinks.Many<Message<Event>> newNotificationUpstreamSink =
      Sinks.many().unicast().onBackpressureBuffer();

  @Bean("newNotificationDownstream")
  public Supplier<Flux<Message<Event>>> newNotificationDownstream() {
    return newNotificationUpstreamSink::asFlux;
  }
}
