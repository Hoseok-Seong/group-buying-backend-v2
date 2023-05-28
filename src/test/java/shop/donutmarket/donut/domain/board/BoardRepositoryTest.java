package shop.donutmarket.donut.domain.board;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import jakarta.persistence.EntityManager;
import org.springframework.test.context.ActiveProfiles;
import shop.donutmarket.donut.domain.admin.model.Category;
import shop.donutmarket.donut.domain.board.model.Board;
import shop.donutmarket.donut.domain.board.model.Event;
import shop.donutmarket.donut.domain.board.model.Tag;
import shop.donutmarket.donut.domain.board.repository.BoardRepository;
import shop.donutmarket.donut.domain.board.repository.EventRepository;
import shop.donutmarket.donut.domain.board.repository.TagRepository;
import shop.donutmarket.donut.domain.user.model.User;

import static org.junit.jupiter.api.Assertions.*;

@ActiveProfiles("test")
@DataJpaTest
public class BoardRepositoryTest {

    @Autowired
    private BoardRepository boardRepository;

    @Autowired
    private TagRepository tagRepository;

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private TestEntityManager tem; // 테스트하기 위한 EntityManager

    @BeforeEach
    public void setUp() {
        autoincrementReset(); // autoincrement 보장해주는 메서드
        dataSetting(); // 초기 dummy 데이터 세팅
        tem.clear(); // 영속성 컨텍스트 비우기
    }

    @Test
    @DisplayName("Board 전체 조회 테스트")
    void findAll_Test() {
        // given

        // when
        List<Board> boards = boardRepository.findAll();

        // then
        assertEquals(boards.size(), 1);
    }

    @Test
    @DisplayName("Board 개별 id조회 테스트")
    void findById_Test() {
        // given
        Long id = 1L;

        // when
        Optional<Board> board = boardRepository.findById(id);

        // then
        board.ifPresent(board1 -> {
            assertNotNull(board1);
            assertEquals(board1.getId(), 1L);
        });
    }

    @Test
    @DisplayName("Board 생성 테스트")
    void save_Test() {
        // given
        User user = User.builder().build();
        Category category = Category.builder().build();
        Event event = Event.builder().build();
        Board board = Board.builder().category(category).title("삼각김밥 1+1 사실분").organizer(user).event(event).content("서면역 1번출구에서").build();

        // when
        boardRepository.save(board);

        // then
        assertNotNull(boardRepository.findById(2L));
    }

    @Test
    @DisplayName("Board 삭제 테스트")
    void deleteById_Test() {
        // given
        Long id = 1L;
        Board board = tem.find(Board.class, id);

        // when
        if (board != null) {
            tem.remove(board);
            tem.flush();
        }

        // then
        assertNull(tem.find(Board.class, id));
    }

    @Test
    @DisplayName("Board 수정 테스트")
    void updateById_Test() {
        // given
        Long id = 1L;
        Board board = tem.find(Board.class, id);

        User user = User.builder().build();
        tem.persist(user);
        Category category = Category.builder().build();
        tem.persist(category);
        Event event = Event.builder().build();
        tem.persist(event);
        LocalDateTime time = LocalDateTime.now();

        // when
        tem.persistAndFlush(board);

        // then
        assertEquals(board.getTitle(), "삼각김밥 1+1 사실분");
    }

    @Test
    @DisplayName("Tag 생성 테스트")
    void tag_Save_Test() {
        // given
        Long id = 1L;
        Board board = tem.find(Board.class, id);
        Tag tag = Tag.builder().boardId(board.getId()).comment("편의점").build();

        // when
        tagRepository.save(tag);

        // then
        assertNotNull(tag);
        assertEquals(tag.getId(), 1L);
    }


    @Test
    @DisplayName("Tag 삭제 테스트")
    void tag_DeleteById_Test() {
        // given
        Long id = 1L;
        Board board = tem.find(Board.class, id);
        Tag tag = Tag.builder().boardId(board.getId()).comment("편의점").build();
        tagRepository.save(tag);

        // when
        tem.remove(tag);
        tem.flush();

        // then
        assertNull(tem.find(Tag.class, id));
    }


    @Test
    @DisplayName("Event 생성 테스트")
    void event_Save_Test() {
        // given
        LocalDateTime time = LocalDateTime.now();
        Event event = Event.builder().latitude(139.123123).longtitude(39.123123).qty(2).paymentType("직거래").startAt(time).endAt(time).price(1000).build();

        // when
        eventRepository.save(event);

        // then
        assertNotNull(event);
        assertEquals(event.getLatitude(), 139.123123);
        assertEquals(event.getLongtitude(), 39.123123);
    }


    @Test
    @DisplayName("Event 삭제 테스트")
    void event_Delete_Test() {
        // given
        LocalDateTime time = LocalDateTime.now();
        Event event = Event.builder().latitude(139.123123).longtitude(39.123123).qty(2).paymentType("직거래").startAt(time).endAt(time).price(1000).build();
        eventRepository.save(event);

        // when
        tem.remove(event);
        tem.flush();

        // then
        assertNull(tem.find(Event.class, 1L));
    }

    private void dataSetting() {
        User user = User.builder().build();
        Category category = Category.builder().build();
        Event event = Event.builder().build();
        Board board = Board.builder().category(category).title("삼각김밥 1+1 사실분").organizer(user).event(event).content("서면역 1번출구에서").build();

        boardRepository.save(board);
    }

    private void autoincrementReset() {
        em.createNativeQuery("ALTER TABLE board ALTER COLUMN `id` RESTART WITH 1").executeUpdate();
    }

}
