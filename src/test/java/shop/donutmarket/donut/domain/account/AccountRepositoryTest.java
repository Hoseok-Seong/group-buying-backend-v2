package shop.donutmarket.donut.domain.account;

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
import shop.donutmarket.donut.domain.account.repository.MyAccountRepository;
import shop.donutmarket.donut.domain.board.model.Board;
import shop.donutmarket.donut.domain.user.model.User;

import static org.junit.jupiter.api.Assertions.*;

@ActiveProfiles("test")
@DataJpaTest
public class AccountRepositoryTest {
    
    @Autowired
    private MyAccountRepository myAccountRepository;

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
    @DisplayName("Account 전체 조회 테스트")
    void findAll_Test() {
        // given

        // when
        List<MyAccount> myAccounts = myAccountRepository.findAll();

        // then
        assertEquals(myAccounts.size(), 1);
    }

    @Test
    @DisplayName("Account 개별 id조회 테스트")
    void findById_Test(){
        // given
        Long id = 1L;

        // when
        Optional<MyAccount> myAccount = myAccountRepository.findById(id);

        // then
        myAccount.ifPresent(myAccount1 -> {
            assertNotNull(myAccount1);
            assertEquals(myAccount1.getId(), 1L);
            assertEquals(myAccount1.getBrand(), "신한");
        });
    }

    @Test
    @DisplayName("Account 생성 테스트")
    void save_Test(){
        // given
        User user = User.builder().build();
        MyAccount myAccount = MyAccount.builder().user(user).brand("농협").accountNumber("1-2312-3-123456").build();
        
        // when
        myAccountRepository.save(myAccount);
        
        // then
        assertNotNull(myAccount);
        assertEquals(myAccount.getId(), 2L);
        assertEquals(myAccount.getBrand(), "농협");
    }

    @Test
    @DisplayName("Account 삭제 테스트")
    void deleteById_Test(){
        // given
        Long id = 1L;
        MyAccount myAccount = tem.find(MyAccount.class, id);

        // when
        if (myAccount != null) {
            tem.remove(myAccount);
            tem.flush();
        }

        // then
        assertNull(tem.find(MyAccount.class, id));
    }

    @Test
    @DisplayName("Account 수정 테스트")
    void updateById_Test() {
        // given
        Long id = 1L;
        MyAccount myAccount = tem.find(MyAccount.class, id);

        // when
        myAccount.updateMyAccount("경남은행", "111-111-111111");
        tem.persistAndFlush(myAccount);

        // then
        assertEquals(myAccount.getBrand(), "경남은행");
        assertEquals(myAccount.getAccountNumber(), "111-111-111111");
    }

    private void dataSetting() {
        User user = User.builder().build();
        MyAccount myAccount = MyAccount.builder().user(user).brand("신한").accountNumber("123-123-123456").build();
        myAccountRepository.save(myAccount);
    }

    private void autoincrementReset() {
        em.createNativeQuery("ALTER TABLE my_account ALTER COLUMN `id` RESTART WITH 1").executeUpdate();
    }
}
