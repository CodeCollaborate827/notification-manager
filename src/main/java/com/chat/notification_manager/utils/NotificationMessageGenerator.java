package com.chat.notification_manager.utils;

import com.chat.notification_manager.document.Conversation;
import com.chat.notification_manager.document.User;
import com.chat.notification_manager.enums.NotificationType;

public class NotificationMessageGenerator {

    private static final String DEFAULT_DISPLAY_NAME = "Someone";
    private static final String DEFAULT_CONVERSATION_NAME = "a conversation";

    // notification title templates
    private static final String DEFAULT_NOTIFICATION_TITLE = "Notification";
    private static final String NEW_FRIEND_REQUEST_TITLE = "New Friend Request from %s";
    private static final String MESSAGE_MENTIONED_TITLE = "%s mentioned you in %s";
    private static final String MESSAGE_REACTED_TITLE = "%s reacted to your message in %s";

    // notification content templates
    private static final String NEW_FRIEND_REQUEST_CONTENT = "%s wants to connect with you. Click to view their profile and accept the request.";
    private static final String MESSAGE_MENTIONED_CONTENT = "You were mentioned by %s in %s. Tap to view the message.";
    private static final String MESSAGE_REACTED_CONTENT = "%s reacted to your message in %s. Tap to view the conversation.";
    private static final String DEFAULT_NOTIFICATION_CONTENT = "You have a new notification.";

    private NotificationMessageGenerator() {
    }

    public static String generateTitle(
            NotificationType type, User fromUser, Conversation conversation) {
        String displayName = extractDisplayName(fromUser);
        String conversationName = extractConversationName(conversation);

        return switch (type) {
            case NOTIFICATION_NEW_FRIEND_REQUEST -> NEW_FRIEND_REQUEST_TITLE.formatted(displayName);
            case NOTIFICATION_MESSAGE_MENTIONED -> MESSAGE_MENTIONED_TITLE.formatted(displayName, conversationName);
            case NOTIFICATION_MESSAGE_REACTED -> MESSAGE_REACTED_TITLE.formatted(displayName, conversationName);
            default -> DEFAULT_NOTIFICATION_TITLE;
        };
    }

    public static String generateContent(
            NotificationType type, User fromUser, Conversation conversation) {
        String displayName = extractDisplayName(fromUser);
        String conversationName = extractConversationName(conversation);

        return switch (type) {
            case NOTIFICATION_NEW_FRIEND_REQUEST -> NEW_FRIEND_REQUEST_CONTENT.formatted(displayName);
            case NOTIFICATION_MESSAGE_MENTIONED -> MESSAGE_MENTIONED_CONTENT.formatted(displayName, conversationName);
            case NOTIFICATION_MESSAGE_REACTED -> MESSAGE_REACTED_CONTENT.formatted(displayName, conversationName);
            default -> DEFAULT_NOTIFICATION_CONTENT;
        };
    }

    private static String extractDisplayName(User user) {
        if (user != null && user.getDisplayName() != null) {
            return user.getDisplayName();
        }
        return DEFAULT_DISPLAY_NAME;
    }

    private static String extractConversationName(Conversation conversation) {
        if (conversation != null && conversation.getConversationName() != null) {
            return conversation.getConversationName();
        }
        return DEFAULT_CONVERSATION_NAME;
    }

}
