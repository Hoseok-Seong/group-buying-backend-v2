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

import java.time.LocalDateTime;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class ChatRoom {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long boardId;
    private Long creatorId;
    private Long userId;
    private ChatType chatType;
    private Integer statusCode;
    @CreationTimestamp
    private LocalDateTime createdAt;

    @Builder
    public ChatRoom(Long id, Long boardId, Long creatorId, Long userId, ChatType chatType, Integer statusCode, LocalDateTime createdAt) {
        this.id = id;
        this.boardId = boardId;
        this.creatorId = creatorId;
        this.userId = userId;
        this.chatType = chatType;
        this.statusCode = statusCode;
        this.createdAt = createdAt;
    }
}
