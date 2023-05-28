package shop.donutmarket.donut.domain.report;

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
import shop.donutmarket.donut.domain.board.model.Board;
import shop.donutmarket.donut.domain.report.model.Report;
import shop.donutmarket.donut.domain.report.repository.ReportRepository;
import shop.donutmarket.donut.domain.user.model.User;

import static org.junit.jupiter.api.Assertions.*;

@ActiveProfiles("test")
@DataJpaTest
public class ReportRepositoryTest {
    
    @Autowired
    private ReportRepository reportRepository;

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
    @DisplayName("Report 전체 조회 테스트")
    void findAll_Test() {
        // given

        // when
        List<Report> reports = reportRepository.findAll();

        // then
        assertEquals(reports.size(), 1);
    }

    @Test
    @DisplayName("Report 개별 id조회 테스트")
    void findById_Test(){
        // given
        Long id = 1L;
        
        // when
        Optional<Report> report = reportRepository.findById(id);

        // then
        report.ifPresent(report1 -> {
            assertNotNull(report1);
            assertEquals(report1.getId(), 1L);
        });
    }
    
    @Test
    @DisplayName("Report 생성 테스트")
    void save_Test() {
        // given
        User user1 = User.builder().build();
        User user2 = User.builder().build();
        Report report = Report.builder().reporter(user1).reported(user2).title("신고").content("신고내용").reportType("일반").createdAt(LocalDateTime.now()).build();

        // when
        reportRepository.save(report);

        // then
        assertNotNull(report);
        assertEquals(report.getId(), 2L);
    }

    @Test
    @DisplayName("Report 삭제 테스트")
    void deleteById_Test(){
        // given
        Long id = 1L;
        Report report = tem.find(Report.class, id);

        // when
        if (report != null) {
            tem.remove(report);
            tem.flush();
        }

        // then
        assertNull(tem.find(Report.class, id));
    }
    @Test
    @DisplayName("Report 수정 테스트")
    void updateById_Test() {
        // given
        Long id = 1L;
        Report report = tem.find(Report.class, id);
        User user1 = User.builder().build();
        User user2 = User.builder().build();
        Board board = Board.builder().build();
        LocalDateTime time = LocalDateTime.now();
        tem.persist(user1);
        tem.persist(user2);
        tem.persist(board);

        // when
        report.updateReport(user1, user2, board, "제목", "내용", "사기", 600, time);
        tem.persistAndFlush(report);

        // then
        assertEquals(report.getReportType(), "사기");
    }

    private void dataSetting() {
        User user1 = User.builder().build();
        User user2 = User.builder().build();
        Report report = Report.builder().reporter(user1).reported(user2).title("신고").content("신고내용").reportType("일반").createdAt(LocalDateTime.now()).build();
        reportRepository.save(report);
    }

    private void autoincrementReset() {
        em.createNativeQuery("ALTER TABLE report ALTER COLUMN `id` RESTART WITH 1").executeUpdate();
    }
}
