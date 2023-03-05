package study.querydsl;

import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Commit;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;
import study.querydsl.entity.Hello;
import study.querydsl.entity.QHello;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest @Transactional @Commit // Rollback(value = false) 를 대신해서 쓸 수 있음.
class SpringQueryDslApplicationTests {

    @PersistenceContext EntityManager em;

    @Test
    void contextLoads() {
        Hello hello = new Hello();
        em.persist(hello);

        JPAQueryFactory query = new JPAQueryFactory(em);        // QueryDsl 을 사용하려면 JPAQueryFactory 가 필요함. 인자로 EntityManager 가 들어감
        // QHello qHello = new QHello("h");                 // 생성된 QHello 의 인스턴스를 만들기위해선 인자로 Alias 를 주어야함. h (방법 1)
        QHello qHello = QHello.hello;                           // 생성된 QHello 의 인스턴스를 만들기 위한 방법은 생성된 QHello 의 정적 팩토리 메서드를 이용하는는 것.(방법 2)

        Hello result = query                    // QueryDsl 사용 (리턴타입으로는 쿼리는 QClass 의 본체 타입을 받는다.)
                .selectFrom(qHello)             // QueryDsl 에서 selectFrom 을 사용할때, Query 와 관련된 QClass 를 인자로 넣어주어야 함.
                .fetchOne();

        assertThat(result).isEqualTo(hello);
        assertThat(result.getId()).isEqualTo(hello.getId());
    }

}
