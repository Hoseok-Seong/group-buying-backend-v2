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
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import shop.donutmarket.donut.domain.chat.dto.ChatMessageReq;
import shop.donutmarket.donut.domain.chat.dto.ChatMessageResp;
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

    // /pub/chatroom/id
    @MessageMapping("/chatroom/{id}")
    public ResponseEntity<?> sendMessage(@DestinationVariable("id") Long id, ChatMessageReq chatMessageReq) {
        simpMessageSendingOperations.convertAndSend
                ("/sub/chatroom/" + chatMessageReq.getChatroomId(), chatMessageReq);
        ChatMessageResp respDTO = chatMessageService.saveMessage(chatMessageReq);
        ResponseDTO<?> responseDTO = new ResponseDTO<>(respDTO);
        return ResponseEntity.ok(responseDTO);
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
