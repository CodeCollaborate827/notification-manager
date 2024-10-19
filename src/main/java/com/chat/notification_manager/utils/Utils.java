package com.chat.notification_manager.utils;

import com.chat.notification_manager.constant.NotificationProperties;
import com.chat.notification_manager.document.Conversation;
import com.chat.notification_manager.document.Notification;
import com.chat.notification_manager.document.User;
import com.chat.notification_manager.dto.response.NotificationDTO;
import com.chat.notification_manager.enums.NotificationType;
import com.chat.notification_manager.event.upstream.UserRegistrationEvent;
import com.chat.notification_manager.exception.ApplicationException;
import com.chat.notification_manager.exception.ErrorCode;
import com.chat.notification_manager.repository.ConversationRepository;
import com.chat.notification_manager.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

import java.util.Map;

@Slf4j
public class Utils {
  private Utils() {}

  public static Mono<NotificationDTO> mapNotificationToDTO(
      Notification notification,
      UserRepository userRepository,
      ConversationRepository conversationRepository) {
    Map<String, Object> props = notification.getProperties();
    Mono<User> userMono = userRepository.findById((String) props.get(NotificationProperties.FROM_USER));

    // If conversationId is null, this will return Mono.empty()
    Mono<Conversation> conversationMono =
        Mono.justOrEmpty((String) props.get(NotificationProperties.CONVERSATION_ID))
            .flatMap(conversationRepository::findById);

    return Mono.zip(userMono, conversationMono.defaultIfEmpty(new Conversation()))
        .map(
            tuple -> {
              log.info("User and conversation found: {}", tuple);

              User user = tuple.getT1();
              Conversation conversation =
                  tuple.getT2(); // Can be an empty Conversation if not present

              return NotificationDTO.builder()
                  .id(notification.getId())
                  .title(
                      NotificationMessageGenerator.generateTitle(
                          notification.getType(), user, conversation))
                  .content(
                      NotificationMessageGenerator.generateContent(
                          notification.getType(), user, conversation))
                  .status(notification.getStatus())
                  .type(notification.getType())
                  .imageUrl(
                      notification.getType().equals(NotificationType.NEW_FRIEND)
                          ? user.getProfilePicture()
                          : conversation.getConversationPicture() // Check if conversation exists
                      )
                  .properties(notification.getProperties())
                  .createdAt(notification.getCreatedAt())
                  .build();
            })
        .switchIfEmpty(Mono.error(new ApplicationException(ErrorCode.NOTIFICATION_ERROR1)));
  }

  public static User convertToUser(UserRegistrationEvent userRegistrationEvent) {
    return User.builder()
        .id(userRegistrationEvent.getUserId())
        .displayName(userRegistrationEvent.getDisplayName())
        .profilePicture(userRegistrationEvent.getAvatar())
        .build();
  }
}
