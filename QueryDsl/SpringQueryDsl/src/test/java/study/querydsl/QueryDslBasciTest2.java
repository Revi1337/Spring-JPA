package study.querydsl;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.QueryResults;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.CaseBuilder;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Commit;
import org.springframework.transaction.annotation.Transactional;
import study.querydsl.dto.MemberDto;
import study.querydsl.dto.QMemberDto;
import study.querydsl.dto.UserDto;
import study.querydsl.entity.Member;
import study.querydsl.entity.QMember;
import study.querydsl.entity.Team;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static study.querydsl.entity.QMember.*;
import static study.querydsl.entity.QTeam.team;

@SpringBootTest @Transactional
public class QueryDslBasciTest2 {

    @PersistenceContext EntityManager em;

    JPAQueryFactory query;

    @BeforeEach
    public void beforeEach() {
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
    public void startJPQL() {
        Member findMember = em.createQuery("select m from Member as m where m.username = :username", Member.class)
                .setParameter("username", "member1")
                .getSingleResult();
        assertThat(findMember.getUsername()).isEqualTo("member1");
    }


    @Test
    public void startQueryDsl() {
        new JPAQueryFactory(em);
        Member findMember = query
                .select(member)
                .from(member)
                .where(member.username.eq("member1"))
                .fetchOne();
        assertThat(findMember.getUsername()).isEqualTo("member1");
    }


    @Test
    public void search() {
        Member findMember = query
                .selectFrom(member)
                .where(member.username.eq("member1").and(member.age.eq(10)))
                .fetchOne();
        assertThat(findMember.getUsername()).isEqualTo("member1");

        Member findMember2 = query
                .selectFrom(member)
                .where(member.username.eq("member1")
                        .and(member.age.between(10, 30)))
                .fetchOne();
        assertThat(findMember2.getUsername()).isEqualTo("member1");
    }


    @Test
    public void searchAndParam() {
        Member findMember = query
                .selectFrom(member)
                .where(
                        member.username.eq("member1"),
                        member.age.between(10, 30)
                )
                .fetchOne();
        assertThat(findMember.getUsername()).isEqualTo("member1");
    }


    @Test
    public void resultFetch() {
//      List<Member> resultList = query                             //TODO jpql 에서 getResultList 라 보면됨. (데이터 없으면 빈리스트 반환)
//                .selectFrom(member)
//                .fetch();

//        Member singleResult = query                               //TODO 없으면 null, 2개 이상이면 NonUniqueResultException.
//                .selectFrom(member)
//                .fetchOne();

//        Member limitFirstAndFetchOne = query                        //TODO  .limit(1).fetchOne()
//                .selectFrom(member)
//                .fetchFirst();

//        QueryResults<Member> results = query                        //TODO 페이징정보 포함 --> 쿼리가 두번나감 (Total Count + 진짜 조회)
//                .selectFrom(member)
//                .fetchResults();
//        long total = results.getTotal();                            // total count
//        List<Member> contents = results.getResults();               // 진짜 컨텐츠들 (결과들)
//        System.out.println("total = " + total);
//        System.out.println("contents = " + contents);

        long total2 = query
                .selectFrom(member)                                 //TODO total count 쿼리만 갖고오는것임(Depr 되었고, 문서에서 그냥 fetch() 에 size() 박으란다.)
                .fetchCount();
        System.out.println(total2);
    }


    @Test
    public void sort() {
        em.persist(new Member(null, 100));
        em.persist(new Member("member5", 100));
        em.persist(new Member("member6", 100));

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
    public void paging1() {
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
    public void paging2() {
        // ============== count() 쿼리가 나가는 페이징 ==============
        QueryResults<Member> result = query
                .selectFrom(member)
                .orderBy(member.username.desc())
                .offset(1)
                .limit(2)
                .fetchResults();
        assertThat(result.getTotal()).isEqualTo(4);
        assertThat(result.getLimit()).isEqualTo(2);
        assertThat(result.getOffset()).isEqualTo(1);
        assertThat(result.getResults().size()).isEqualTo(2);
    }

    @Test
    public void aggregation() {
        List<Tuple> result = query           // select 에서 조회하는것인 단일타입이 아니라, 여러개의타입이면 리턴타입은 Tuple 임 --> 이유는 select 절에서 뽑아내려는 컬럼들의 타입이 다 다를수있기 때문.
                .select(
                        member.count(),
                        member.age.sum(),
                        member.age.avg(),
                        member.age.max(),
                        member.age.min())
                .from(member)
                .fetch();
        Tuple tuple = result.get(0);
        assertThat(tuple.get(member.count())).isEqualTo(4);
        assertThat(tuple.get(member.age.sum())).isEqualTo(100);
        assertThat(tuple.get(member.age.avg())).isEqualTo(25);
        assertThat(tuple.get(member.age.max())).isEqualTo(40);
        assertThat(tuple.get(member.age.min())).isEqualTo(10);
        System.out.println("tuple = " + tuple);
    }

    @Test
    @DisplayName(value = "팀의 이름과 팀의 평균 연령을 구해라")
    public void groupBy() {
        List<Tuple> result = query
                .select(team.name, member.age.avg())
                .from(member)
                .join(member.team, team)
                .groupBy(team.name)
                .fetch();
        Tuple teamA = result.get(0);
        Tuple teamB = result.get(1);
        assertThat(teamA.get(team.name)).isEqualTo("teamA");
        assertThat(teamA.get(member.age.avg())).isEqualTo(15);

        assertThat(teamB.get(team.name)).isEqualTo("teamB");
        assertThat(teamB.get(member.age.avg())).isEqualTo(35);
    }

    @Test
    @DisplayName(value = "Inner Join [팀 A 에 소속된 모든 회원]")
    public void innerJoin() {
//        List<Member> resultList = em.createQuery("select m from Member as m inner join m.team as t where t.name = 'teamA'", Member.class)
//                .getResultList();

        List<Member> findMembers = query
                .selectFrom(member)
                .innerJoin(member.team, team)
                .where(team.name.eq("teamA"))
                .fetch();
        assertThat(findMembers.size()).isEqualTo(2);
        assertThat(findMembers).extracting("username").containsExactly("member1", "member2");
        for (Member findMember : findMembers) {
            System.out.println("findMember = " + findMember);
        }
    }

    @Test
    @DisplayName(value = "Left Join [팀 A 에 소속된 모든 회원]")
    public void leftJoin() {
//        List<Member> resultList = em.createQuery("select m from Member as m left outer join m.team as t where t.name='teamA'", Member.class)
//                .getResultList();

        List<Member> resultList = query
                .selectFrom(member)
                .leftJoin(member.team, team)
//                .on(team.name.eq("teamA"))
                .where(team.name.eq("teamA"))
                .fetch();
        for (Member member1 : resultList) {
            System.out.println("member1 = " + member1);
        }

    }

    @Test
    @DisplayName(value = "theta Join [회원의 이름이 팀 이름과 같은 회원을 조회] ")
    public void thetaJoin() {
        em.persist(new Member("teamA"));
        em.persist(new Member("teamB"));
        em.persist(new Member("teamC"));

//        List<Member> resultList = em.createQuery("select m from Member as m, Team as t where m.username = t.name", Member.class)
//                .getResultList();

        List<Member> thetaResult = query
                .select(member)
                .from(member, team) // theta 조인(막조인)은 from 절에 QClass 타입을 두개 나열하는 것임. (쉽게말해 모든 회원과 팀을 가져와서 조인하는 것임)
                .where(member.username.eq(team.name))
                .fetch();           // Member 테이블과 Team 테이블을 모두 Join 한 뒤, 그 Join 된 테이블에서 Member 의 이름과 Team 이름이 같은 결과를 갖고온 것임.
        assertThat(thetaResult).extracting("username").containsExactly("teamA", "teamB");
    }

    // TODO Join 에서 On 절은 두가지 역할로 사용
    // 1. 조인대상 필터링 (inner join 에는 효과가 없음. --> where 와 같음) (left join 에는 on 절에 and 절이 더해져서 조인할 대상을 필터링함)
    // 2. 연관관계가 없는 엔티티를 외부조인할때 사용. (주로 여기서 많이 쓰임)
    @Test
    @DisplayName(value = "JOIN + On 절 [조인대상 필터링] (회원과 팀을 조인하는데, 팀이름이 teamA 인 팀만 조인하고 회원은 모두 조회)")
    public void JoinOnFiltering() {
        // =================== JPQL 로 구현 ===================
//        List<Object[]> resultList  = em.createQuery("select m, t from Member as m left outer join m.team as t on t.name = 'teamA'")
//                .getResultList();
//        for (Object[] objects : resultList) {
//            for (Object object : objects)
//                System.out.print("object = " + object + ", ");
//            System.out.println();
//        }

        // =================== QueryDsl 로 구현 (Left Join + On) ===================
        // left 조인이면 on 절로 조인하는 대상을 필터링할 수 있다.
        List<Tuple> resultList = query
                .select(member, team)
                .from(member)
                .leftJoin(member.team, team).on(team.name.eq("teamA"))
                .fetch();
        for (Tuple tuple : resultList) { // member3, member4 는 조인하는 대상이 없(null)고 left join 이기 때문에 member3, member4 도 조회에 포함된다.
            System.out.println("tuple = " + tuple);
        }

        // =================== QueryDsl 로 작성했을 때  (inner)===================
        // on 절을 활용해 조인대상을 필터링할떄, 외부조인(left) 가 아닌 inner 조인이면 where 절에서 필터링하는것과 결과가 동일하기 떄문에 의미가 없다.
        // 따라서 on 절을 사용할 떄 inner join 을 사용하면 where 절로 해겨하고, 정말 left join 이 필요한 경우에만 on 을 사용하자.
//        List<Tuple> resultList = query
//                .select(member, team)
//                .from(member)
//                .innerJoin(member.team, team)
//                .on(team.name.eq("teamA"))                               // --> 이놈과
////                .where(team.name.eq("teamA"))                          // --> 이놈이 똑같다는것임
//                .fetch();
//        for (Tuple tuple : resultList) { // member3, member4 는 조인하는 대상이 없(null)고 inner join 이기 때문에 member3, member4 는 조회에서 빠지게된다.
//            System.out.println("tuple = " + tuple);
//        }
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
                .getResultList();                                   // left outer join m.team 을 하면 On 절에 PK(id) 값으로 매칭하지만, 생으로 Team 을 박아넣으면 On 절에 Team 의 name 컬럼과 매칭시킬 수 있다.
        for (Object[] objects : resultList) {
            for (Object object : objects) {
                System.out.print("object = " + object + ", ");
            }
            System.out.println();
        }

        // QueryDsl
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

    @Test
    @DisplayName(value = "서브쿼리 - 나이가 가장 많은 회원을 조회 (메인쿼리의 member 와 서브쿼리의 member 의 Alias 가 겹치면안되기 때문에, 서브쿼리의 alias 를 생성)")
    public void subQuery1() {
        QMember memberSub = new QMember("memberSub");
        List<Member> result = query
                .selectFrom(member)
                .where(member.age.eq(
                        JPAExpressions.select(memberSub.age.min())
                                .from(memberSub)))
                .fetch();
        assertThat(result).extracting("age").containsExactly(10);
    }

    @Test
    @DisplayName(value = "서브쿼리 - 나이가 평균 이상인 회원 (메인쿼리의 member 와 서브쿼리의 member 의 Alias 가 겹치면안되기 때문에, 서브쿼리의 alias 를 생성)")
    public void subQuery2() {
        QMember memberSub = new QMember("memberSub");
        List<Member> result = query
                .selectFrom(member)
                .where(member.age.gt(
                        JPAExpressions.select(memberSub.age.avg())
                                .from(memberSub)))
                .fetch();
        assertThat(result.size()).isEqualTo(2);
        assertThat(result).extracting("age").containsExactly(30, 40);
    }

    @Test
    @DisplayName(value = "서브쿼리 - (select 절에도 서브쿼리가 가능하다 --> 하지만 from 절의 서브쿼리는 불가능하다.)")
    public void subQuery3() {
        QMember memberSub = new QMember("memberSub");
        List<Tuple> fetch = query
                .select(member.username,
                        JPAExpressions.select(memberSub.age.avg())
                                .from(memberSub))
                .from(member)
                .fetch();
        for (Tuple tuple : fetch) {
            System.out.println("tuple = " + tuple);
        }

//        from 절의 서브쿼리 한계
//        JPA JPQL 서브쿼리의 한계점으로 from 절의 서브쿼리(인라인 뷰)는 지원하지 않는다. 당연히 Querydsl
//        도 지원하지 않는다. 하이버네이트 구현체를 사용하면 select 절의 서브쿼리는 지원한다. Querydsl도
//        하이버네이트 구현체를 사용하면 select 절의 서브쿼리를 지원한다.
//                -- from 절의 서브쿼리 해결방안 --
//        1. 서브쿼리를 join 으로 변경한다. (가능한 상황도 있고, 불가능한 상황도 있다.)
//        2. 애플리케이션에서 쿼리를 2번 분리해서 실행한다.
//        3. nativeSQL 을 사용한다
    }


    @Test
    @DisplayName(value = "CASE 문 사용 (Simple Case)")
    public void basicCase1() {
        List<String> result = query
                .select(member.age
                        .when(10).then("열살")
                        .when(20).then("스무살")
                        .otherwise("기타"))
                .from(member)
                .fetch();
        for (String s : result) {
            System.out.println("s = " + s);
        }
    }

    @Test
    @DisplayName(value = "CASE 문 사용 (Complex Case) --> CaseBuilder 를 사용")
    public void basicCase2() {
        List<String> result = query
                .select(new CaseBuilder()
                        .when(member.age.between(0, 20)).then("0~20살")
                        .when(member.age.between(21, 30)).then("21~30살")
                        .otherwise("기타"))
                .from(member)
                .fetch();
        for (String s : result) {
            System.out.println("s = " + s);
        }
    }

    @Test
    @DisplayName(value = "상수 문자 더하기 (1)")
    public void constant1() {
        List<Tuple> result = query
                .select(member.username, Expressions.constant("A"))
                .from(member)
                .fetch();
        for (Tuple tuple : result) {
            System.out.println("tuple = " + tuple);
        }
    }

    @Test
    @DisplayName(value = "상수 문자 더하기 (2) --> stringValue() 가 중요한데, 문자가 아닌 타입들을 문자로 변환할 수 있다. 이방법은 ENUM 을 처리할때도 자주 사용한다.")
    public void constant2() {
        // {username}_{age}
        List<String> result = query
                .select(member.username.concat("_").concat(member.age.stringValue()))
                .from(member)
                .where(member.username.eq("member1"))
                .fetch();
        for (String s : result) {
            System.out.println("s = " + s);
        }
    }

    @Test
    @DisplayName(value = "프로젝션과 결과 반환 (프로젝션 대상이 하나 --> select 절에 단일 타입 --> 타입을 명확하게 지정 가능)")
    public void projection1() {
        // 프로젝션은 보통 select 절에 무엇을 가져올지 대상을 지정하는것을 의미한다.
        List<String> fetch = query
                .select(member.username)
                .from(member)
                .fetch();
        for (String s : fetch) {
            System.out.println("s = " + s);
        }
    }

    @Test
    @DisplayName(value = "프로젝션과 결과 반환 (프로젝션이 둘 이상 --> 튜플로 반환.)")
    public void projection2() {
        List<Tuple> result = query
                .select(member.username, member.age)
                .from(member)
                .fetch();
        for (Tuple tuple : result) {                                            // Tuple 을 출력할때는 그냥 이터로 돌려도 되고
            System.out.println("tuple = " + tuple);
        }

        for (Tuple tuple : result) {
            String username = tuple.get(member.username);                       // 프로젝션에 넣었던 값 그대로 꺼낼 수 있다.
            Integer age = tuple.get(member.age);
            System.out.print("username = " + username + ", ");
            System.out.println("age = " + age);
        }
    }

    @Test
    @DisplayName(value = "프로젝션과 결과 반환 (프로젝션이 둘 이상 --> DTO 로 반환. (순수 JPQL 구현))")
    public void projectionPureJPQLDTO() {
        // 순수 JPQL 구현 (생성자 방식만 지원)
        List<MemberDto> result = em.createQuery(
                        "select new study.querydsl.dto.MemberDto(m.username, m.age) " +
                                "from Member m", MemberDto.class)
                .getResultList();
    }

    // QueryDsl 에서 결과를 DTO 로 반환할때는 3가지 방법을 지원
//    1. 프로퍼티 접근
//    2. 필드 직접 접근
//    3. 생성자 사용

    @Test
    @DisplayName(value = "프로젝션과 결과 반환 (프로젝션이 둘 이상 --> DTO 로 반환. --> 1. QueryDsl 에서 DTO 의 프로퍼티(세터) 로 결과 조회")
    public void queryDslProjectionDTOBySetter() {
        // DTO 에 기본생성자, 셋터가 꼭필요함. --> QueryDsl 이 기본생성자로 인스턴스를 만들고 setter 를 호출하기 때문
        List<MemberDto> result = query
                .select(Projections.bean(MemberDto.class,
                        member.username,
                        member.age))
                .from(member)
                .fetch();
    }

    @Test
    @DisplayName(value = "프로젝션과 결과 반환 (프로젝션이 둘 이상 --> DTO 로 반환. --> 2. QueryDsl 에서 DTO 의 필드에 직접 접근하여 결과 조회")
    public void queryDslProjectionDTOByField() {
        // DTO 에 기본생성자는 필요 --> 겟터, 셋터 없어도 필드에다가 다이렉트로 넣어줌.
        List<MemberDto> result = query
                .select(Projections.fields(MemberDto.class,
                        member.username,
                        member.age))
                .from(member)
                .fetch();
    }

    @Test
    @DisplayName(value = "프로젝션과 결과 반환 (프로젝션이 둘 이상 --> DTO 로 반환. --> 3. QueryDsl 에서 DTO 의 생성자에 직접 접근하여 결과 조회")
    public void queryDslProjectionDTOByConstructor() {
        // 순수 JPQL 로 DTO 를 받을때처럼 DTO 에 username 와 age 필드만 들어가는 생성자가 필요 --> 기본생성자가 없어도 됨.
        List<MemberDto> result = query
                .select(Projections.constructor(MemberDto.class,
                        member.username,
                        member.age))
                .from(member)
                .fetch();
    }

    @Test
    @DisplayName(value = "프로젝션과 결과 반환 (프로젝션이 둘 이상 --> DTO 로 반환 --> DTO 의 필드와, 프로젝션의 Alias 가 맞지 않으면 안됨")
    public void projectionIssueAlias1() {
        // UserDto 의 필드이름은 name, 하지만, 프로젝션에서는 username 을 사용중 --> Alias 가 맞지않음 -->
        // 익셉션이 터지지는 않고, UserDto 의 name 필드에 null 이 들어가게된다.
        // TODO 버전이 올라가면서 DTO 의 age 필드가 프로젝션의 age 와 동일하기때문에, 나머지 username 프로젝션은 DTO 의 필드와 동일하지 않아도 알아서 채워주는 것 같다.
        List<UserDto> issue = query
                .select(Projections.constructor(UserDto.class,
                        member.username,
                        member.age))
                .from(member)
                .fetch();
        for (UserDto userDto : issue) {
            System.out.println("userDto = " + userDto);
        }


        // TODO 정석적인 해결방법으로는 .as() 를 붙여주어 DTO 의 필드명과, 프로젝션의 alias 를 동일하게 매칭시켜주어야한다.
        List<UserDto> solution = query
                .select(Projections.constructor(UserDto.class,
                        member.username.as("name"),
                        member.age))
                .from(member)
                .fetch();
        for (UserDto userDto : solution) {
            System.out.println("userDto = " + userDto);
        }

    }

    @Test
    @DisplayName(value = "프로젝션과 결과 반환 (프로젝션이 둘 이상 --> DTO 로 반환 --> DTO 의 생성자에 @QueryProjection 사용")
    public void queryProjection() {
        // ㅋㅋ 아 개어이없네. ㅋㅋㅋㅋ 반환받을 DTO 에 @QueryProjection 를 설정해주면 Q 클래스를 만들어준다.
        // 생성된 Q 클래스타입의 DTO 로 결과를 받아주면 그게 끝이다. ㅋㅋㅋ -> 생성자를 그대로 가져가기떄문에 굉장히 안정적임.
        // 컴파일 시점에 타입이 오류를 잡아주는 장점까지~ ㅋㅋ 개사기 진짜 ㅋㅋ
        List<MemberDto> fetch = query
                .select(new QMemberDto(member.username, member.age))
                .from(member)
                .fetch();
        for (MemberDto memberDto : fetch) {
            System.out.println("memberDto = " + memberDto);
        }

        // TODO @QueryProjection 와 Projection.constructor() 둘다 생성자를 이용하는데 뭐가 다를까?
        //  @QueryProjection 는 새로운 Q 클래스가 생기기때문에 매개변수를 더 넣어도 컴파일시점에 오류를 잡아줌.
        //  하지만 Projection.constructor() 는 매개변수를 더 넣으면 컴파일시점에 오류를 잡지 못하고 런타임에 오류가 남.
        //  이렇게 들어보면 @QueryProjection 가 더 좋은건사실이지만, DTO 는 결국 QueryDsl 에 대한 의존성을 갖게되어 아키텍쳐에 이슈가 생김
        //  결국 DTO 는 API 로 나가게되거나 서비스나 컨트롤러에서 사용할텐데, QueryDsl 에 의존적이기 떄문에 순수하지않은 DTO 가 되버리는것임
    }

    // 동적쿼리를 해결하는 두가지 방식
    // 1. BooleanBuilder
    // 2. where 다중 파라미터 사용
    @Test
    @DisplayName(value = "동적쿼리를 해결하는 첫번째 방법 [BooleanBuilder]")
    public void dynamicQuery_BooleanBuilder() {
        String usernameParam = "member1";
        Integer ageParam = 10;
        List<Member> result = searchMember1(usernameParam, ageParam);
        assertThat(result.size()).isEqualTo(1);
    }
    private List<Member> searchMember1(String usernameCond, Integer ageCond) {
        BooleanBuilder builder = new BooleanBuilder();
        if (usernameCond != null) {                                                 // null 이면 where 문에 조건이 들어가지않음
            builder.and(member.username.eq(usernameCond));
        }
        if (ageCond != null) {                                                      // null 이 아니면 where 조건에 들어감
            builder.and(member.age.eq(ageCond));
        }
        return query
                .selectFrom(member)
                .where(builder)                                                     // where 전에 생성한 BooleanBuilder 가 들어간다.
                .fetch();
    }

    @Test
    @DisplayName(value = "동적쿼리를 해결하는 첫번째 방법 [where 문에 다중 파라미터 --> 실무에서 많이 사용 --> 기가막힌것은 조건을 조립할수가 있다는 것임.]")
    public void dynamicQuery_MultipleParameter() {
        String usernameParam = "member1";
        Integer ageParam = 10;
        List<Member> result = searchMember2(usernameParam, ageParam);
        assertThat(result.size()).isEqualTo(1);
    }
    private List<Member> searchMember2(String usernameCond, Integer ageCond) {
        return query
                .selectFrom(member)
//                .where(usernameEq(usernameCond), ageEqual(ageCond))           // 두개의 조건을 함수로 뺴서 Where 절에 넣어도되고 (where 절에 null 값은 무시된다.)
                .where(allEq(usernameCond, ageCond))                            // 그 두개의 함수를 또 다른함수로 뺴서 하나의 조건을 넣어주어도된다. --> 조건들이 함수로 빠졌기때문에 재사용이 가능 --> 개쩜ㅋㅋㅋ
                .fetch();
    }
    private BooleanExpression usernameEq(String usernameCond) {
        return usernameCond != null ? member.username.eq(usernameCond) : null;
    }
    private BooleanExpression ageEqual(Integer ageCond) {
        return ageCond != null ? member.age.eq(ageCond) : null;
    }
    private BooleanExpression allEq(String usernameCond, Integer ageCond) {
        return usernameEq(usernameCond).and(ageEqual(ageCond));
    }

//    @Commit
    @Test
    @DisplayName(value =
            "Bulk 연산 - 당연히 JPA 에서 배운것처럼 영속성컨텍스트에 값을 저장하지않고 DB 에만 반영하기때문에, DB 와 영속성컨텍스트의 값이 동일하지 않음. 이것도 문젠데 더큰문제는 이후임" +
            "벌크연산 날리고 JPQL 이나 QueryDsl 로 조회를하게되면 무조건 DB 에 SQL 이 나가 결과값을 영속성컨텍스트에 저장을 하게되는데, 이때 영속성컨텍스트에 이미 값이 존재하면, " +
            "DB 에서 갖고온 데이터를 영속성컨텍스트에 저장하지않고 누락시켜버림. 따라서 Bulk 연산되기 전의 데이터를 영속성컨텍스트에서 갖고오게됨." +
            "** 그냥 해결방법으로는 순수 JPA 처럼 영속성컨텍스트에 있는 것들을 flush 해서 DB 랑 데이터를 맞추고, clear 로 영속성컨텍스트를 비워주면 됨.")
    public void test() {
        long count = query
                .update(member)
                .set(member.username, "비회원")
                .where(member.age.lt(28))
                .execute();

//        em.flush();
//        em.clear();

        List<Member> result = query
                .selectFrom(member)
                .fetch();
        for (Member member1 : result) {
            System.out.println("member1 = " + member1);
        }
    }

    @Test
    @DisplayName(value = "모든 회원의 나이를 1 더해서 수정해")
    public void bulkAdd() {
        long count = query
                .update(member)
                .set(member.age, member.age.multiply(1))
                .execute();
    }

    @Test
    @DisplayName(value = "모든 회원의 나이를 2 곱해서 수정해")
    public void bulkMultiply() {
        long count = query
                .update(member)
                .set(member.age, member.age.multiply(1))
                .execute();
    }

    @Test
    @DisplayName(value = "특정 모두 지우기")
    public void bulkDelete() {
        long count = query
                .delete(member)
                .where(member.age.gt(18))
                .execute();
    }

    // SQL Function 은 JPA 와 같이 Dialect 에 등록된 내용만 호출할 수 있다.
    @Test
    @DisplayName(value = "SQL Function 사용. --> member 라는 단어를 M 으로 변경 --> String 을 바꾸는거라 stringTemplate 사용")
    public void sqlFunction() {
        List<String> result = query
                .select(
                        Expressions.stringTemplate(
                                "function('replace', {0}, {1}, {2})",
                                member.username, "member", "M"))
                .from(member)
                .fetch();
        for (String s : result) {
            System.out.println("s = " + s);
        }
    }

    @Test
    @DisplayName(value = "")
    public void sqlFunction2() {
        List<String> result = query
                .select(member.username)
                .from(member)
                // .where(member.username.eq(Expressions.stringTemplate("function('lower', {0})", member.username)))
                .where(member.username.eq(member.username.lower()))
                .fetch();
        System.out.println("result = " + result);
    }
}