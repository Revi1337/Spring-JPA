package study.querydsl;

import com.querydsl.core.QueryResults;
import com.querydsl.core.Tuple;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.PersistenceUnit;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import study.querydsl.entity.Member;
import study.querydsl.entity.Team;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static study.querydsl.entity.QMember.member;
import static study.querydsl.entity.QTeam.team;

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

//        List<Member> fetch = query                      // 리스트 결과 조회 (jpql 에서 getResultList 라 보면됨. (데이터 없으면 빈리스트 반환))
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
//                .selectFrom(member)                     // 페이징정보 포함 --> 쿼리가 두번나감 (Total Count + 진짜 조회)
//                .fetchResults();                        // content 용 쿼리 를 갖고온다.
//        results.getTotal();
//        List<Member> content = results.getResults();

        long total = query                                // count() 용 쿼리만 나간다. (@Deprecated 되었고, 문서에서 그냥 fetch() 에 size() 박으란다.)
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
    @DisplayName(value = "Paging 테스트 (1) - count() 쿼리 X")
    public void pagingTest1() {
        // ============== count() 쿼리가 나가지 않는 페이징 ==============
        List<Member> result = query
                .selectFrom(member)
                .orderBy(member.username.desc())
                .offset(1)                          // 끊어올 쿼리의 시작위치 (offset --> 0부터 시작임)
                .limit(2)                           // offset 으로부터 몇개를 가져올 것인지 (결국 LIMIT 1, 2 와 동일)
                .fetch();
        assertThat(result.size()).isEqualTo(2);
    }

    @Test
    @DisplayName(value = "Paging 테스트 (2) - count() 쿼리 O")
    public void pagingTest2() {
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

    @Test
    @DisplayName(value = "집합 테스트 (1) - select 절에서 다중타입 ")
    public void selectMultiTypeTest() {
        // select 에서 조회하는것인 단일타입이 아니라, 여러개의타입이면 리턴타입은 Tuple 임 --> 이유는 select 절에서 뽑아내려는 컬럼들의 타입이 다 다를수있기 때문.
        List<Tuple> result = query
                .select(
                        member.count(),
                        member.age.sum(),
                        member.age.avg(),
                        member.age.max(),
                        member.age.min())
                .from(member)
                .fetch();
        Tuple tuple = result.get(0);
        assertThat(tuple.get(member.count())).isEqualTo(4);     // Tuple 에 있는 값을 꺼낼떄는 select 절에 넣은 값과 똑같이 넣어주면 됨.
        assertThat(tuple.get(member.age.sum())).isEqualTo(100);
        assertThat(tuple.get(member.age.avg())).isEqualTo(25);
        assertThat(tuple.get(member.age.max())).isEqualTo(40);
        assertThat(tuple.get(member.age.min())).isEqualTo(10);
    }

    @Test
    @DisplayName(value = "집합 테스트 (2) - groupBy 사용 (Team 의 이름과 각 Team 의 평균 연령을 구해라)")
    public void groupingByTest() {
        List<Tuple> result = query
                .select(team.name, member.age.avg())        // select 절에서 단일타입이 아닌, 다중타입 --> 리턴타입 Tuple
                .from(member)
                .join(member.team, team)                    // Member 의 Team 와 Team 을 조인 (JPQL 조인과 동일함.)
                .groupBy(team.name)                         // Team 의 이름으로 그룹짐
                .fetch();
        Tuple teamA = result.get(0);
        Tuple teamB = result.get(1);
        assertThat(teamA.get(team.name)).isEqualTo("teamA");
        assertThat(teamA.get(member.age.avg())).isEqualTo(15);
        assertThat(teamB.get(team.name)).isEqualTo("teamB");
        assertThat(teamB.get(member.age.avg())).isEqualTo(35);
    }

    @Test
    @DisplayName(value = "Inner 조인 테스트 - [팀 A 에 소속된 모든 회원]")
    public void joinTest() {
//        List<Member> resultList = em.createQuery("select m from Member as m inner join m.team as t where t.name = 'teamA'", Member.class)
//                .getResultList();

        List<Member> result = query
                .selectFrom(member)
                .innerJoin(member.team, team)                               // 그냥 .join() 과 동일
                .where(team.name.eq("teamA"))
                .fetch();
        assertThat(result).extracting("username").containsExactly("member1", "member2");
    }

    @Test
    @DisplayName(value = "Left Join [팀 A 에 소속된 모든 회원]")
    public void leftJoin() {
//        List<Member> resultList = em.createQuery("select m from Member as m left outer join m.team as t where t.name='teamA'", Member.class)
//                .getResultList();

        List<Member> resultList = query
                .selectFrom(member)
                .leftJoin(member.team, team)
                //.on(team.name.eq("teamA"))
                .where(team.name.eq("teamA"))
                .fetch();
        for (Member member1 : resultList) {
            System.out.println("member1 = " + member1);
        }
    }

    @Test
    @DisplayName(value = "theta 조인 테스트 - [회원의 이름이 팀 이름과 같은 회원을 조회] --> (theta 조인은 연관관계가 아니여도 할수있는 조인)")
    public void thetaJoinTest() {
        em.persist(new Member("teamA"));
        em.persist(new Member("teamB"));
        em.persist(new Member("teamC"));

//        List<Member> resultList = em.createQuery("select m from Member as m, Team as t where m.username = t.name", Member.class)
//                .getResultList();

        List<Member> thetaResult = query
                .select(member)
                .from(member, team)                                 // theta 조인(막조인)은 from 절에 QClass 타입을 두개 나열하는 것임. (쉽게말해 모든 회원과 팀을 가져와서 조인하는 것임)
                .where(member.username.eq(team.name))
                .fetch();                                           // Member 테이블과 Team 테이블을 모두 Join 한 뒤, 그 Join 된 테이블에서 Member 의 이름과 Team 이름이 같은 결과를 갖고온 것임.
        assertThat(thetaResult).extracting("username").containsExactly("teamA", "teamB");
    }

    // TODO Join 에서 On 절은 두가지 역할로 사용
    // 1. 조인대상 필터링 (inner join 에는 효과가 없음. --> where 와 같음) (left join 에는 on 절에 and 절이 더해져서 조인할 대상을 필터링함)
    // 2. 연관관계가 없는 엔티티를 외부조인할때 사용. (주로 여기서 많이 쓰임)
    @Test
    @DisplayName(value = "JOIN + On 절 [조인대상 필터링] (회원과 팀을 조인하는데, 팀이름이 teamA 인 팀만 조인하고 회원은 모두 조회)")
    public void JoinOnFiltering() {
        // =================== JPQL 로 구현 ===================
        List<Object[]> resultList  = em.createQuery("select m, t from Member as m left outer join m.team as t on t.name = 'teamA'")
                .getResultList();
        for (Object[] objects : resultList) {
            for (Object object : objects)
                System.out.print("object = " + object + ", ");
            System.out.println();
        }

        // =================== QueryDsl 로 구현 (Left Join + On) ===================
        // left 조인이면 on 절로 조인하는 대상을 필터링할 수 있다.
        List<Tuple> resultList2 = query
                .select(member, team)
                .from(member)
                .leftJoin(member.team, team).on(team.name.eq("teamA"))
                .fetch();
        for (Tuple tuple : resultList2) {                           // member3, member4 는 조인하는 대상이 없(null)고 left join 이기 때문에 member3, member4 도 조회에 포함된다.
            System.out.println("tuple = " + tuple);
        }

        // =================== QueryDsl 로 작성했을 때  (inner)===================
        // on 절을 활용해 조인대상을 필터링할떄, 외부조인(left) 가 아닌 inner 조인이면 where 절에서 필터링하는것과 결과가 동일하기 떄문에 의미가 없다.
        // 따라서 on 절을 사용할 떄 inner join 을 사용하면 where 절로 해겨하고, 정말 left join 이 필요한 경우에만 on 을 사용하자.
        List<Tuple> resultList3 = query
                .select(member, team)
                .from(member)
                .innerJoin(member.team, team)
                //.on(team.name.eq("teamA"))                        // --> 이놈과
                .where(team.name.eq("teamA"))                  // --> 이놈이 똑같다는것임
                .fetch();
        for (Tuple tuple : resultList3) {                           // member3, member4 는 조인하는 대상이 없(null)고 inner join 이기 때문에 member3, member4 는 조회에서 빠지게된다.
            System.out.println("tuple = " + tuple);
        }
    }

    @Test
    @DisplayName(
            value = "JOIN + On 절 [연관관계가 없는 엔티티를 외부조인] (회원의 이름이 팀 이름과 같은 대상을 외부 조인)" +
                    "연관관계가 없는 엔티티를 외부조인 이란 --> PK 끼리조인하는 것이 아니고, PK 가 아닌 컬럼들고 조인하는 것을 의미함.")
    public void JoinOnWithNoRelationEntity() {
        em.persist(new Member("teamA"));
        em.persist(new Member("teamB"));
        em.persist(new Member("teamC"));

        // JPQL
        List<Object[]> resultList = em.createQuery("select m, t from Member as m left outer join Team as t on t.name = m.username")
                .getResultList();                                   // left outer join m.team 을 하면 On 절에 PK(id) 값으로 매칭하지만, 생으로 Team 을 박아넣으면 On 절에 지정한 Team 의 name 컬럼과 비교하게 된다.
        for (Object[] objects : resultList) {
            for (Object object : objects) {
                System.out.print("object = " + object + ", ");
            }
            System.out.println();
        }

        List<Tuple> fetch = query
                .select(member, team)
                .from(member)
                .leftJoin(team).on(member.username.eq(team.name))   // 문법을 주의해야하는데, leftJoin() 부분에 일반 조인과 다르게 엔티티 하나만 들어간다.
                .fetch();                                           // member.team 을 하면 On 절에 PK(id) 값으로 매칭시키지만, 생으로 team 을 박아넣으면 On 절에 지정한 team 의 name 컬럼과 비교하게 된다.
        for (Tuple tuple : fetch) {
            System.out.println("tuple = " + tuple);
        }

        // 보통 JPQL 이나 QueryDSL 둘다, .team 같이 . 이 들어가게되면 Join On 절에 PK(id) 이 들어가게 되어 Join 하는 대상이 PK(id) 로 매칭이된다.
        // 하지만, . 말고 직접 Team 으로 박아넣게되면 Join On 절에 PK(id) 가 아닌, 다른 컬럼이 들어가게 되어 Join 하는 대상이 다른컬럼으로 매칭이되는것임.
        // 따라서 연관관계가 없는 엔티티를 외부조인 시킬 수 있음.
        // 추가적으로 left join 이기 때문에 Join 하는 대상이 없어(null)도 null 인 row 도 조회된다. --> inner join 이면 다 빠짐.
    }


    @PersistenceUnit EntityManagerFactory emf;

    @Test
    @DisplayName(value = "Fetch Join 사용하기 이전")
    public void beforeFetchJoin() {
//        em.createQuery("select m from Member as m where m.username = 'member1'", Member.class)
//                .getSingleResult();
        em.flush();
        em.clear();
        Member findMember = query
                .selectFrom(member)
                .where(member.username.eq("member1"))
                .fetchOne();
        assertThat(emf.getPersistenceUnitUtil().isLoaded(findMember.getTeam())).isFalse();
    }

    @Test
    @DisplayName(value = "Fetch Join 사용 시")
    public void afterFetchJoin() {
//        Member singleResult = em.createQuery("select m from Member as m inner join fetch m.team where m.username = 'member1'", Member.class)
//                .getSingleResult();
        em.flush();
        em.clear();
        Member findMember = query
                .selectFrom(member)
                .join(member.team, team).fetchJoin()
                .where(member.username.eq("member1"))
                .fetchOne();
        assertThat(emf.getPersistenceUnitUtil().isLoaded(findMember.getTeam())).isTrue();
    }

}
