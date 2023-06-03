package shop.donutmarket.donut.integration.myLocation;

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
import shop.donutmarket.donut.domain.admin.repository.CategoryRepository;
import shop.donutmarket.donut.domain.myCategory.repository.MyCategoryRepository;
import shop.donutmarket.donut.domain.myLocation.dto.MyLocationReq;
import shop.donutmarket.donut.domain.myLocation.model.MyLocation;
import shop.donutmarket.donut.domain.myLocation.repository.MyLocationRepository;
import shop.donutmarket.donut.domain.review.model.Rate;
import shop.donutmarket.donut.domain.review.repository.RateRepository;
import shop.donutmarket.donut.domain.user.model.User;
import shop.donutmarket.donut.domain.user.repository.UserRepository;
import shop.donutmarket.donut.global.dummy.DummyEntity;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DisplayName("나의 지역 API")
@AutoConfigureRestDocs(uriScheme = "http", uriHost = "localhost", uriPort = 8080)
@ActiveProfiles("test")
@Sql("classpath:db/teardown.sql")
@AutoConfigureMockMvc
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
public class MyLocationControllerTest extends MyRestDocs {
    private DummyEntity dummy = new DummyEntity();

    @Autowired
    private MockMvc mvc;
    @Autowired
    private ObjectMapper om;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private MyLocationRepository myLocationRepository;
    @Autowired
    private RateRepository rateRepository;
    @Autowired
    private EntityManager em;

    @BeforeEach
    public void setUp() {
        // Unique보장하기 위함
        myLocationRepository.deleteAll();
        em.clear();

        Rate rate = Rate.builder().rateName("글레이즈드").createdAt(LocalDateTime.now()).build();
        rateRepository.save(rate);
        userRepository.save(dummy.newUser("ssar@naver.com", rate));
        userRepository.save(dummy.newUser("cos@naver.com", rate));

        // 내 지역 dummy 생성
        Optional<User> userOP = userRepository.findById(1L);
        User userPS = userOP.get();

        MyLocation myLocation = MyLocation.builder().user(userPS).state("창원시").city("성산구").town("남양동").build();
        myLocationRepository.save(myLocation);

        em.clear();
    }

    @AfterEach
    void clean() {
        myLocationRepository.deleteAll();
//        userRepository.deleteAll();
//        rateRepository.deleteAll();
    }


    @DisplayName("디폴트 지역")
    @WithUserDetails(value = "ssar@naver.com", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @Test
    public void default_test() throws Exception {
        // given

        // when
        ResultActions resultActions = mvc
                .perform(post("/myLocations/default").with(csrf()));

        String responseBody = resultActions.andReturn().getResponse().getContentAsString();
        System.out.println("테스트 : " + responseBody);

        // then
        resultActions.andExpect(jsonPath("$.data.state").value("부산광역시"));
        resultActions.andExpect(jsonPath("$.data.city").value("부산진구"));
        resultActions.andExpect(jsonPath("$.data.town").value("부전동"));
        resultActions.andExpect(status().isOk());
        resultActions.andDo(MockMvcResultHandlers.print()).andDo(document);
    }

    @DisplayName("지역 변경")
    @WithUserDetails(value = "ssar@naver.com", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @Test
    public void update_test() throws Exception {
        // given
        MyLocationReq.MyLocationSaveReqDTO myLocationSaveReqDTO = new MyLocationReq.MyLocationSaveReqDTO();
        myLocationSaveReqDTO.setState("부산광역시");
        myLocationSaveReqDTO.setCity("해운대구");
        myLocationSaveReqDTO.setTown("반송1동");
        String requestBody = om.writeValueAsString(myLocationSaveReqDTO);

        // when
        ResultActions resultActions = mvc
                .perform(put("/myLocations").content(requestBody).contentType(MediaType.APPLICATION_JSON).with(csrf()));

        String responseBody = resultActions.andReturn().getResponse().getContentAsString();
        System.out.println("테스트 : " + responseBody);

        // then
        resultActions.andExpect(jsonPath("$.data.state").value("부산광역시"));
        resultActions.andExpect(jsonPath("$.data.city").value("해운대구"));
        resultActions.andExpect(jsonPath("$.data.town").value("반송1동"));
        resultActions.andExpect(status().isOk());
        resultActions.andDo(MockMvcResultHandlers.print()).andDo(document);
    }

    @DisplayName("나의 지역 보기")
    @WithUserDetails(value = "ssar@naver.com", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @Test
    public void select_test() throws Exception {
        // given

        // when
        ResultActions resultActions = mvc
                .perform(get("/myLocations"));

        String responseBody = resultActions.andReturn().getResponse().getContentAsString();
        System.out.println("테스트 : " + responseBody);

        // then
        resultActions.andExpect(jsonPath("$.data.myLocation.state").value("창원시"));
        resultActions.andExpect(jsonPath("$.data.myLocation.city").value("성산구"));
        resultActions.andExpect(jsonPath("$.data.myLocation.town").value("남양동"));
        resultActions.andExpect(status().isOk());
        resultActions.andDo(MockMvcResultHandlers.print()).andDo(document);
    }
}
