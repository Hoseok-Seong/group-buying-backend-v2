package shop.donutmarket.donut.domain.blacklist.model;

import java.time.LocalDateTime;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import shop.donutmarket.donut.domain.user.model.User;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class BlackList {
        
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "blocked_user_id")
    private User blockedUser;
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Builder
    public BlackList(Long id, User user, User blockedUser, LocalDateTime createdAt) {
        this.id = id;
        this.user = user;
        this.blockedUser = blockedUser;
        this.createdAt = createdAt;
    }
}
