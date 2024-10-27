package com.chat.notification_manager.controller;

import com.chat.notification_manager.dto.response.NotificationDTO;
import com.chat.notification_manager.service.NotificationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/notification")
public class NotificationController {
  private final NotificationService notificationService;

  @Operation(summary = "Get all notifications for a user")
  @ApiResponse(
      responseCode = "200",
      description = "Notifications retrieved successfully",
      content = {
        @Content(
            mediaType = "application/json",
            schema = @Schema(implementation = NotificationDTO.class))
      })
  @ApiResponse(responseCode = "404", description = "Notifications not found", content = @Content)
  @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content)
  @GetMapping("/allNotifications")
  // TODO: add from and to query parameters for paging(limit 20 notifications)
  public Flux<ResponseEntity<NotificationDTO>> getAllNotifications(@RequestHeader String userId) {
    return notificationService.getAllNotifications(userId);
  }

  @Operation(summary = "Get all read notifications for a user")
  @ApiResponse(
      responseCode = "200",
      description = "Read notifications retrieved successfully",
      content = {
        @Content(
            mediaType = "application/json",
            schema = @Schema(implementation = NotificationDTO.class))
      })
  @ApiResponse(
      responseCode = "404",
      description = "Read notifications not found",
      content = @Content)
  @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content)
  @GetMapping("/getAllReadNotifications")
  public Flux<ResponseEntity<NotificationDTO>> getAllReadNotifications(
      @RequestHeader String userId) {
    return notificationService.getAllReadNotifications(userId);
  }
}
