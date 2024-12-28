package com.chat.notification_manager.event.upstream.userContact;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FriendRequestAcceptedEventData {
  public enum Status {
    PENDING,
    ACCEPTED,
    DENIED
  }

  private String senderId;
  private String recipientId;
  private Status status;
  private Long timestamp;
}