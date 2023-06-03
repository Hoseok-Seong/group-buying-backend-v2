package shop.donutmarket.donut.integration.account;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityManager;
import org.assertj.core.api.Condition;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.TestExecutionEvent;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.transaction.annotation.Transactional;
import shop.donutmarket.donut.core.MyRestDocs;
import shop.donutmarket.donut.domain.account.dto.AccountReq;
import shop.donutmarket.donut.domain.account.dto.AccountResp;
import shop.donutmarket.donut.domain.account.repository.MyAccountRepository;
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

@DisplayName("계좌 API")
@AutoConfigureRestDocs(uriScheme = "http", uriHost = "localhost", uriPort = 8080)
@ActiveProfiles("test")
@Sql("classpath:db/teardown.sql")
@AutoConfigureMockMvc
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
public class AccountControllerTest extends MyRestDocs {

    private DummyEntity dummy = new DummyEntity();

    @Autowired
    private MockMvc mvc;
    @Autowired
    private ObjectMapper om;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private MyAccountRepository myAccountRepository;
    @Autowired
    private RateRepository rateRepository;
    @Autowired
    private EntityManager em;

    @BeforeEach
    public void setUp() {
        Rate rate = Rate.builder().rateName("글레이즈드").createdAt(LocalDateTime.now()).build();
        rateRepository.save(rate);
        userRepository.save(dummy.newUser("ssar@naver.com", rate));
        userRepository.save(dummy.newUser("cos@naver.com", rate));

        // 계좌 dummy 생성
        AccountReq.insertDTO insertDTO = new AccountReq.insertDTO();
        insertDTO.setBrand("하나");
        insertDTO.setAccountNumber("2223334444");
        Optional<User> userOP = userRepository.findById(1L);
        User userPS = userOP.get();
        myAccountRepository.save(insertDTO.toEntity(userPS));

        em.clear();
    }

    @AfterEach
    void clean() {
        myAccountRepository.deleteAll();
//        userRepository.deleteAll();
//        rateRepository.deleteAll();
    }


    @DisplayName("계좌 등록")
    @WithUserDetails(value = "cos@naver.com", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @Test
    public void insert_test() throws Exception {
        // given
        AccountReq.insertDTO insertDTO = new AccountReq.insertDTO();
        insertDTO.setBrand("신한");
        insertDTO.setAccountNumber("1112223333");
        String requestBody = om.writeValueAsString(insertDTO);

        // when
        ResultActions resultActions = mvc
                .perform(post("/accounts").content(requestBody).contentType(MediaType.APPLICATION_JSON).with(csrf()));

        String responseBody = resultActions.andReturn().getResponse().getContentAsString();
        System.out.println("테스트 : " + responseBody);

        // then
        resultActions.andExpect(jsonPath("$.data.userId").value(2L));
        resultActions.andExpect(jsonPath("$.data.brand").value("신한"));
        resultActions.andExpect(jsonPath("$.data.accountNumber").value("1112223333"));
        resultActions.andExpect(status().isOk());
        resultActions.andDo(MockMvcResultHandlers.print()).andDo(document);
    }

    @DisplayName("계좌 삭제")
    @WithUserDetails(value = "ssar@naver.com", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @Test
    public void delete_test() throws Exception {
        // given

        // when
        ResultActions resultActions = mvc
                .perform(delete("/accounts").with(csrf()));

        String responseBody = resultActions.andReturn().getResponse().getContentAsString();
        System.out.println("테스트 : " + responseBody);

        // then
        resultActions.andExpect(status().isOk());
        resultActions.andDo(MockMvcResultHandlers.print()).andDo(document);
    }

    @DisplayName("계좌 수정")
    @WithUserDetails(value = "ssar@naver.com", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @Test
    public void update_test() throws Exception {
        // given
        AccountReq.updateDTO updateDTO = new AccountReq.updateDTO();
        updateDTO.setBrand("농협");
        updateDTO.setAccountNumber("1112223333");
        String requestBody = om.writeValueAsString(updateDTO);

        // when
        ResultActions resultActions = mvc
                .perform(put("/accounts").content(requestBody).contentType(MediaType.APPLICATION_JSON).with(csrf()));

        String responseBody = resultActions.andReturn().getResponse().getContentAsString();
        System.out.println("테스트 : " + responseBody);

        // then
        resultActions.andExpect(jsonPath("$.data.userId").value(1L));
        resultActions.andExpect(jsonPath("$.data.brand").value("농협"));
        resultActions.andExpect(jsonPath("$.data.accountNumber").value("1112223333"));
        resultActions.andExpect(status().isOk());
        resultActions.andDo(MockMvcResultHandlers.print()).andDo(document);
    }

    @DisplayName("계좌 조회")
    @WithUserDetails(value = "ssar@naver.com", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @Test
    public void select_test() throws Exception {
        // given

        // when
        ResultActions resultActions = mvc
                .perform(get("/accounts"));

        String responseBody = resultActions.andReturn().getResponse().getContentAsString();
        System.out.println("테스트 : " + responseBody);

        // then
        resultActions.andExpect(jsonPath("$.data.userId").value(1L));
        resultActions.andExpect(jsonPath("$.data.brand").value("하나"));
        resultActions.andExpect(jsonPath("$.data.accountNumber").value("2223334444"));
        resultActions.andExpect(status().isOk());
        resultActions.andDo(MockMvcResultHandlers.print()).andDo(document);
    }
}
