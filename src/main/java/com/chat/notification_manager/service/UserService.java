package com.chat.notification_manager.service;

import com.chat.notification_manager.document.User;
import com.chat.notification_manager.event.upstream.UserRegistrationEvent;
import reactor.core.publisher.Mono;

public interface UserService {
  Mono<User> save(User user);

  Mono<User> processUserRegistrationEvent(UserRegistrationEvent userRegistrationEvent);
}
