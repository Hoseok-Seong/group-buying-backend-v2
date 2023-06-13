package shop.donutmarket.donut.domain.chat.dto;

import lombok.Getter;
import lombok.Setter;
import shop.donutmarket.donut.domain.chat.model.ChatRoom;

@Getter
@Setter
public class ChatRoomResp {

    @Getter
    @Setter
    public static class GroupChatRoomResp {
        private ChatRoom chatRoom;

        public GroupChatRoomResp(ChatRoom chatRoom) {
            this.chatRoom = chatRoom;
        }
    }
}
