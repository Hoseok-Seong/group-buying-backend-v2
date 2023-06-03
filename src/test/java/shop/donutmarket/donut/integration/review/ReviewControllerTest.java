package shop.donutmarket.donut.integration.review;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.test.context.support.TestExecutionEvent;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.transaction.annotation.Transactional;
import shop.donutmarket.donut.core.MyRestDocs;
import shop.donutmarket.donut.domain.account.dto.AccountReq;
import shop.donutmarket.donut.domain.account.repository.MyAccountRepository;
import shop.donutmarket.donut.domain.admin.model.Category;
import shop.donutmarket.donut.domain.admin.repository.CategoryRepository;
import shop.donutmarket.donut.domain.board.model.Board;
import shop.donutmarket.donut.domain.board.model.Event;
import shop.donutmarket.donut.domain.board.repository.BoardRepository;
import shop.donutmarket.donut.domain.board.repository.EventRepository;
import shop.donutmarket.donut.domain.participant.model.Participant;
import shop.donutmarket.donut.domain.participant.repository.ParticipantRepository;
import shop.donutmarket.donut.domain.review.dto.ReviewReq;
import shop.donutmarket.donut.domain.review.model.Rate;
import shop.donutmarket.donut.domain.review.repository.RateRepository;
import shop.donutmarket.donut.domain.review.repository.ReviewRepository;
import shop.donutmarket.donut.domain.user.model.User;
import shop.donutmarket.donut.domain.user.repository.UserRepository;
import shop.donutmarket.donut.global.dummy.DummyEntity;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DisplayName("리뷰 API")
@AutoConfigureRestDocs(uriScheme = "http", uriHost = "localhost", uriPort = 8080)
@ActiveProfiles("test")
@Sql("classpath:db/teardown.sql")
@AutoConfigureMockMvc
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
public class ReviewControllerTest extends MyRestDocs {
    private DummyEntity dummy = new DummyEntity();

    @Autowired
    private MockMvc mvc;
    @Autowired
    private ObjectMapper om;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private EventRepository eventRepository;
    @Autowired
    private ParticipantRepository participantRepository;
    @Autowired
    private CategoryRepository categoryRepository;
    @Autowired
    private BoardRepository boardRepository;
    @Autowired
    private RateRepository rateRepository;
    @Autowired
    private ReviewRepository reviewRepository;
    @Autowired
    private EntityManager em;

    @BeforeEach
    public void setUp() {
        // 등급 객체
        Rate rate = Rate.builder().rateName("글레이즈드").createdAt(LocalDateTime.now()).build();
        rateRepository.save(rate);

        User user1 = userRepository.save(dummy.newUser("ssar@naver.com", rate));
        User user2 = userRepository.save(dummy.newUser("cos@naver.com", rate));

        // 리뷰를 위한 dummy 생성

        // 이벤트 객체
        Event event = Event.builder().latitude(111.111).longtitude(222.222).qty(1).paymentType("직거래")
                .startAt(LocalDateTime.of(2023, 5, 1, 13, 30))
                .endAt(LocalDateTime.of(2023, 5, 2, 13, 30))
                .price(1000).createdAt(LocalDateTime.now()).build();
        eventRepository.save(event);

        // 참가자 객체
        Participant participant = Participant.builder().event(event).user(user2).statusCode(300)
                .limitTime(LocalDateTime.of(2023, 5, 1, 13, 30))
                .qty(1).createdAt(LocalDateTime.now()).build();
        participantRepository.save(participant);

        // 카테고리 객체
        Category category = Category.builder().name("생활가전").createdAt(LocalDateTime.now()).build();
        categoryRepository.save(category);

        // 게시글 객체
        Board board = Board.builder().category(category).event(event)
                .organizer(user1).title("제목1").content("내용1").img("사진").statusCode(200)
                .views(100).recommend(false).state("부산광역시").city("부산진구").town("부전동").build();

        boardRepository.save(board);

        em.clear();
    }

    @AfterEach
    void clean() {
//        userRepository.deleteAll();
//        eventRepository.deleteAll();
//        participantRepository.deleteAll();
//        categoryRepository.deleteAll();
//        boardRepository.deleteAll();
//        rateRepository.deleteAll();
//        reviewRepository.deleteAll();
    }

    @DisplayName("리뷰 작성")
    @WithUserDetails(value = "cos@naver.com", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @Test
    public void insert_test() throws Exception {
        // given
        ReviewReq.ReviewSaveReqDTO saveReqDTO = new ReviewReq.ReviewSaveReqDTO();
        saveReqDTO.setBoardId(1L);
        saveReqDTO.setReviewedUserId(1L);
        saveReqDTO.setScore(5);
        saveReqDTO.setComment("좋은 거래였습니다");
        String requestBody = om.writeValueAsString(saveReqDTO);

        // when
        ResultActions resultActions = mvc
                .perform(post("/reviews").content(requestBody).contentType(MediaType.APPLICATION_JSON).with(csrf()));

        String responseBody = resultActions.andReturn().getResponse().getContentAsString();
        System.out.println("테스트 : " + responseBody);

        // then
        resultActions.andExpect(jsonPath("$.data.review.reviewer.username").value("cos@naver.com"));
        resultActions.andExpect(jsonPath("$.data.review.reviewed.username").value("ssar@naver.com"));
        resultActions.andExpect(jsonPath("$.data.review.reviewed.ratePoint").value(21));
        resultActions.andExpect(jsonPath("$.data.review.score").value(5));
        resultActions.andExpect(status().isOk());
        resultActions.andDo(MockMvcResultHandlers.print()).andDo(document);
    }
}
