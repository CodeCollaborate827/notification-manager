package com.chat.notification_manager.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Base64;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DecodeUtil<T> {
  private final ObjectMapper objectMapper;

  public T decode(String encodedBase64, Class<T> clazz) throws JsonProcessingException {
    byte[] decoded = Base64.getDecoder().decode(encodedBase64);
    String json = new String(decoded);
    return objectMapper.readValue(json, clazz);
  }

  public String encode(T object) throws JsonProcessingException {
    String json = objectMapper.writeValueAsString(object);
    return Base64.getEncoder().encodeToString(json.getBytes());
  }
}
