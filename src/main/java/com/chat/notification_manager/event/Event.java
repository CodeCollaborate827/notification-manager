package com.chat.notification_manager.event;

import java.time.Instant;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Event {
  @Builder.Default private String specVersion = "1.0";
  @Builder.Default private String id = UUID.randomUUID().toString();
  @Builder.Default private Long timestamp = Instant.now().getEpochSecond();
  @Builder.Default private String dataContentType = "application/json";
  private String payloadBase64;
}
