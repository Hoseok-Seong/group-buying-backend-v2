package shop.donutmarket.donut.domain.chat.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import shop.donutmarket.donut.domain.chat.dto.ChatMessageReq;
import shop.donutmarket.donut.domain.chat.dto.ChatRoomReq;
import shop.donutmarket.donut.domain.chat.dto.ChatRoomReq.GroupChatRoomAddMember;
import shop.donutmarket.donut.domain.chat.dto.ChatRoomReq.GroupChatRoomInit;
import shop.donutmarket.donut.domain.chat.dto.ChatRoomResp;
import shop.donutmarket.donut.domain.chat.dto.ChatRoomResp.GroupChatRoomResp;
import shop.donutmarket.donut.domain.chat.service.ChatMessageService;
import shop.donutmarket.donut.domain.chat.service.ChatRoomService;
import shop.donutmarket.donut.global.auth.MyUserDetails;
import shop.donutmarket.donut.global.dto.ResponseDTO;

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

    @PostMapping("/chatroom/group/init")
    public ResponseEntity<?> chatroomInit(@RequestBody @Valid GroupChatRoomInit chatRoomInit,
                                          @AuthenticationPrincipal MyUserDetails myUserDetails,
                                          BindingResult bindingResult) {
        GroupChatRoomResp respDTO = chatRoomService.init(chatRoomInit, myUserDetails);
        ResponseDTO<?> responseDTO = new ResponseDTO<>(respDTO);
        return ResponseEntity.ok(responseDTO);
    }

    @PostMapping("/chatroom/group/members/add")
    public ResponseEntity<?> chatroomAdd(@RequestBody @Valid GroupChatRoomAddMember chatRoomAddMember,
                            @AuthenticationPrincipal MyUserDetails myUserDetails,
                            BindingResult bindingResult) {
        GroupChatRoomResp respDTO = chatRoomService.addMember(chatRoomAddMember, myUserDetails);
        ResponseDTO<?> responseDTO = new ResponseDTO<>(respDTO);
        return ResponseEntity.ok(responseDTO);
    }
}
