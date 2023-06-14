package shop.donutmarket.donut.domain.chat.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import shop.donutmarket.donut.domain.chat.model.ChatMessage;

@Getter
@Setter
public class ChatMessageReq {
    @NotNull(message = "채팅방 id를 입력해주세요")
    private Long chatroomId;
    @NotNull(message = "유저 id를 입력해주세요")
    private Long senderId;
    @NotNull(message = "채팅 메세지를 입력해주세요")
    private String message;

    public ChatMessage toEntity() {
        return ChatMessage.builder()
                .chatroomId(chatroomId)
                .senderId(senderId)
                .message(message)
                .build();
    }
}
