package shop.donutmarket.donut.domain.chat.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import shop.donutmarket.donut.domain.chat.dto.ChatMessageReq;
import shop.donutmarket.donut.domain.chat.dto.ChatMessageResp;
import shop.donutmarket.donut.domain.chat.dto.ChatRoomReq;
import shop.donutmarket.donut.domain.chat.dto.ChatRoomReq.GroupChatRoomAddMember;
import shop.donutmarket.donut.domain.chat.dto.ChatRoomReq.GroupChatRoomDeleteMember;
import shop.donutmarket.donut.domain.chat.dto.ChatRoomReq.GroupChatRoomInit;
import shop.donutmarket.donut.domain.chat.dto.ChatRoomResp;
import shop.donutmarket.donut.domain.chat.dto.ChatRoomResp.GroupChatRoomResp;
import shop.donutmarket.donut.domain.chat.service.ChatMessageService;
import shop.donutmarket.donut.domain.chat.service.ChatRoomService;
import shop.donutmarket.donut.global.auth.MyUserDetails;
import shop.donutmarket.donut.global.dto.ResponseDTO;

@RestController
@Slf4j
@RequiredArgsConstructor
public class ChatController {

    private final ChatRoomService chatRoomService;

    private final ChatMessageService chatMessageService;

    private final SimpMessageSendingOperations simpMessageSendingOperations;

    // 메세지 전송 및 메세지 DB 저장
    // /pub/chatroom
    @MessageMapping("/chatroom")
    public void sendMessage(ChatMessageReq chatMessageReq) {
        simpMessageSendingOperations.convertAndSend
                ("/sub/chatroom/" + chatMessageReq.getChatroomId(), chatMessageReq);
        log.info("메세지 전송 성공");
        chatMessageService.saveMessage(chatMessageReq);
    }

    // 채팅방 개설
    @PostMapping("/chatroom/group/init")
    public ResponseEntity<?> chatroomInit(@RequestBody @Valid GroupChatRoomInit chatRoomInit,
                                          @AuthenticationPrincipal MyUserDetails myUserDetails,
                                          BindingResult bindingResult) {
        GroupChatRoomResp respDTO = chatRoomService.init(chatRoomInit, myUserDetails);
        ResponseDTO<?> responseDTO = new ResponseDTO<>(respDTO);
        return ResponseEntity.ok(responseDTO);
    }

    // 채팅방 멤버 초대
    @PostMapping("/chatroom/group/members")
    public ResponseEntity<?> chatroomAdd(@RequestBody @Valid GroupChatRoomAddMember chatRoomAddMember,
                            @AuthenticationPrincipal MyUserDetails myUserDetails,
                            BindingResult bindingResult) {
        GroupChatRoomResp respDTO = chatRoomService.addMember(chatRoomAddMember, myUserDetails);
        ResponseDTO<?> responseDTO = new ResponseDTO<>(respDTO);
        return ResponseEntity.ok(responseDTO);
    }

    // 채팅방 멤버 퇴장
    @DeleteMapping("/chatroom/group/members")
    public ResponseEntity<?> chatroomDelete(@RequestBody @Valid GroupChatRoomDeleteMember groupChatRoomDeleteMember,
                                            @AuthenticationPrincipal MyUserDetails myUserDetails,
                                            BindingResult bindingResult) {
        return chatRoomService.deleteMember(groupChatRoomDeleteMember, myUserDetails);
    }
}
