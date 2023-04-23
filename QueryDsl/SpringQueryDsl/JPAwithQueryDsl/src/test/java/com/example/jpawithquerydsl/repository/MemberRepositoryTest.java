package com.example.jpawithquerydsl.repository;

import com.example.jpawithquerydsl.entity.Member;
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

    @Autowired
    public MemberRepositoryTest(
            MemberJpaRepository memberJpaRepository, MemberQueryDslRepository memberQueryDslRepository) {
        this.memberJpaRepository = memberJpaRepository;
        this.memberQueryDslRepository = memberQueryDslRepository;
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

}
