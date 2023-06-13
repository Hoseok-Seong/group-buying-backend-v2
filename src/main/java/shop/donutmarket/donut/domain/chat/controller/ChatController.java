package shop.donutmarket.donut.domain.chat.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import shop.donutmarket.donut.domain.chat.dto.ChatMessageReq;
import shop.donutmarket.donut.domain.chat.service.ChatMessageService;
import shop.donutmarket.donut.domain.chat.service.ChatRoomService;

@Controller
@RequiredArgsConstructor
public class ChatController {

    private final ChatRoomService chatRoomService;

    private final ChatMessageService chatMessageService;

    private final SimpMessageSendingOperations simpMessageSendingOperations;

    @MessageMapping("/chatroom/{id}")
    public void sendMessage(@DestinationVariable("id") Long id, ChatMessageReq chatMessageReq) {
        System.out.println("id = " + id);
        System.out.println("chatMessageReq RoomId = " + chatMessageReq.getChatroomId());
        System.out.println("chatMessageReq SenderId = " + chatMessageReq.getSenderId());
        System.out.println("chatMessageReq Message = " + chatMessageReq.getMessage());
        simpMessageSendingOperations.convertAndSend
                ("/sub/chatroom/" + chatMessageReq.getChatroomId(), chatMessageReq);
    }

    @PostMapping("/chatroom")
    public void makeChatroom() {

    }
}
