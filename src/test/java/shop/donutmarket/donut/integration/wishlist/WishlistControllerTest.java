package shop.donutmarket.donut.integration.wishlist;

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
import org.springframework.security.test.context.support.TestExecutionEvent;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.transaction.annotation.Transactional;
import shop.donutmarket.donut.core.MyRestDocs;
import shop.donutmarket.donut.domain.admin.model.Category;
import shop.donutmarket.donut.domain.admin.repository.CategoryRepository;
import shop.donutmarket.donut.domain.board.model.Board;
import shop.donutmarket.donut.domain.board.model.Event;
import shop.donutmarket.donut.domain.board.repository.BoardRepository;
import shop.donutmarket.donut.domain.board.repository.EventRepository;
import shop.donutmarket.donut.domain.participant.model.Participant;
import shop.donutmarket.donut.domain.participant.repository.ParticipantRepository;
import shop.donutmarket.donut.domain.report.dto.ReportReq;
import shop.donutmarket.donut.domain.review.model.Rate;
import shop.donutmarket.donut.domain.review.repository.RateRepository;
import shop.donutmarket.donut.domain.user.model.User;
import shop.donutmarket.donut.domain.user.repository.UserRepository;
import shop.donutmarket.donut.domain.wishlist.dto.WishlistReq;
import shop.donutmarket.donut.domain.wishlist.model.Wishlist;
import shop.donutmarket.donut.domain.wishlist.repository.WishlistRepository;
import shop.donutmarket.donut.global.dummy.DummyEntity;

import java.time.LocalDateTime;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DisplayName("관심목록 API")
@AutoConfigureRestDocs(uriScheme = "http", uriHost = "localhost", uriPort = 8080)
@ActiveProfiles("test")
@Sql("classpath:db/teardown.sql")
@AutoConfigureMockMvc
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
public class WishlistControllerTest extends MyRestDocs {
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
    private CategoryRepository categoryRepository;
    @Autowired
    private BoardRepository boardRepository;
    @Autowired
    private WishlistRepository wishlistRepository;
    @Autowired
    private RateRepository rateRepository;
    @Autowired
    private EntityManager em;

    @BeforeEach
    public void setUp() {
        Rate rate = Rate.builder().rateName("글레이즈드").createdAt(LocalDateTime.now()).build();
        rateRepository.save(rate);

        User user1 = userRepository.save(dummy.newUser("ssar@naver.com", rate));
        User user2 = userRepository.save(dummy.newUser("cos@naver.com", rate));

        // 관심목록을 위한 dummy 생성

        // 이벤트 객체
        Event event = Event.builder().latitude(111.111).longtitude(222.222).qty(1).paymentType("직거래")
                .startAt(LocalDateTime.of(2023, 5, 1, 13, 30))
                .endAt(LocalDateTime.of(2023, 5, 2, 13, 30))
                .price(1000).createdAt(LocalDateTime.now()).build();
        eventRepository.save(event);
        
        // 카테고리 객체
        Category category = Category.builder().name("생활가전").createdAt(LocalDateTime.now()).build();
        categoryRepository.save(category);

        // 게시글 객체
        Board board = Board.builder().category(category).event(event)
                .organizer(user1).title("제목1").content("내용1").img("사진").statusCode(200)
                .views(100).recommend(false).state("부산광역시").city("부산진구").town("부전동").build();

        boardRepository.save(board);

        // 게시글 객체
        Board board2 = Board.builder().category(category).event(event)
                .organizer(user1).title("제목1").content("내용1").img("사진").statusCode(200)
                .views(100).recommend(false).state("부산광역시").city("부산진구").town("부전동").build();

        boardRepository.save(board2);

        // 관심목록 객체
        Wishlist wishlist = Wishlist.builder().board(board2).user(user2).createdAt(LocalDateTime.now()).build();

        wishlistRepository.save(wishlist);
        
        em.clear();
    }

    @AfterEach
    void clean() {
//        userRepository.deleteAll();
//        eventRepository.deleteAll();
//        categoryRepository.deleteAll();
//        boardRepository.deleteAll();
//        wishlistRepository.deleteAll();
//        rateRepository.deleteAll();
    }

    @DisplayName("관심등록하기")
    @WithUserDetails(value = "cos@naver.com", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @Test
    public void save_test() throws Exception {
        // given
        WishlistReq.WishListSaveReqDTO wishListSaveReqDTO = new WishlistReq.WishListSaveReqDTO();
        wishListSaveReqDTO.setBoardId(1L);
        String requestBody = om.writeValueAsString(wishListSaveReqDTO);

        // when
        ResultActions resultActions = mvc
                .perform(post("/wishlist").content(requestBody).contentType(MediaType.APPLICATION_JSON).with(csrf()));

        String responseBody = resultActions.andReturn().getResponse().getContentAsString();
        System.out.println("테스트 : " + responseBody);

        // then
        resultActions.andExpect(jsonPath("$.data.wishlist.user.username").value("cos@naver.com"));
        resultActions.andExpect(jsonPath("$.data.wishlist.board.organizer.username").value("ssar@naver.com"));
        resultActions.andExpect(status().isOk());
        resultActions.andDo(MockMvcResultHandlers.print()).andDo(document);
    }

    @DisplayName("관심등록 취소하기")
    @WithUserDetails(value = "cos@naver.com", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @Test
    public void delete_test() throws Exception {
        // given
        WishlistReq.WishListDeleteReqDTO wishListDeleteReqDTO = new WishlistReq.WishListDeleteReqDTO();
        wishListDeleteReqDTO.setWishlistId(1L);
        String requestBody = om.writeValueAsString(wishListDeleteReqDTO);

        // when
        ResultActions resultActions = mvc
                .perform(delete("/wishlist").content(requestBody).contentType(MediaType.APPLICATION_JSON));

        String responseBody = resultActions.andReturn().getResponse().getContentAsString();
        System.out.println("테스트 : " + responseBody);

        // then
        resultActions.andExpect(status().isOk());
        resultActions.andDo(MockMvcResultHandlers.print()).andDo(document);
    }

    @DisplayName("나의 관심목록 보기")
    @WithUserDetails(value = "cos@naver.com", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @Test
    public void wishlist_test() throws Exception {
        // given

        // when
        ResultActions resultActions = mvc
                .perform(get("/wishlist"));

        String responseBody = resultActions.andReturn().getResponse().getContentAsString();
        System.out.println("테스트 : " + responseBody);

        // then
        resultActions.andExpect(jsonPath("$.data.wishlistList[0].user.username").value("cos@naver.com"));
        resultActions.andExpect(jsonPath("$.data.wishlistList[0].board.organizer.username").value("ssar@naver.com"));
        resultActions.andExpect(status().isOk());
        resultActions.andDo(MockMvcResultHandlers.print()).andDo(document);
    }

}
