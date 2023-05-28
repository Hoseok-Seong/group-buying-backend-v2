package shop.donutmarket.donut.domain.participant;

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
import shop.donutmarket.donut.domain.board.model.Event;
import shop.donutmarket.donut.domain.participant.model.Participant;
import shop.donutmarket.donut.domain.participant.repository.ParticipantRepository;
import shop.donutmarket.donut.domain.user.model.User;

import static org.junit.jupiter.api.Assertions.*;

@ActiveProfiles("test")
@DataJpaTest
public class ParticipantRepositoryTest {
    
    @Autowired
    private ParticipantRepository participantRepository;

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
    @DisplayName("Participant 전체 조회 테스트")
    void findAll_Test() {
        // given

        // when
        List<Participant> participants = participantRepository.findAll();

        // then
        assertEquals(participants.size(), 1);
    }
    
    @Test
    @DisplayName("Participant 개별 id조회 테스트")
    void findById_Test(){
        // given
        Long id = 1L;
        
        // when
        Optional<Participant> participant = participantRepository.findById(id);
        
        // then
        participant.ifPresent(participant1 -> {
            assertNotNull(participant1);
            assertEquals(participant1.getId(), 1L);
        });
    }
    
    @Test
    @DisplayName("Participant 생성 테스트")
    void save_Test() {
        // given
        Event event = Event.builder().build();
        User user = User.builder().build();
        Participant participant = Participant.builder().event(event).user(user).qty(5).createdAt(LocalDateTime.now()).limitTime(LocalDateTime.now()).build();

        // when
        participantRepository.save(participant);

        // then
        assertNotNull(participant);
        assertEquals(participant.getQty(), 5);
    }

    @Test
    @DisplayName("Participant 삭제 테스트")
    void deleteById_Test(){
        // given
        Long id = 1L;
        Participant participant = tem.find(Participant.class, id);

        // when
        if (participant != null) {
            tem.remove(participant);
            tem.flush();
        }

        // then
        assertNull(tem.find(Participant.class, id));
    }

    @Test
    @DisplayName("Participant 수정 테스트")
    void updateById_Test() {
        // given
        Long id = 1L;
        Participant participant = tem.find(Participant.class, id);
        Event event = Event.builder().build();
        User user = User.builder().build();
        LocalDateTime time1 = LocalDateTime.now();
        LocalDateTime time2 = LocalDateTime.now();
        tem.persist(event);
        tem.persist(user);

        // when
        participant.updateParticipant(event, user, 10, time1, time2, 300);
        tem.persistAndFlush(participant);

        // then
        assertEquals(participant.getQty(), 10);
    }

    private void dataSetting() {
        Event event = Event.builder().build();
        User user = User.builder().build();
        Participant participant = Participant.builder().event(event).user(user).qty(2).createdAt(LocalDateTime.now()).limitTime(LocalDateTime.now()).build();
        participantRepository.save(participant);
    }

    private void autoincrementReset() {
        em.createNativeQuery("ALTER TABLE participant ALTER COLUMN `id` RESTART WITH 1").executeUpdate();
    }
}
