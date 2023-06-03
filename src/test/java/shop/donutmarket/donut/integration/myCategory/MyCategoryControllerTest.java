package shop.donutmarket.donut.integration.myCategory;

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
import shop.donutmarket.donut.domain.blacklist.repository.BlackListRepository;
import shop.donutmarket.donut.domain.myCategory.dto.MyCategoryReq;
import shop.donutmarket.donut.domain.myCategory.model.MyCategory;
import shop.donutmarket.donut.domain.myCategory.repository.MyCategoryRepository;
import shop.donutmarket.donut.domain.review.model.Rate;
import shop.donutmarket.donut.domain.review.repository.RateRepository;
import shop.donutmarket.donut.domain.user.model.User;
import shop.donutmarket.donut.domain.user.repository.UserRepository;
import shop.donutmarket.donut.global.dummy.DummyEntity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DisplayName("카테고리 API")
@AutoConfigureRestDocs(uriScheme = "http", uriHost = "localhost", uriPort = 8080)
@ActiveProfiles("test")
@Sql("classpath:db/teardown.sql")
@AutoConfigureMockMvc
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
public class MyCategoryControllerTest extends MyRestDocs {

    private DummyEntity dummy = new DummyEntity();

    @Autowired
    private MockMvc mvc;
    @Autowired
    private ObjectMapper om;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private MyCategoryRepository myCategoryRepository;
    @Autowired
    private CategoryRepository categoryRepository;
    @Autowired
    private RateRepository rateRepository;
    @Autowired
    private EntityManager em;

    @BeforeEach
    public void setUp() {
        Rate rate = Rate.builder().rateName("글레이즈드").createdAt(LocalDateTime.now()).build();
        rateRepository.save(rate);
        User user1 = userRepository.save(dummy.newUser("ssar@naver.com", rate));
        userRepository.save(dummy.newUser("cos@naver.com", rate));

        // 디폴트 카테고리 dummy 생성
        Category category1 = Category.builder().name("생활가전").createdAt(LocalDateTime.now()).build();
        Category category2 = Category.builder().name("스포츠").createdAt(LocalDateTime.now()).build();
        Category category3 = Category.builder().name("잡화").createdAt(LocalDateTime.now()).build();
        Category category4 = Category.builder().name("유아물품").createdAt(LocalDateTime.now()).build();
        Category category5 = Category.builder().name("의류").createdAt(LocalDateTime.now()).build();
        Category category6= Category.builder().name("식품").createdAt(LocalDateTime.now()).build();
        Category category7 = Category.builder().name("미용").createdAt(LocalDateTime.now()).build();
        Category category8 = Category.builder().name("반려동물 물품").createdAt(LocalDateTime.now()).build();
        Category category9 = Category.builder().name("식물").createdAt(LocalDateTime.now()).build();
        Category category10 = Category.builder().name("문구").createdAt(LocalDateTime.now()).build();
        Category category11 = Category.builder().name("티켓/교환권").createdAt(LocalDateTime.now()).build();
        Category category12 = Category.builder().name("편의점").createdAt(LocalDateTime.now()).build();
        Category category13 = Category.builder().name("홈쇼핑").createdAt(LocalDateTime.now()).build();
        Category category14 = Category.builder().name("도매상").createdAt(LocalDateTime.now()).build();

        categoryRepository.save(category1);
        categoryRepository.save(category2);
        categoryRepository.save(category3);
        categoryRepository.save(category4);
        categoryRepository.save(category5);
        categoryRepository.save(category6);
        categoryRepository.save(category7);
        categoryRepository.save(category8);
        categoryRepository.save(category9);
        categoryRepository.save(category10);
        categoryRepository.save(category11);
        categoryRepository.save(category12);
        categoryRepository.save(category13);
        categoryRepository.save(category14);

        MyCategory myCategory = MyCategory.builder().user(user1).category(category1).createdAt(LocalDateTime.now()).build();
        myCategoryRepository.save(myCategory);

        em.clear();
    }

    @AfterEach
    void clean() {
//        myCategoryRepository.deleteAll();
//        userRepository.deleteAll();
//        categoryRepository.deleteAll();
//        rateRepository.deleteAll();
    }


    @DisplayName("디폴트 카테고리")
    @WithUserDetails(value = "ssar@naver.com", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @Test
    public void default_test() throws Exception {
        // given

        // when
        ResultActions resultActions = mvc
                .perform(post("/myCategories/default").with(csrf()));

        String responseBody = resultActions.andReturn().getResponse().getContentAsString();
        System.out.println("테스트 : " + responseBody);

        // then
        resultActions.andExpect(jsonPath("$.data.list").isArray());
        resultActions.andExpect(jsonPath("$.data.list[0].name").value("생활가전"));
        resultActions.andExpect(status().isOk());
        resultActions.andDo(MockMvcResultHandlers.print()).andDo(document);
    }

    @DisplayName("카테고리 업데이트")
    @WithUserDetails(value = "ssar@naver.com", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @Test
    public void update_test() throws Exception {
        // given
        List<Long> list = new ArrayList<>();
        list.add(1L);
        list.add(2L);
        MyCategoryReq.MyCategoryUpdateReqDTO myCategoryUpdateReqDTO = new MyCategoryReq.MyCategoryUpdateReqDTO();
        myCategoryUpdateReqDTO.setCategoryId(list);
        String requestBody = om.writeValueAsString(myCategoryUpdateReqDTO);

        // when
        ResultActions resultActions = mvc
                .perform(post("/myCategories").content(requestBody).contentType(MediaType.APPLICATION_JSON).with(csrf()));

        String responseBody = resultActions.andReturn().getResponse().getContentAsString();
        System.out.println("테스트 : " + responseBody);

        // then
        resultActions.andExpect(jsonPath("$.data.list").isArray());
        resultActions.andExpect(jsonPath("$.data.list[0].name").value("생활가전"));
        resultActions.andExpect(status().isOk());
        resultActions.andDo(MockMvcResultHandlers.print()).andDo(document);
    }

    @DisplayName("나의 카테고리 보기")
    @WithUserDetails(value = "ssar@naver.com", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @Test
    public void select_test() throws Exception {
        // given

        // when
        ResultActions resultActions = mvc
                .perform(get("/myCategories"));

        String responseBody = resultActions.andReturn().getResponse().getContentAsString();
        System.out.println("테스트 : " + responseBody);

        // then
        resultActions.andExpect(jsonPath("$.data.list").isArray());
        resultActions.andExpect(jsonPath("$.data.list[0].user.email").value("ssar@naver.com"));
        resultActions.andExpect(jsonPath("$.data.list[0].category.name").value("생활가전"));
        resultActions.andExpect(status().isOk());
        resultActions.andDo(MockMvcResultHandlers.print()).andDo(document);
    }
}
