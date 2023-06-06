package shop.donutmarket.donut.domain.board.model;

import java.time.LocalDateTime;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import shop.donutmarket.donut.domain.admin.model.Category;
import shop.donutmarket.donut.domain.user.model.User;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class Board {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private Category category;
    private String title;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "organizer_id")
    private User organizer;
    private String content;
    private String imgKey;
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "event_id")
    private Event event;
    private Integer statusCode;
    private int views;
    private boolean recommend;
    private String state;
    private String city;
    private String town;
    @CreationTimestamp
    private LocalDateTime createdAt;

    @Builder
    public Board(Long id, Category category, String title, User organizer, String content, String imgKey, Event event, Integer statusCode, int views, boolean recommend, String state, String city, String town, LocalDateTime createdAt) {
        this.id = id;
        this.category = category;
        this.title = title;
        this.organizer = organizer;
        this.content = content;
        this.imgKey = imgKey;
        this.event = event;
        this.statusCode = statusCode;
        this.views = views;
        this.recommend = recommend;
        this.state = state;
        this.city = city;
        this.town = town;
        this.createdAt = createdAt;
    }

    public void updateStatusCode(Integer statusCode) {
        this.statusCode = statusCode;
    }
}
