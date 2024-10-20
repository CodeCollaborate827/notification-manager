package com.chat.notification_manager;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.mongodb.repository.config.EnableReactiveMongoRepositories;

@SpringBootApplication
@EnableReactiveMongoRepositories
public class NotificationManagerApplication {

  public static void main(String[] args) {
    SpringApplication.run(NotificationManagerApplication.class, args);
  }
}
