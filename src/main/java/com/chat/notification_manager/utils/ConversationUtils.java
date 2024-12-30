package com.chat.notification_manager.utils;

import com.chat.notification_manager.document.Conversation;
import com.chat.notification_manager.enums.ConversationType;
import com.chat.notification_manager.event.upstream.conversation.NewConversationEventData;
import com.chat.notification_manager.exception.ApplicationException;
import com.chat.notification_manager.exception.ErrorCode;
import com.chat.notification_manager.model.ConversationMember;
import java.util.ArrayList;
import java.util.List;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ConversationUtils {
  public static String getConversationName(
      Conversation conversation, String currentUserId, String requestId) {
    if (ConversationType.GROUP.equals(conversation.getConversationType())) {
      return constructGroupConversationName(conversation);
    } else {
      return conversationDirectConversationName(conversation, currentUserId, requestId);
    }
  }

  public static List<String> getConversationAvatar(
      Conversation conversation, String currentUserId, String requestId) {
    if (ConversationType.GROUP.equals(conversation.getConversationType())) {
      return getAvatarForGroupConversation(conversation);
    } else {
      return getAvatarForDirectConversation(conversation, currentUserId, requestId);
    }
  }

  private static String conversationDirectConversationName(
      Conversation conversation, String currentUserId, String requestId) {
    // a direct conversation only has 2 members
    // return name of the other user as the name for the conversation
    List<ConversationMember> members = getMemberList(conversation);
    for (ConversationMember member : members) {
      boolean isOtherUser = !member.getId().equals(currentUserId);
      if (isOtherUser) {
        return member.getDisplayName();
      }
    }

    throw new ApplicationException(ErrorCode.NOTIFICATION_ERROR3, requestId);
  }

  private static String constructGroupConversationName(Conversation conversation) {
    String groupConversationName = conversation.getGroupConversationName();
    // if there is no name for the group conversation, create a default name by concatinating member
    // names
    if (groupConversationName == null) {
      StringBuilder sb = new StringBuilder();

      List<ConversationMember> members = getMemberList(conversation);
      for (int i = 0; i < members.size() - 1; i++) {
        sb.append(members.get(i).getDisplayName().trim());
        sb.append(", ");
      }

      // add last member
      sb.append(members.get(members.size() - 1).getDisplayName().trim());

      groupConversationName = sb.toString().trim();
    }

    return groupConversationName;
  }

  private static List<String> getAvatarForDirectConversation(
      Conversation conversation, String currentUserId, String requestId) {
    // return the other member's avatar as the avatar of the conversation
    List<String> avatarList = new ArrayList<>();
    List<ConversationMember> members = getMemberList(conversation);

    for (ConversationMember member : members) {
      boolean isOtherUser = !member.getId().equals(currentUserId);
      if (isOtherUser) {
        avatarList.add(member.getAvatar());
        return avatarList;
      }
    }

    if (checkIfSelfConversation(conversation, currentUserId)) {
      avatarList.add(members.get(0).getAvatar());
      return avatarList;
    }

    throw new ApplicationException(ErrorCode.NOTIFICATION_ERROR4, requestId);
  }

  private static boolean checkIfSelfConversation(Conversation conversation, String userId) {
    List<ConversationMember> members = getMemberList(conversation);

    // check if all members in the conversation is the current user
    return members.stream().allMatch(u -> u.getId().equals(userId));
  }

  private static List<String> getAvatarForGroupConversation(Conversation conversation) {
    List<String> avatarList = new ArrayList<>();
    // if the group has an avatar, return it
    if (conversation.getGroupConversationAvatar() != null) {
      avatarList.add(conversation.getGroupConversationAvatar());
    } else {
      // else return list of all first 3 member avatars
      List<ConversationMember> members = getMemberList(conversation);
      for (int i = 0; i < Math.min(3, members.size()); i++) {
        avatarList.add(members.get(i).getAvatar());
      }
    }
    return avatarList;
  }

  public static List<ConversationMember> getMemberList(Conversation conversation) {
    return new ArrayList<>(conversation.getMemberDetails().values());
  }

  public static Conversation createConversation(NewConversationEventData newConversationEventData) {
    return Conversation.builder()
        .id(newConversationEventData.getConversationId())
        .memberDetails(newConversationEventData.getMemberDetails())
        .conversationType(newConversationEventData.getConversationType())
        .groupConversationName(newConversationEventData.getGroupConversationName())
        .groupConversationAvatar(newConversationEventData.getGroupConversationAvatar())
        .createdAt(newConversationEventData.getCreatedAt())
        .build();
  }
}
