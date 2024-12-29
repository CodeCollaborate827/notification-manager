package com.chat.notification_manager.event.upstream.userContact;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NewFriendRequestEventData {
  private String requestSenderId;
  private String requestRecipientId;
  private Long createdAt;
}
