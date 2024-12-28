package com.chat.notification_manager.event.upstream;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MessageReactedEvent {

  public enum Reaction {
    LIKE,
    LOVE,
    HAHA,
    WOW,
    SAD,
    ANGRY;

    public static Reaction getReaction(String reaction) {
      for (Reaction r : Reaction.values()) {
        if (r.name().equalsIgnoreCase(reaction)) {
          return r;
        }
      }
      return null;
    }
  }

  private String senderId;
  private String messageId;
  private String conversationId;
  private Long createdAt;
  private String messageContent;
  private Reaction reaction;
}
