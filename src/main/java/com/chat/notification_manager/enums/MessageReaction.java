package com.chat.notification_manager.enums;

public enum MessageReaction {
  LIKE,
  LOVE,
  HAHA,
  WOW,
  SAD,
  ANGRY;

  public static MessageReaction getMessageReaction(String reaction) {
    for (MessageReaction r : MessageReaction.values()) {
      if (r.name().equalsIgnoreCase(reaction)) {
        return r;
      }
    }
    return null;
  }
}
