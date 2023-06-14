package shop.donutmarket.donut.domain.chat.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import shop.donutmarket.donut.domain.chat.model.ChatRoom;
import shop.donutmarket.donut.domain.chat.model.ChatType;

@Getter
@Setter
public class ChatRoomReq {

    @Getter
    @Setter
    public static class GroupChatRoomInit {
        @NotNull(message = "게시글 id를 입력해주세요")
        private Long boardId;
        @NotNull(message = "채팅방 개설자 id를 입력해주세요")
        private Long creatorId;
        @NotNull(message = "채팅방 id를 입력해주세요")
        private ChatType chatType;

        public ChatRoom toEntity() {
            return ChatRoom.builder()
                    .chatroomId(boardId)
                    .boardId(boardId)
                    .creatorId(creatorId)
                    .userId(creatorId)
                    .chatType(chatType)
                    .build();
        }
    }

    @Getter
    @Setter
    public static class GroupChatRoomAddMember {
        @NotNull(message = "채팅방 id를 입력해주세요")
        private Long chatroomId;
        @NotNull(message = "유저 id를 입력해주세요")
        private Long userId;

        public ChatRoom toEntity(ChatRoom chatRoom) {
            return ChatRoom.builder()
                    .chatroomId(chatRoom.getBoardId())
                    .boardId(chatRoom.getBoardId())
                    .creatorId(chatRoom.getCreatorId())
                    .userId(userId)
                    .chatType(chatRoom.getChatType())
                    .build();
        }
    }

    @Getter
    @Setter
    public static class GroupChatRoomDeleteMember {
        @NotNull(message = "채팅방 id를 입력해주세요")
        private Long chatroomId;
        @NotNull(message = "유저 id를 입력해주세요")
        private Long userId;
    }
}
