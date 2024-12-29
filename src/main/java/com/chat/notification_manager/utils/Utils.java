package com.chat.notification_manager.utils;

import com.chat.notification_manager.document.User;
import com.chat.notification_manager.event.Event;
import com.chat.notification_manager.event.downstream.NotificationEvent;
import com.chat.notification_manager.event.upstream.userAccount.UserRegistrationEventData;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.OffsetDateTime;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.support.GenericMessage;

@Slf4j
public class Utils {
  private static final ObjectMapper objectMapper = new ObjectMapper();

  public static Event createEvent(NotificationEvent object) {
    try {
      String decodedObject = DecodeUtil.encode(object);
      Event event = Event.builder().payloadBase64(decodedObject).build();
      return event;
    } catch (JsonProcessingException e) {
      throw new RuntimeException(e);
    }
  }

  public static GenericMessage<Event> createGenericMessage(NotificationEvent notificationEvent) {
    Event event = createEvent(notificationEvent);
    return new GenericMessage<>(event);
  }

  public static User convertToUser(UserRegistrationEventData userRegistrationEvent) {
    return User.builder()
        .id(userRegistrationEvent.getUserId())
        .displayName(userRegistrationEvent.getDisplayName())
        .avatar(userRegistrationEvent.getAvatar())
        .build();
  }

  public static OffsetDateTime convertEpochToOffsetDateTime(long epoch) {
    return OffsetDateTime.ofInstant(
        java.time.Instant.ofEpochMilli(epoch), java.time.ZoneOffset.UTC);
  }

  public static <T> T convertObject(Object object, Class<T> clazz) {
    return objectMapper.convertValue(object, clazz);
  }
}
