package com.example.springdatajpa.repository;

import com.example.springdatajpa.entity.Member;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest @Transactional
class MemberJpaRepositoryTest {

    private final MemberJpaRepository memberRepository;

    @Autowired
    public MemberJpaRepositoryTest(MemberJpaRepository memberRepository) {
        this.memberRepository = memberRepository;
    }

    @Test
    @DisplayName(value = "testMember")
    @Rollback(value = false)
    public void testMember() throws Exception {
        Member member = new Member("mebmerA");
        Member savedMember = memberRepository.save(member);

        Member findMember = memberRepository.find(savedMember.getId());
        assertThat(savedMember.getId()).isEqualTo(findMember.getId());
        assertThat(savedMember.getUsername()).isEqualTo(findMember.getUsername());
        assertThat(savedMember).isEqualTo(findMember);
    }
}