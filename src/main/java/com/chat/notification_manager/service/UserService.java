package com.chat.notification_manager.service;

import com.chat.notification_manager.document.User;
import reactor.core.publisher.Mono;

public interface UserService {
  Mono<User> save(User user);
}
