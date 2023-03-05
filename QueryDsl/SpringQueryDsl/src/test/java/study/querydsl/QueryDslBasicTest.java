package study.querydsl;

import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import study.querydsl.entity.Member;
import study.querydsl.entity.QMember;
import study.querydsl.entity.Team;

import static org.assertj.core.api.Assertions.assertThat;
import static study.querydsl.entity.QMember.member;

@SpringBootTest @Transactional
public class QueryDslBasicTest {

    @PersistenceContext EntityManager em;

    JPAQueryFactory query;

    @BeforeEach
    @DisplayName(value = "개별 테스트 전 수행되는 메서드")
    public void before() {
        query = new JPAQueryFactory(em);
        Team teamA = new Team("teamA");
        Team teamB = new Team("teamB");
        em.persist(teamA);
        em.persist(teamB);

        Member member1 = new Member("member1", 10, teamA);
        Member member2 = new Member("member2", 20, teamA);
        Member member3 = new Member("member3", 30, teamB);
        Member member4 = new Member("member4", 40, teamB);
        em.persist(member1);
        em.persist(member2);
        em.persist(member3);
        em.persist(member4);
    }

    @Test
    @DisplayName(value = "JPQL 테스트")
    public void startJpqlTest() {
        Member findMember = em.createQuery("select m from Member as m where m.username = :username", Member.class)
                .setParameter("username", "member1")
                .getSingleResult();
        assertThat(findMember.getUsername()).isEqualTo("member1");
    }

    @Test
    @DisplayName(value = "QueryDSL 테스트")
    public void startQueryDSLTest() {
        // JPAQueryFactory query = new JPAQueryFactory(em);        // EntityManger 를 가지고 영속성컨텍스트에 접근하겠다. --> JPAQueryFactory 는 필드 레벨로 뺼 수 있다.
        // QMember member = new QMember("m");                      // QClass 를 사용하는 1 번째 방법 - 인스턴스 생성 (생성되는 JPQL 의 alias 가 m 임)
        // QMember member = QMember.member;                        // QClass 를 사용하는 2 번째 방법 - 기본 인스턴스를 사용 (생성되는 JPQL 의 alias 는 QClass 안에 명시된 "member1")
        Member findMember = query
                .select(member)                                     // QClass 를 사용하는 3번째 방법 - 기본 인스턴스를 스태틱 임포트 (생성되는 JPQL 의 alias 는 QClass 안에 명시된 "member1")
                .from(member)
                .where(member.username.eq("member1"))            // 파라미터 바인딩을 하지 않아도 됨. --> 내부적으로 JDBC 의 PreparedStatement 로 자동으로 파라미터 바인딩을 함.
                .fetchOne();
        assertThat(findMember.getUsername()).isEqualTo("member1");
    }
}
