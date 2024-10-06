package com.chat.notification_manager.utils;

import com.chat.notification_manager.document.Conversation;
import com.chat.notification_manager.document.User;
import com.chat.notification_manager.enums.NotificationType;

public class NotificationMessageGenerator {

  private NotificationMessageGenerator() {}

  public static String generateTitle(
      NotificationType type, User fromUser, Conversation conversation) {
    String displayName =
        (fromUser != null && fromUser.getDisplayName() != null)
            ? fromUser.getDisplayName()
            : "Someone";
    String conversationName =
        (conversation != null && conversation.getConversationName() != null)
            ? conversation.getConversationName()
            : "a conversation";

    return switch (type) {
      case NEW_FRIEND -> "New Friend Request from " + displayName;
      case MESSAGE_MENTIONED -> displayName + " mentioned you in " + conversationName;
      case MESSAGE_REACTED -> displayName + " reacted to your message in " + conversationName;
      default -> "Notification";
    };
  }

  public static String generateContent(
      NotificationType type, User fromUser, Conversation conversation) {
    String displayName =
        (fromUser != null && fromUser.getDisplayName() != null)
            ? fromUser.getDisplayName()
            : "Someone";
    String conversationName =
        (conversation != null && conversation.getConversationName() != null)
            ? conversation.getConversationName()
            : "a conversation";

    return switch (type) {
      case NEW_FRIEND ->
          displayName
              + " wants to connect with you. Click to view their profile and accept the request.";
      case MESSAGE_MENTIONED ->
          "You were mentioned by "
              + displayName
              + " in "
              + conversationName
              + ". Tap to view the message.";
      case MESSAGE_REACTED ->
          displayName
              + " reacted to your message in "
              + conversationName
              + ". Tap to view the conversation.";
      default -> "You have a new notification.";
    };
  }
}
