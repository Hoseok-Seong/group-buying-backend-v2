package shop.donutmarket.donut.domain.blacklist;

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
import shop.donutmarket.donut.domain.blacklist.model.Blacklist;
import shop.donutmarket.donut.domain.blacklist.repository.BlackListRepository;
import shop.donutmarket.donut.domain.user.model.User;

import static org.junit.jupiter.api.Assertions.*;

@ActiveProfiles("test")
@DataJpaTest
public class BlacklistRepositoryTest {
    
    @Autowired
    private BlackListRepository blackListRepository;

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
    @DisplayName("Blacklist 전체 조회 테스트")
    void findAll_Test() {
        // given

        // when
        List<Blacklist> blacklists = blackListRepository.findAll();

        // then
        assertEquals(blacklists.size(), 1);
    }
    
    @Test
    @DisplayName("Blacklist 개별 id조회 테스트")
    void findById_Test(){
        // given
        Long id = 1L;
        // when
        Optional<Blacklist> blacklist = blackListRepository.findById(id);
        // then
        assertNotNull(blacklist);
    }
    
    
    @Test
    @DisplayName("Blacklist 생성 테스트")
    void save_Test(){
        // given
        User user1 = User.builder().build();
        User user2 = User.builder().build();
        Blacklist blackList = Blacklist.builder().user(user1).blockedUser(user2).createdAt(LocalDateTime.now()).build();

        // when
        blackListRepository.save(blackList);
        
        // then
        assertNotNull(blackList);
        assertEquals(blackList.getId(), 2L);
    }

    @Test
    @DisplayName("Blacklist 삭제 테스트")
    void deleteById_Test(){
        // given
        Long id = 1L;
        Blacklist blackList = tem.find(Blacklist.class, id);

        // when
        blackListRepository.deleteById(id);
        if (blackList != null) {
            tem.remove(blackList);
            tem.flush();
        }

        // then
        assertNull(tem.find(Blacklist.class, id));
    }

    private void dataSetting() {
        User user1 = User.builder().build();
        User user2 = User.builder().build();
        Blacklist blackList = Blacklist.builder().user(user1).blockedUser(user2).createdAt(LocalDateTime.now()).build();
        blackListRepository.save(blackList);
    }

    private void autoincrementReset() {
        em.createNativeQuery("ALTER TABLE blacklist ALTER COLUMN `id` RESTART WITH 1").executeUpdate();
    }
}