package shop.donutmarket.donut.domain.user;

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
import shop.donutmarket.donut.domain.review.model.Rate;
import shop.donutmarket.donut.domain.user.model.User;
import shop.donutmarket.donut.domain.user.repository.UserRepository;

import static org.junit.jupiter.api.Assertions.*;

@ActiveProfiles("test")
@DataJpaTest
public class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private TestEntityManager tem; // 테스트하기 위한 EntityManager

    @BeforeEach
    void setUp() {
        autoincrementReset(); // autoincrement 보장해주는 메서드
        dataSetting(); // 초기 dummy 데이터 세팅
        tem.clear(); // 영속성 컨텍스트 비우기
    }

    @Test
    @DisplayName("User 전체 조회 테스트")
    void findAll_Test() {
        // given

        // when
        List<User> users = userRepository.findAll();

        // then
        assertEquals(users.size(), 1);
    }

    @Test
    @DisplayName("User 개별 id조회 테스트")
    void findById_Test() {
        // given
        Long id = 1L;

        // when
        Optional<User> user = userRepository.findById(id);

        // then
        user.ifPresent(user1 -> {
            assertNotNull(user1);
            assertEquals(user1.getId(), 1L);
        });
    }

    @Test
    @DisplayName("User 생성 테스트")
    void save_Test() {
        // given
        Rate rate = Rate.builder().build();
        User user = User.builder().password("1234").email("cos@cos").rate(rate).role("user")
                .createdAt(LocalDateTime.now()).build();

        // when
        userRepository.save(user);

        // then
        assertNotNull(user);
        assertEquals(user.getId(), 2L);
    }

    @Test
    @DisplayName("User 삭제 테스트")
    void deleteById_Test() {
        // given
        Long id = 1L;
        User user = tem.find(User.class, id);

        // when
        if (user != null) {
            tem.remove(user);
            tem.flush();
        }

        // then
        assertNull(tem.find(User.class, id));
    }

    @Test
    @DisplayName("User 수정 테스트")
    void updateById_Test() {
        // given
        Long id = 1L;
        User user = tem.find(User.class, id);
        LocalDateTime time = LocalDateTime.now();

        // when
        user.updateUser("3456", "src/main/resources/static/images.png", time);
        tem.persistAndFlush(user);

        // then
        assertEquals(user.getProfile(), "src/main/resources/static/images.png");
    }

    private void dataSetting() {
        Rate rate = Rate.builder().build();
        User user = User.builder().password("1234").email("ssar@ssar").rate(rate).role("user")
                .createdAt(LocalDateTime.now()).build();
        userRepository.save(user);
    }

    private void autoincrementReset() {
        em.createNativeQuery("ALTER TABLE user_tb ALTER COLUMN `id` RESTART WITH 1").executeUpdate();
    }
}
