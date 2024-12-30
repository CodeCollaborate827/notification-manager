package com.chat.notification_manager.utils;

import com.chat.notification_manager.document.User;
import com.chat.notification_manager.enums.NotificationType;

public class NotificationMessageGenerator {

  private static final String DEFAULT_DISPLAY_NAME = "Someone";
  private static final String DEFAULT_CONVERSATION_NAME = "a conversation";

  // notification title templates
  private static final String DEFAULT_NOTIFICATION_TITLE = "Notification";
  private static final String NEW_FRIEND_REQUEST_TITLE = "New Friend Request from %s";
  private static final String FRIEND_REQUEST_ACCEPTED_TITLE = "%s accepted your friend request";
  private static final String MESSAGE_MENTIONED_TITLE = "%s mentioned you in %s";
  private static final String MESSAGE_REACTED_TITLE = "%s reacted to your message in %s";

  // notification content templates
  private static final String NEW_FRIEND_REQUEST_CONTENT =
      "%s wants to connect with you. Click to view their profile and accept the request.";
  private static final String FRIEND_REQUEST_ACCEPTED_CONTENT =
      "%s accepted your friend request. You are now connected! Tap to start a conversation or view their profile.";
  private static final String MESSAGE_MENTIONED_CONTENT =
      "You were mentioned by %s in %s. Tap to view the message.";
  private static final String MESSAGE_REACTED_CONTENT =
      "%s reacted to your message in %s. Tap to view the conversation.";
  private static final String DEFAULT_NOTIFICATION_CONTENT = "You have a new notification.";

  private NotificationMessageGenerator() {}

  public static String generateTitle(
      NotificationType type, User fromUser, String conversationName) {
    String displayName = extractDisplayName(fromUser);

    return switch (type) {
      case NOTIFICATION_NEW_FRIEND_REQUEST -> NEW_FRIEND_REQUEST_TITLE.formatted(displayName);
      case NOTIFICATION_FRIEND_REQUEST_ACCEPTED ->
          FRIEND_REQUEST_ACCEPTED_TITLE.formatted(displayName);
      case NOTIFICATION_MESSAGE_MENTIONED ->
          MESSAGE_MENTIONED_TITLE.formatted(displayName, conversationName);
      case NOTIFICATION_MESSAGE_REACTED ->
          MESSAGE_REACTED_TITLE.formatted(displayName, conversationName);
      default -> DEFAULT_NOTIFICATION_TITLE;
    };
  }

  public static String generateContent(
      NotificationType type, User fromUser, String conversationName) {
    String displayName = extractDisplayName(fromUser);

    return switch (type) {
      case NOTIFICATION_NEW_FRIEND_REQUEST -> NEW_FRIEND_REQUEST_CONTENT.formatted(displayName);
      case NOTIFICATION_FRIEND_REQUEST_ACCEPTED ->
          FRIEND_REQUEST_ACCEPTED_CONTENT.formatted(displayName);
      case NOTIFICATION_MESSAGE_MENTIONED ->
          MESSAGE_MENTIONED_CONTENT.formatted(displayName, conversationName);
      case NOTIFICATION_MESSAGE_REACTED ->
          MESSAGE_REACTED_CONTENT.formatted(displayName, conversationName);
      default -> DEFAULT_NOTIFICATION_CONTENT;
    };
  }

  private static String extractDisplayName(User user) {
    if (user != null && user.getDisplayName() != null) {
      return user.getDisplayName();
    }
    return DEFAULT_DISPLAY_NAME;
  }
}
