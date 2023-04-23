package com.example.jpawithquerydsl.repository;

import com.example.jpawithquerydsl.dto.MemberSearchCondition;
import com.example.jpawithquerydsl.dto.MemberTeamDto;
import com.example.jpawithquerydsl.entity.Member;
import com.example.jpawithquerydsl.entity.Team;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest @Transactional
class MemberRepositoryTest {

    private final MemberJpaRepository memberJpaRepository;

    private final MemberQueryDslRepository memberQueryDslRepository;

    private final EntityManager entityManager;

    @Autowired
    public MemberRepositoryTest(
            MemberJpaRepository memberJpaRepository,
            MemberQueryDslRepository memberQueryDslRepository,
            EntityManager entityManager) {
        this.memberJpaRepository = memberJpaRepository;
        this.memberQueryDslRepository = memberQueryDslRepository;
        this.entityManager = entityManager;
    }

    @Test
    @DisplayName(value = "순수하게 JPA 만 사용했을때의 CRUD 테스트")
    public void jpaMemberTest() {
        Member member = new Member("member1", 10);
        memberJpaRepository.save(member);
        Member findMember = memberJpaRepository.findById(member.getId()).get();
        assertThat(findMember.getUsername()).isEqualTo("member1");

        List<Member> findAll = memberJpaRepository.findAll();
        assertThat(findAll).extracting("username").containsExactly("member1");
    }

    @Test
    @DisplayName(value = "일부를 QueryDsl 로 변경했을때의 테스트")
    public void queryDslMemberTest() {
        Member member = new Member("member1", 10);
        memberQueryDslRepository.save(member);
        Member findMember = memberQueryDslRepository.findById(member.getId()).get();
        assertThat(findMember.getUsername()).isEqualTo("member1");

        List<Member> findAll = memberQueryDslRepository.findAll();
        assertThat(findAll).extracting("username").containsExactly("member1");

        List<Member> findByUsername = memberQueryDslRepository.findByUsername("member1");
        assertThat(findByUsername).extracting("username").containsExactly("member1");
    }

    @Test
    @DisplayName(value = "동적 쿼리와 성능 최적화 조회 (Builder 사용)")
    public void searchTest() {
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

        MemberSearchCondition memberSearchCondition = new MemberSearchCondition();
        memberSearchCondition.setAgeGoe(35);
        memberSearchCondition.setAgeLoe(40);
        memberSearchCondition.setTeamName("teamB");

        List<MemberTeamDto> result = memberQueryDslRepository.searchByBuilder(memberSearchCondition);
        assertThat(result).extracting("username").containsExactly("member4");
    }

    @Test
    @DisplayName(value = "동적 쿼리와 성능 최적화 조회 (Where 절 다중 파라미터 사용)")
    public void searchWhereMulipleParams() {
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

        MemberSearchCondition memberSearchCondition = new MemberSearchCondition();
        memberSearchCondition.setAgeGoe(35);
        memberSearchCondition.setAgeLoe(40);
        memberSearchCondition.setTeamName("teamB");

        List<MemberTeamDto> result = memberQueryDslRepository.search(memberSearchCondition);
        assertThat(result).extracting("username").containsExactly("member4");
    }

}
