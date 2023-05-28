package shop.donutmarket.donut.domain.mycategory;

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
import shop.donutmarket.donut.domain.account.model.MyAccount;
import shop.donutmarket.donut.domain.admin.model.Category;
import shop.donutmarket.donut.domain.board.model.Board;
import shop.donutmarket.donut.domain.myCategory.model.MyCategory;
import shop.donutmarket.donut.domain.myCategory.repository.MyCategoryRepository;
import shop.donutmarket.donut.domain.user.model.User;

import static org.junit.jupiter.api.Assertions.*;

@ActiveProfiles("test")
@DataJpaTest
public class MyCategoryRepositoryTest {
    
    @Autowired
    private MyCategoryRepository myCategoryRepository;

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
    @DisplayName("MyCategory 전체 조회 테스트")
    void findAll_Test() {
        // given

        // when
        List<MyCategory> myCategories = myCategoryRepository.findAll();

        // then
        assertEquals(myCategories.size(), 1);
    }
    
    @Test
    @DisplayName("MyCategory 개별 id조회 테스트")
    void findById_Test(){
        // given
        Long id = 1L;
        
        // when
        Optional<MyCategory> myCategory = myCategoryRepository.findById(id);

        // then
        myCategory.ifPresent(myCategory1 -> {
            assertNotNull(myCategory1);
            assertEquals(myCategory1.getId(), 1L);
        });
    }

    @Test
    @DisplayName("MyCategory 생성 테스트")
    void save_Test() {
        // given
        User user = User.builder().build();
        Category category = Category.builder().build();
        MyCategory myCategory = MyCategory.builder().user(user).category(category).build();
        myCategoryRepository.save(myCategory);

        // when
        myCategoryRepository.save(myCategory);

        // then
        assertNotNull(myCategoryRepository.findById(2L));
    }

    @Test
    @DisplayName("MyCategory 삭제 테스트")
    void deleteById_Test(){
        // given
        Long id = 1L;
        MyCategory myCategory = tem.find(MyCategory.class, id);

        // when
        if (myCategory != null) {
            tem.remove(myCategory);
            tem.flush();
        }

        // then
        assertNull(tem.find(MyCategory.class, id));
    }

    @Test
    @DisplayName("MyCategory 수정 테스트")
    void updateById_Test() {
        // given
        Long id = 1L;
        MyCategory myCategory = tem.find(MyCategory.class, id);
        Category category = Category.builder().name("도서").build();
        tem.persist(category);
        LocalDateTime time = LocalDateTime.now();

        // when
        myCategory.updateMyCategory(category, time);
        tem.persistAndFlush(myCategory);

        // then
        assertEquals(myCategory.getCategory().getName(), "도서");
    }

    private void dataSetting() {
        User user = User.builder().build();
        Category category = Category.builder().build();
        MyCategory myCategory = MyCategory.builder().user(user).category(category).build();
        myCategoryRepository.save(myCategory);
    }

    private void autoincrementReset() {
        em.createNativeQuery("ALTER TABLE my_category ALTER COLUMN `id` RESTART WITH 1").executeUpdate();
    }
}
