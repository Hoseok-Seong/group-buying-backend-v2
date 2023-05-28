package shop.donutmarket.donut.domain.mylocation;

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
import org.springframework.transaction.annotation.Transactional;

import shop.donutmarket.donut.domain.account.model.MyAccount;
import shop.donutmarket.donut.domain.board.model.Board;
import shop.donutmarket.donut.domain.myLocation.model.MyLocation;
import shop.donutmarket.donut.domain.myLocation.repository.MyLocationRepository;
import shop.donutmarket.donut.domain.user.model.User;

import static org.junit.jupiter.api.Assertions.*;

@ActiveProfiles("test")
@DataJpaTest
public class MyLocationRepositoryTest {
    
    @Autowired
    private MyLocationRepository myLocationRepository;

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
    @DisplayName("MyLocation 전체 조회 테스트")
    void findAll_Test() {
        // given

        // when
        List<MyLocation> myLocations = myLocationRepository.findAll();

        // then
        assertEquals(myLocations.size(), 1);
    }

    @Test
    @DisplayName("MyLocation 개별 id조회 테스트")
    void findById_Test(){
        // given
        Long id = 1L;
        
        // when
        Optional<MyLocation> myLocation = myLocationRepository.findById(id);

        // then
        myLocation.ifPresent(myLocation1 -> {
            assertNotNull(myLocation1);
            assertEquals(myLocation1.getId(), 1L);
        });
    }

    @Test
    @DisplayName("MyLocation 생성 테스트")
    void save_Test() {
        // given
        User user = User.builder().build();
        MyLocation myLocation = MyLocation.builder().user(user).state("부산광역시").city("부산진구").town("부암동").build();

        // when
        myLocationRepository.save(myLocation);

        // then
        assertNotNull(myLocation);
        assertEquals(myLocation.getId(), 2L);
        assertEquals(myLocation.getTown(), "부암동");
    }

    @Test
    @DisplayName("MyLocation 삭제 테스트")
    void deleteById_Test(){
        // given
        Long id = 1L;
        MyLocation myLocation = tem.find(MyLocation.class, id);

        // when
        if (myLocation != null) {
            tem.remove(myLocation);
            tem.flush();
        }

        // then
        assertNull(tem.find(MyLocation.class, id));
    }

    @Test
    @DisplayName("MyLocation 수정 테스트")
    void updateById_Test() {
        // given
        Long id = 1L;
        MyLocation myLocation = tem.find(MyLocation.class, id);
        LocalDateTime time = LocalDateTime.now();

        // when
        myLocation.updateMyLocation("부산광역시", "부산진구", "양정동", time);
        tem.persistAndFlush(myLocation);

        // then
        assertEquals(myLocation.getTown(), "양정동");
    }


    private void dataSetting() {
        User user = User.builder().build();
        MyLocation myLocation = MyLocation.builder().user(user).state("부산광역시").city("부산진구").town("부전동").build();
        myLocationRepository.save(myLocation);
    }

    private void autoincrementReset() {
        em.createNativeQuery("ALTER TABLE my_location ALTER COLUMN `id` RESTART WITH 1").executeUpdate();
    }
}
