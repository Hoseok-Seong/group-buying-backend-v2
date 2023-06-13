package shop.donutmarket.donut.domain.chat.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import shop.donutmarket.donut.domain.chat.model.ChatMessage;

public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {
}
