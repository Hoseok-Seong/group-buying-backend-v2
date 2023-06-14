package shop.donutmarket.donut.domain.chat.dto;

import lombok.Getter;
import lombok.Setter;
import shop.donutmarket.donut.domain.chat.model.ChatMessage;

@Getter
@Setter
public class ChatMessageResp {

    private ChatMessage chatMessage;

    public ChatMessageResp(ChatMessage chatMessage) {
        this.chatMessage = chatMessage;
    }
}
