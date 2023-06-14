package shop.donutmarket.donut.domain.chat.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.springframework.security.core.parameters.P;

import java.time.LocalDateTime;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class ChatMessage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long chatroomId;
    private Long senderId;
    private String message;
    @CreationTimestamp
    private LocalDateTime createdAt;

    @Builder
    public ChatMessage(Long id, Long chatroomId, Long senderId, String message, LocalDateTime createdAt) {
        this.id = id;
        this.chatroomId = chatroomId;
        this.senderId = senderId;
        this.message = message;
        this.createdAt = createdAt;
    }
}
