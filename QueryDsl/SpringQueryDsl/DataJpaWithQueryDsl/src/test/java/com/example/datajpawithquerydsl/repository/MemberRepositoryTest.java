package com.example.datajpawithquerydsl.repository;

import com.example.datajpawithquerydsl.dto.MemberSearchCondition;
import com.example.datajpawithquerydsl.dto.MemberTeamDto;
import com.example.datajpawithquerydsl.entity.Member;
import com.example.datajpawithquerydsl.entity.Team;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest @Transactional
class MemberRepositoryTest {

    private final EntityManager entityManager;

    private final MemberRepository memberRepository;

    @Autowired
    public MemberRepositoryTest(EntityManager entityManager, MemberRepository memberRepository) {
        this.entityManager = entityManager;
        this.memberRepository = memberRepository;
    }

    @Test
    @DisplayName(value = "순수하게 Data JPA 만 사용했을때의 CRUD 테스트")
    public void jpaMemberTest() {
        Member member = new Member("member1", 10);
        memberRepository.save(member);

        Member findMember = memberRepository.findById(member.getId()).get();
        assertThat(findMember.getUsername()).isEqualTo("member1");

        List<Member> findAll = memberRepository.findAll();
        assertThat(findAll).extracting("username").containsExactly("member1");
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
        List<MemberTeamDto> result = memberRepository.search(memberSearchCondition);
        assertThat(result).extracting("username").containsExactly("member4");
    }

    @Test
    @DisplayName(value = "DataJPA 의 페이징을 이용해서 QueryDsl 에서 사용하는 테스트")
    public void searchPageSimpleTest() {
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
        PageRequest pageRequest = PageRequest.of(0, 3);
        Page<MemberTeamDto> result = memberRepository.searchPageSimple(memberSearchCondition, pageRequest);

        assertThat(result.getSize()).isEqualTo(3);
        assertThat(result.getContent()).extracting("username").containsExactly("member1", "member2", "member3");
    }

}