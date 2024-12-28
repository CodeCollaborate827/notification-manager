package com.chat.notification_manager.service.implementation;

import com.chat.notification_manager.document.User;
import com.chat.notification_manager.event.upstream.UserRegistrationEvent;
import com.chat.notification_manager.repository.UserRepository;
import com.chat.notification_manager.service.UserService;
import com.chat.notification_manager.utils.Utils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
  private final UserRepository userRepository;

  @Override
  public Mono<User> save(User user) {
    return userRepository
        .save(user)
        .doOnSuccess(savedUser -> log.info("User saved: {}", savedUser))
        .doOnError(throwable -> log.error("Error saving user: {}", throwable.getMessage()));
  }

  @Override
  public Mono<User> processUserRegistrationEvent(UserRegistrationEvent userRegistrationEvent) {
    User user = Utils.convertToUser(userRegistrationEvent);
    return save(user);
  }
}
