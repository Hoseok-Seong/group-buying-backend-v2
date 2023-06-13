package shop.donutmarket.donut.domain.chat.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import shop.donutmarket.donut.domain.chat.model.ChatRoom;

public interface ChatRoomRepository extends JpaRepository<ChatRoom, Long> {
}
