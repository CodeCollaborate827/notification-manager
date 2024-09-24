package com.chat.notification_manager.utils;

import com.chat.notification_manager.docunent.Conversation;
import com.chat.notification_manager.docunent.User;
import com.chat.notification_manager.enums.NotificationType;

public class NotificationMessageGenerator {

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

    switch (type) {
      case NEW_FRIEND:
        return "New Friend Request from " + displayName;
      case MESSAGE_MENTIONED:
        return displayName + " mentioned you in " + conversationName;
      case MESSAGE_REACTED:
        return displayName + " reacted to your message in " + conversationName;
      default:
        return "Notification";
    }
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

    switch (type) {
      case NEW_FRIEND:
        return displayName
            + " wants to connect with you. Click to view their profile and accept the request.";
      case MESSAGE_MENTIONED:
        return "You were mentioned by "
            + displayName
            + " in "
            + conversationName
            + ". Tap to view the message.";
      case MESSAGE_REACTED:
        return displayName
            + " reacted to your message in "
            + conversationName
            + ". Tap to view the conversation.";
      default:
        return "You have a new notification.";
    }
  }
}
