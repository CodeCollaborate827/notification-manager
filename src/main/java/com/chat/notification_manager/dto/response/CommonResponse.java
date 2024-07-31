package com.chat.notification_manager.dto.response;

import com.chat.notification_manager.exception.ErrorCode;
import lombok.Data;

@Data
public class CommonResponse {
  private ErrorCode errorCode;
  private String message;
  private String requestId;
  private Object data;
}
