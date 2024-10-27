package com.chat.notification_manager.event.upstream;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserRegistrationEvent {
  private String userId;
  private String username;
  private String displayName;
  private String email;
  private OffsetDateTime createdAt;
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
