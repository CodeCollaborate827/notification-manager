package com.chat.notification_manager.exception;

import lombok.Getter;

@Getter
public enum ErrorCode {
  NOTIFICATION_ERROR1("Notification not found", 404),
  NOTIFICATION_ERROR2("Can't handle notification", 500),
  NOTIFICATION_ERROR3("Cannot get name of direct conversation", 500),
  NOTIFICATION_ERROR4("Cannot get image of direct conversation", 500),
  NOTIFICATION_ERROR5("The conversation is already exists", 500),
  EVENT_ERROR1("Event not found", 404);

  private final String errorMessage;
  private final int httpStatus;

  ErrorCode(String errorMessage, int httpStatus) {
    this.errorMessage = errorMessage;
    this.httpStatus = httpStatus;
  }
}
