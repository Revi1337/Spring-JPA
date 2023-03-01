package com.example.springdatajpa.repository;

import com.example.springdatajpa.entity.Member;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.NoSuchElementException;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest @Transactional @Rollback(value = false)
class MemberRepositoryTest {

    private final MemberRepository memberRepository;

    @Autowired
    public MemberRepositoryTest(MemberRepository memberRepository) {
        this.memberRepository = memberRepository;
    }

    @Test
    @DisplayName(value = "testMember")
    public void testMember() throws Exception {
        Member member = new Member("mebmerA");
        Member savedMember = memberRepository.save(member);

        Member findMember = memberRepository.findById(savedMember.getId()).orElseThrow(NoSuchElementException::new);
        assertThat(savedMember.getId()).isEqualTo(findMember.getId());
        assertThat(savedMember.getUsername()).isEqualTo(findMember.getUsername());
        assertThat(savedMember).isEqualTo(findMember);

    }

    @Test
    @DisplayName(value = "JpaRepository 의 정체")
    public void identifyMemberRepository() throws Exception {
        // TODO 개발자는 JpaRepository 인터페이스만 선언해주면, Spring DataJpa 스스로 JpaRepository 의 구현체를 만들어서 인젝션을 해주는 것임.
        // Spring DataJpa 가 애플리케이션 로딩시점에 JpaRepository 인터페이스와 관련된 레포지토리(여기서는 MemberRepository)의 구현체를 만들어서 인젝션해주는 것임.
        System.out.println("JpaRepository Class = " + memberRepository.getClass().getName());
    }

    @Test
    @DisplayName(value = "basicCRUD [직접 구현한 순수 JPA 레포지토리 --> DataJPA 가 제공하는 레포지토리]")
    public void basicCRUD() throws Exception {
        // 직접 JPA 로 구현한 순수 MemberJpaRepository 를 DataJPA 에서 제공하는 Repository 로 갈아끼기만했는데, 그대로 CRUD 테스트가 통과 --> DataJPA 는 JPA 의 중복되는 CRUD 메서드들을 추상화시킨것. (CrudRepository 에 존재함.)
        Member member1 = new Member("member1");
        Member member2 = new Member("member2");
        memberRepository.save(member1);
        memberRepository.save(member2);

        // 단건 조회 검증
        Member findMember1 = memberRepository.findById(member1.getId()).get();
        Member findMember2 = memberRepository.findById(member2.getId()).get();
        assertThat(member1).isEqualTo(findMember1);
        assertThat(member2).isEqualTo(findMember2);

        // 변경감지 (dirty checking)
        // --> Test 코드에 @Transactional 이 붙으면 rollback 이어서 변경감지가 수행되지 않지만, @Rollback(false) 를 설정하였기 때문에, commit 된다.
        findMember1.setUsername("member!!");
        assertThat(findMember1.getUsername()).isEqualTo("member!!").isEqualTo(member1.getUsername());

        // 리스트 조회 검증
        List<Member> all = memberRepository.findAll();
        assertThat(all.size()).isEqualTo(2);

        // 카운트 검증
        long count = memberRepository.count();
        assertThat(count).isEqualTo(2);

        // 삭제 검증
        memberRepository.delete(member1);
        memberRepository.delete(member2);
        long deletedCount = memberRepository.count();
        assertThat(deletedCount).isEqualTo(0);

    }
}