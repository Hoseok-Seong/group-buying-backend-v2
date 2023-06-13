package shop.donutmarket.donut.domain.chat.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import shop.donutmarket.donut.domain.board.model.Board;
import shop.donutmarket.donut.domain.board.repository.BoardRepository;
import shop.donutmarket.donut.domain.chat.dto.ChatRoomReq.GroupChatRoomAddMember;
import shop.donutmarket.donut.domain.chat.dto.ChatRoomReq.GroupChatRoomInit;
import shop.donutmarket.donut.domain.chat.dto.ChatRoomResp;
import shop.donutmarket.donut.domain.chat.dto.ChatRoomResp.GroupChatRoomResp;
import shop.donutmarket.donut.domain.chat.model.ChatRoom;
import shop.donutmarket.donut.domain.chat.repository.ChatRoomRepository;
import shop.donutmarket.donut.domain.participant.model.Participant;
import shop.donutmarket.donut.domain.participant.repository.ParticipantRepository;
import shop.donutmarket.donut.domain.user.model.User;
import shop.donutmarket.donut.domain.user.repository.UserRepository;
import shop.donutmarket.donut.global.auth.MyUserDetails;
import shop.donutmarket.donut.global.exception.Exception403;
import shop.donutmarket.donut.global.exception.Exception404;
import shop.donutmarket.donut.global.exception.Exception500;

import java.util.Objects;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ChatRoomService {

    private final ChatRoomRepository chatRoomRepository;
    private final UserRepository userRepository;
    private final BoardRepository boardRepository;
    private final ParticipantRepository participantRepository;

    @Transactional
    public GroupChatRoomResp init(GroupChatRoomInit chatRoomInit, MyUserDetails myUserDetails) {

        Optional<User> userOP = userRepository.findByIdJoinFetch(myUserDetails.getUser().getId());

        if (userOP.isEmpty()) {
            throw new Exception404("존재하지 않는 사용자입니다");
        }

        if (!Objects.equals(myUserDetails.getUser().getId(), chatRoomInit.getCreatorId())) {
            throw new Exception403("채팅방을 개설할 권한이 없습니다");
        }

        Optional<Board> boardOP = boardRepository.findByIdWithAll(chatRoomInit.getBoardId());

        if (boardOP.isEmpty()) {
            throw new Exception404("존재하지 않는 게시글입니다");
        }

        if (!Objects.equals(boardOP.get().getOrganizer().getId(), chatRoomInit.getCreatorId())) {
            throw new Exception403("채팅방을 개설할 권한이 없습니다");
        }

        try {
            ChatRoom chatRoom = chatRoomInit.toEntity();
            ChatRoom chatRoomPS = chatRoomRepository.save(chatRoom);
            GroupChatRoomResp groupChatRoomResp = new GroupChatRoomResp(chatRoomPS);
            return groupChatRoomResp;
        } catch (Exception e) {
            throw new Exception500("채팅방 개설하기 실패 : " + e.getMessage());
        }
    }

    @Transactional
    public GroupChatRoomResp addMember(GroupChatRoomAddMember chatRoomAddMember, MyUserDetails myUserDetails) {
        Optional<User> userOP = userRepository.findByIdJoinFetch(myUserDetails.getUser().getId());

        if (userOP.isEmpty()) {
            throw new Exception404("존재하지 않는 사용자입니다");
        }

        Optional<ChatRoom> chatRoomOP = chatRoomRepository.findById(chatRoomAddMember.getChatroomId());

        if (chatRoomOP.isEmpty()) {
            throw new Exception404("존재하지 않는 채팅방입니다");
        }

        Optional<Board> boardOP = boardRepository.findByIdWithAll(chatRoomAddMember.getChatroomId());

        if (boardOP.isEmpty()) {
            throw new Exception404("존재하지 않는 게시글입니다");
        }

        Optional<Participant> participantOP = participantRepository.findByUserIdAndEvendId(
                myUserDetails.getUser().getId(),
                boardOP.get().getEvent().getId());

        if (participantOP.isEmpty()) {
            throw new Exception403("채팅방에 참여할 권한이 없습니다");
        }

        try {
            ChatRoom chatRoom = chatRoomAddMember.toEntity(chatRoomOP.get());
            ChatRoom chatRoomPS = chatRoomRepository.save(chatRoom);
            GroupChatRoomResp groupChatRoomResp = new GroupChatRoomResp(chatRoomPS);
            return groupChatRoomResp;
        } catch (Exception e) {
            throw new Exception500("채팅방 개설하기 실패 : " + e.getMessage());
        }
    }
}
