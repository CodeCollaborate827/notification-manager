package com.chat.notification_manager.service;

import com.chat.notification_manager.document.User;
import com.chat.notification_manager.event.upstream.userAccount.UserRegistrationEventData;
import reactor.core.publisher.Mono;

public interface UserService {
  Mono<User> save(User user);

  Mono<User> processUserRegistrationEvent(UserRegistrationEventData userRegistrationEvent);
}
