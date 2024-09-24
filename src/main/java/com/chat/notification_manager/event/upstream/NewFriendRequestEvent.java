package com.chat.notification_manager.event.upstream;

import java.time.OffsetDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NewFriendRequestEvent {
  private String senderId;
  private OffsetDateTime createdAt;
}
