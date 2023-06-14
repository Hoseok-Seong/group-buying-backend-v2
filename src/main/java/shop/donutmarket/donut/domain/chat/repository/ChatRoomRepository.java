package shop.donutmarket.donut.domain.chat.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import shop.donutmarket.donut.domain.chat.model.ChatRoom;

import java.util.Optional;

public interface ChatRoomRepository extends JpaRepository<ChatRoom, Long> {

    Optional<ChatRoom> findChatRoomByChatroomIdAndUserId(Long chatroomId, Long userId);
}
