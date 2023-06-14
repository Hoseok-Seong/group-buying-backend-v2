package shop.donutmarket.donut.domain.chat.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import shop.donutmarket.donut.domain.chat.dto.ChatMessageReq;
import shop.donutmarket.donut.domain.chat.dto.ChatMessageResp;
import shop.donutmarket.donut.domain.chat.dto.ChatRoomResp;
import shop.donutmarket.donut.domain.chat.model.ChatMessage;
import shop.donutmarket.donut.domain.chat.model.ChatRoom;
import shop.donutmarket.donut.domain.chat.repository.ChatMessageRepository;
import shop.donutmarket.donut.domain.chat.repository.ChatRoomRepository;
import shop.donutmarket.donut.global.exception.Exception404;
import shop.donutmarket.donut.global.exception.Exception500;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ChatMessageService {

    private final ChatMessageRepository chatMessageRepository;
    private final ChatRoomRepository chatRoomRepository;

    @Transactional
    public ChatMessageResp saveMessage(ChatMessageReq chatMessageReq) {

        Optional<ChatRoom> chatRoomOP = chatRoomRepository
                .findChatRoomByChatroomIdAndUserId(
                        chatMessageReq.getChatroomId(), chatMessageReq.getSenderId());

        if (chatRoomOP.isEmpty()) {
            throw new Exception404("해당 유저가 존재하지 않거나 존재하지 않는 채팅방입니다");
        }

        try {
            ChatMessage chatMessage = chatMessageReq.toEntity();
            ChatMessage chatMessagePS = chatMessageRepository.save(chatMessage);
            ChatMessageResp chatMessageResp = new ChatMessageResp(chatMessagePS);
            return chatMessageResp;
        } catch (Exception e) {
            throw new Exception500("메세지 저장하기 실패 : " + e.getMessage());
        }
    }
}
