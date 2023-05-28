package shop.donutmarket.donut.domain.review;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import org.springframework.test.context.ActiveProfiles;
import shop.donutmarket.donut.domain.account.model.MyAccount;
import shop.donutmarket.donut.domain.board.model.Board;
import shop.donutmarket.donut.domain.review.model.Review;
import shop.donutmarket.donut.domain.review.repository.ReviewRepository;
import shop.donutmarket.donut.domain.user.model.User;

import static org.junit.jupiter.api.Assertions.*;

@ActiveProfiles("test")
@DataJpaTest
public class ReviewRepositoryTest {
    
    @Autowired
    private ReviewRepository reviewRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private TestEntityManager tem; // 테스트하기 위한 EntityManager

    @BeforeEach
    void setUp(){
        autoincrementReset(); // autoincrement 보장해주는 메서드
        dataSetting(); // 초기 dummy 데이터 세팅
        tem.clear(); // 영속성 컨텍스트 비우기
    }

    @Test
    @DisplayName("Review 전체 조회 테스트")
    void findAll_Test() {
        // given

        // when
        List<Review> reviews = reviewRepository.findAll();

        // then
        assertEquals(reviews.size(), 1);
    }

    @Test
    @DisplayName("Review 개별 id조회 테스트")
    void findById_Test(){
        // given
        Long id = 1L;
        
        // when
        Optional<Review> review = reviewRepository.findById(id);
        
        // then
        review.ifPresent(review1 -> {
            assertNotNull(review1);
            assertEquals(review1.getId(), 1L);
        });
    }
    
    @Test
    @DisplayName("Review 생성 테스트")
    void save_Test() {
        // given
        User user1 = User.builder().build();
        User user2 = User.builder().build();
        Review review = Review.builder().reviewer(user1).reviewed(user2).score(4).comment("리뷰내용2").createdAt(LocalDateTime.now()).build();

        // when
        reviewRepository.save(review);

        // then
        assertNotNull(review);
        assertEquals(review.getId(), 2L);
        assertEquals(review.getComment(), "리뷰내용2");
    }

    @Test
    @DisplayName("Review 삭제 테스트")
    void deleteById_Test(){
        // given
        Long id = 1L;
        Review review = tem.find(Review.class, id);

        // when
        if (review != null) {
            tem.remove(review);
            tem.flush();
        }

        // then
        assertNull(tem.find(Review.class, id));
    }

    @Test
    @DisplayName("Review 수정 테스트")
    void updateById_Test() {
        // given
        Long id = 1L;
        Review review = tem.find(Review.class, id);
        User user1 = User.builder().build();
        User user2 = User.builder().build();
        LocalDateTime time = LocalDateTime.now();
        tem.persist(user1);
        tem.persist(user2);

        // when
        review.updateReview(user1, user2, 5, "좋은 거래였습니다", time);
        tem.persistAndFlush(review);

        // then
        assertEquals(review.getComment(), "좋은 거래였습니다");
    }

    private void dataSetting() {
        User user1 = User.builder().build();
        User user2 = User.builder().build();
        Review review = Review.builder().reviewer(user1).reviewed(user2).score(5).comment("리뷰내용").createdAt(LocalDateTime.now()).build();
        reviewRepository.save(review);
    }

    private void autoincrementReset() {
        em.createNativeQuery("ALTER TABLE review ALTER COLUMN `id` RESTART WITH 1").executeUpdate();
    }
}
