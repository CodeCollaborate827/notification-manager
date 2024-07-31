package com.chat.notification_manager.exception;

import lombok.Getter;

@Getter
public enum ErrorCode {
  NOTIFICATION_ERROR1("Notification not found", 404);

  private final String errorMessage;
  private final int httpStatus;

  ErrorCode(String errorMessage, int httpStatus) {
    this.errorMessage = errorMessage;
    this.httpStatus = httpStatus;
  }

}
