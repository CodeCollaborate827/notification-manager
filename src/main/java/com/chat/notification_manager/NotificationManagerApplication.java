package com.chat.notification_manager;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.mongodb.repository.config.EnableReactiveMongoRepositories;

@SpringBootApplication
@EnableReactiveMongoRepositories
public class NotificationManagerApplication {
  // TODO: simplify the flow, handling message events, user contacts, and conversation events
  // TODO: implement logging with requestId/correlationId to flow tracing
  // TODO: implement logging message in different level (DEBUG, INFO, WARN, ERROR)
  // TODO: implement unit tests
  // TODO: implement metrics
  public static void main(String[] args) {
    SpringApplication.run(NotificationManagerApplication.class, args);
  }
}
