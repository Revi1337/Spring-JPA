package com.example.springdatajpa.entity;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;


@SpringBootTest @Transactional @Rollback(value = false)
class MemberTest {

    @PersistenceContext EntityManager entityManager;

    @Test
    @DisplayName(value = "testEntity")
    public void testEntity() throws Exception {
        Team teamA = new Team("teamA");
        Team teamB = new Team("teamB");
        entityManager.persist(teamA);
        entityManager.persist(teamB);

        Member member1 = new Member("member1", 10, teamA);
        Member member2 = new Member("member2", 20, teamA);
        Member member3 = new Member("member3", 30, teamB);
        Member member4 = new Member("member4", 40, teamB);

        entityManager.persist(member1);
        entityManager.persist(member2);
        entityManager.persist(member3);
        entityManager.persist(member4);

        entityManager.flush();
        entityManager.clear();

        List<Member> members = entityManager.createQuery("select m from Member as m", Member.class).getResultList();
        for (Member member : members) {
            System.out.println("member = " + member);
            // TODO QUESTION
            // JPA 기본편 강의에서는 지연로딩 설정시 member.geTeam() 을 호출해도 팀에 있는 필드를 호출(member.getTeam().getName()) 하기 전까지는 SQL 문이 호출되지 않았음
            // 현재 예제에서는 member.getTeam() 만 호출했는데도 SQL 문이 실행되고있음
            // TODO ANSWER
            // 사실, 식별자(id=PK)를 제외한 어떤 메서드를 호출해도 다 Proxy 가 초기화 되어버림. 왜냐하면 그 메서드 안에서 어떤 필드를 호출할지 JPA 는 모르기 때문에, 우선 다 초기화 시켜버리는 것임.
            // 실제로 member.getTeam() 과 member.getTeam().getName() 모두 SQL 쿼리가 나가지만, member.getTeam().getId() 로 식별자를 호출하면 쿼리가 나가지 않음.
            System.out.println("-> member.team = " + member.getTeam());

        }
    }

}