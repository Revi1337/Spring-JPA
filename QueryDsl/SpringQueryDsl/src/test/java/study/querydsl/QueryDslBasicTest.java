package study.querydsl;

import com.querydsl.core.QueryResults;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.jpa.repository.support.QuerydslJpaRepository;
import org.springframework.transaction.annotation.Transactional;
import study.querydsl.entity.Member;
import study.querydsl.entity.QMember;
import study.querydsl.entity.Team;

import java.util.List;

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

    @Test
    @DisplayName(value = "검색 쿼리 테스트")
    public void searchTest() {
        Member findMember = query
                .select(member)
                .from(member)                                                // select 와 from 의 인자가 같으면 selectFrom() 으로 바꿀 수 있음.
                .where(
                        member.username.eq("member1")                    // and() 나 or() 로 검색 조건을 걸 수 있고, 메서드 체인을 걸 수 있음.
                        .and(member.age.between(10, 30))
                )
                .fetchOne();
        assertThat(findMember.getUsername()).isEqualTo("member1");
    }

    @Test
    @DisplayName(value = "And 조건 검색 쿼리 테스트")
    public void searchAndParamTest() {
        Member findMember = query.select(member)
                .from(member)
                .where( // and 조건의 경우 는 굉장히 자주사용하기 때문에 .and() 로 체인하는 방법말고, 쉼표로 끊어서 조건을 줄 수 있다.
                        member.username.eq("member1"),
                        member.age.eq(10)
                )
                .fetchOne();
        assertThat(findMember.getUsername()).isEqualTo("member1");
    }
    
    @Test
    @DisplayName(value = "검색 결과 조회 테스트")
    public void resultFetchTest() {
//        Member fetchOne = query                         // 단건 결과 조회 (여기서는 BeforeEach 로 생성된 여러개의 데이터 때문에 NonUniqueResultException 터짐)
//                .selectFrom(member)
//                .fetchOne();

//        List<Member> fetch = query                      // 리스트 결과 조회
//                .selectFrom(member)
//                .fetch();

//        Member fetchFirst = query                       // limit(1).fetchOne() 과 동일함
//                .selectFrom(member)
//                .fetchFirst();
//        Member fetchLimitOne = query                    // fetchFirst() 와 동일함
//                .selectFrom(member)
//                .limit(1).fetchOne();
//
//        QueryResults<Member> results = query
//                .selectFrom(member)                     // 쿼리가 두방나감. 페이징용 count() 쿼리로 모든 데이터의 수를 갖고오는 쿼리와 (totalCount)
//                .fetchResults();                        // content 용 쿼리 를 갖고온다.
//        results.getTotal();
//        List<Member> content = results.getResults();

        long total = query                              // count() 용 쿼리만 나간다.
                .selectFrom(member)
                .fetchCount();
        assertThat(total).isEqualTo(4);
    }

    @Test
    @DisplayName(value = "정렬 테스트")
    public void sortTest() {
        em.persist(new Member(null, 100));
        em.persist(new Member("member5", 100));
        em.persist(new Member("member6", 100));

//         회원 정렬 순서
//         1. 회원 나이 내림차순
//         2. 회원 이름 오름차순
//         단, 2 에서 회원 이름이 없으면 마지막에 출력 (nulls last)
        List<Member> result = query
                .selectFrom(member)
                .where(member.age.eq(100))
                .orderBy(member.age.desc(), member.username.asc().nullsLast())
                .fetch();
        Member member5 = result.get(0);
        Member member6 = result.get(1);
        Member memberNull = result.get(2);
        assertThat(member5.getUsername()).isEqualTo("member5");
        assertThat(member6.getUsername()).isEqualTo("member6");
        assertThat(memberNull.getUsername()).isNull();
    }

    @Test
    @DisplayName(value = "페이징 테스트")
    public void pagingTest() {
        // ============== count() 쿼리가 나가지 않는 페이징 ==============
//        List<Member> results = query
//                .selectFrom(member)
//                .orderBy(member.username.desc())
//                .offset(1)                      // 끊어올 쿼리의 시작위치 (offset --> 0부터 시작임)
//                .limit(3)                       // offset 으로부터 몇개를 가져올 것인지
//                .fetch();
//        assertThat(results.size()).isEqualTo(3);

        // ============== count() 쿼리가 나가는 페이징 ==============
        // --> 이 방벙은 실무에서 쓸수있을 떄가 있고 없을떄가 있음. --> count() 쿼리를 분리해서 따로 작성해야하는 경우도 있다는 것임.
        // --> 페이징 쿼리가 단순하면 써도됨. 하지만, content 쿼리는 되게 복잡한데 count 쿼리는 단순하게 짤수있을때가 있음. (이런경우에는 content 쿼리와 count 쿼리를 따로 작성해야 함.)
        QueryResults<Member> results = query
                .selectFrom(member)
                .orderBy(member.username.desc())
                .offset(1)
                .limit(2)
                .fetchResults();
        assertThat(results.getTotal()).isEqualTo(4);            // 페이징전의 총 row 개수 (cont 쿼리가 얘때문에 나가는 것임.)
        assertThat(results.getOffset()).isEqualTo(1);           // 끊어올 쿼리의 시작위치 (offset --> 0부터 시작임)
        assertThat(results.getLimit()).isEqualTo(2);            // offset 으로부터 몇개를 가져올 것인지
        assertThat(results.getResults().size()).isEqualTo(2);   // 끊어온 row 들의 개수
    }
}
