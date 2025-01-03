package com.chat.notification_manager.event.upstream.userAccount;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserRegistrationEventData {
  private String userId;
  private String username;
  private String displayName;
  private String email;
  private Long createdAt;
  private String city;
  private String dateOfBirth;
  private Gender gender;
  private String avatar;

  public enum Gender {
    MALE,
    FEMALE,
    OTHER
  }
}
