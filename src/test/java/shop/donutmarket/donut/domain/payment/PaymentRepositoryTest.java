package shop.donutmarket.donut.domain.payment;

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
import shop.donutmarket.donut.domain.payment.model.Payment;
import shop.donutmarket.donut.domain.payment.repository.PaymentRepository;

import static org.junit.jupiter.api.Assertions.*;

@ActiveProfiles("test")
@DataJpaTest
public class PaymentRepositoryTest {
    
    @Autowired
    private PaymentRepository paymentRepository;

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
    @DisplayName("Payment 전체 조회 테스트")
    void findAll_Test() {
        // given

        // when
        List<Payment> payments = paymentRepository.findAll();

        // then
        assertEquals(payments.size(), 1);
    }

    @Test
    @DisplayName("Payment 개별 id조회 테스트")
    void findById_Test(){
        // given
        Long id = 1L;
        
        // when
        Optional<Payment> payment = paymentRepository.findById(id);

        // then
        payment.ifPresent(payment1 -> {
            assertNotNull(payment1);
            assertEquals(payment1.getId(), 1L);
        });
    }
    
    @Test
    @DisplayName("Payment 생성 테스트")
    void save_Test() {
        // given
        Payment payment = Payment.builder().paymentType("직거래").confirmed(true).createdAt(LocalDateTime.now()).build();

        // when
        paymentRepository.save(payment);

        // then
        assertNotNull(payment);
        assertEquals(payment.getId(), 2L);
    }

    @Test
    @DisplayName("Payment 삭제 테스트")
    void deleteById_Test(){
        // given
        Long id = 1L;
        Payment payment = tem.find(Payment.class, id);

        // when
        if (payment != null) {
            tem.remove(payment);
            tem.flush();
        }

        // then
        assertNull(tem.find(Payment.class, id));
    }

    private void dataSetting() {
        Payment payment = Payment.builder().id(1L).paymentType("직거래").confirmed(true).createdAt(LocalDateTime.now()).build();
        paymentRepository.save(payment);
    }

    private void autoincrementReset() {
        em.createNativeQuery("ALTER TABLE payment ALTER COLUMN `id` RESTART WITH 1").executeUpdate();
    }
}
