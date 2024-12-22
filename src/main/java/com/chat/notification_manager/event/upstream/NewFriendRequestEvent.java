package com.chat.notification_manager.event.upstream;

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
  private String recipientId;
  private Long createdAt;
}
