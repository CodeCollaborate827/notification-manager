package com.chat.notification_manager.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;

import java.util.Base64;

@RequiredArgsConstructor
public class DecodeUtil<T> {
    private final ObjectMapper objectMapper;

    public T decode(String encodedBase64, Class<T> clazz) throws JsonProcessingException {
        byte[] decoded = Base64.getDecoder().decode(encodedBase64);
        String json = new String(decoded);
        return objectMapper.readValue(json, clazz);
    }

    public String encode(T object) {
        return null;
    }
}
