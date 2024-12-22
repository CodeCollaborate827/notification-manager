package com.chat.notification_manager.utils;

import com.chat.notification_manager.event.Event;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Base64;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class DecodeUtil {
  private static final ObjectMapper objectMapper = new ObjectMapper();

  public static <T> T decode(Event event, Class<T> clazz) {
    byte[] decoded = Base64.getDecoder().decode(event.getPayloadBase64());
    String json = new String(decoded);
    try {
      return objectMapper.readValue(json, clazz);
    } catch (JsonProcessingException e) {
      log.error("Error decoding event {}", e.getMessage(), e);
      return null;
    }
  }

  public static String encode(Object object) throws JsonProcessingException {
    String json = objectMapper.writeValueAsString(object);
    return Base64.getEncoder().encodeToString(json.getBytes());
  }
}
