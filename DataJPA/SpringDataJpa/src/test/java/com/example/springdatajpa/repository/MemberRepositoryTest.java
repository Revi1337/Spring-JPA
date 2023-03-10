package com.example.springdatajpa.repository;

import com.example.springdatajpa.dto.MemberDto;
import com.example.springdatajpa.entity.Member;
import com.example.springdatajpa.entity.Team;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceContext;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest @Transactional @Rollback(value = false)
class MemberRepositoryTest {

    private final MemberRepository memberRepository;

    private final TeamRepository teamRepository;

    @PersistenceContext EntityManager em;

    @Autowired MemberQueryRepository memberQueryRepository;

    @Autowired
    public MemberRepositoryTest(MemberRepository memberRepository, TeamRepository teamRepository) {
        this.memberRepository = memberRepository;
        this.teamRepository = teamRepository;
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
    @DisplayName(value = "JpaRepository ??? ??????")
    public void identifyMemberRepository() throws Exception {
        // TODO ???????????? JpaRepository ?????????????????? ???????????????, Spring DataJpa ????????? JpaRepository ??? ???????????? ???????????? ???????????? ????????? ??????.
        // Spring DataJpa ??? ?????????????????? ??????????????? JpaRepository ?????????????????? ????????? ???????????????(???????????? MemberRepository)??? ???????????? ???????????? ?????????????????? ??????.
        System.out.println("JpaRepository Class = " + memberRepository.getClass().getName());
    }

    @Test
    @DisplayName(value = "basicCRUD [?????? ????????? ?????? JPA ??????????????? --> DataJPA ??? ???????????? ???????????????]")
    public void basicCRUD() throws Exception {
        // ?????? JPA ??? ????????? ?????? MemberJpaRepository ??? DataJPA ?????? ???????????? Repository ??? ????????????????????????, ????????? CRUD ???????????? ?????? --> DataJPA ??? JPA ??? ???????????? CRUD ??????????????? ??????????????????. (CrudRepository ??? ?????????.)
        Member member1 = new Member("member1");
        Member member2 = new Member("member2");
        memberRepository.save(member1);
        memberRepository.save(member2);

        // ?????? ?????? ??????
        Member findMember1 = memberRepository.findById(member1.getId()).get();
        Member findMember2 = memberRepository.findById(member2.getId()).get();
        assertThat(member1).isEqualTo(findMember1);
        assertThat(member2).isEqualTo(findMember2);

        // ???????????? (dirty checking)
        // --> Test ????????? @Transactional ??? ????????? rollback ????????? ??????????????? ???????????? ?????????, @Rollback(false) ??? ??????????????? ?????????, commit ??????.
        findMember1.setUsername("member!!");
        assertThat(findMember1.getUsername()).isEqualTo("member!!").isEqualTo(member1.getUsername());

        // ????????? ?????? ??????
        List<Member> all = memberRepository.findAll();
        assertThat(all.size()).isEqualTo(2);

        // ????????? ??????
        long count = memberRepository.count();
        assertThat(count).isEqualTo(2);

        // ?????? ??????
        memberRepository.delete(member1);
        memberRepository.delete(member2);
        long deletedCount = memberRepository.count();
        assertThat(deletedCount).isEqualTo(0);

    }
    
    @Test
    @DisplayName(value = "DataJPA ??? ?????????????????? ?????????")
    public void findByUsernameAndAgeGreaterThan() throws Exception {
        Member m1 = new Member("AAA", 10);
        Member m2 = new Member("AAA", 20);
        memberRepository.save(m1);
        memberRepository.save(m2);

        List<Member> result = memberRepository.findByUsernameAndAgeGreaterThan("AAA", 15);
        assertThat(result.get(0).getUsername()).isEqualTo("AAA");
        assertThat(result.get(0).getAge()).isEqualTo(20);
        assertThat(result.size()).isEqualTo(1);
    }
    
    @Test
    @DisplayName(value = "By ?????? ???????????? ????????? ????????????")
    public void findHelloBy() throws Exception {
        List<Member> helloBy = memberRepository.findHelloBy(); // ????????????
        List<Member> top3HelloBy = memberRepository.findTop3HelloBy(); // LIMIT
    }

    @Test
    @DisplayName(value = "DataJPA ??? NamedQuery ?????? ?????????")
    public void namedQueryTest() throws Exception {
        Member m1 = new Member("AAA", 10);
        Member m2 = new Member("BBB", 20);
        memberRepository.save(m1);
        memberRepository.save(m2);

        List<Member> result = memberRepository.findByUsername("AAA");
        Member findMember = result.get(0);
        assertThat(findMember).isEqualTo(m1);
    }

    @Test
    @DisplayName(value = "@Query ?????????????????? ???????????? ??????????????? ?????????????????? ?????? ??????????????? ????????? (?????? ?????? ??????)")
    public void testQuery() throws Exception {
        Member m1 = new Member("AAA", 10);
        Member m2 = new Member("BBB", 20);
        memberRepository.save(m1);
        memberRepository.save(m2);

        List<Member> result = memberRepository.findUser("AAA", 10);
        Member findMember = result.get(0);
        assertThat(findMember).isEqualTo(m1);
    }

    @Test
    @DisplayName(value = "@Query ?????????????????? ???????????? Entity ??? ??????, ?????? ??????. (@Embedded ???????????? ?????? ??????)")
    public void findUsernameList() throws Exception {
        Member m1 = new Member("AAA", 10);
        Member m2 = new Member("BBB", 20);
        memberRepository.save(m1);
        memberRepository.save(m2);

        List<String> usernameList = memberRepository.findUsernameList();
        for (String s : usernameList) {
            System.out.println("s = " + s);
        }
    }

    @Test
    @DisplayName(value = "@Query ?????????????????? ???????????? Entity ??? ?????? ??????, DTO ??? ?????? ??????")
    public void findMemberDtoTest() throws Exception {
        Team team = new Team("teamA");
        teamRepository.save(team);

        Member m1 = new Member("AAA", 10);
        m1.setTeam(team);
        memberRepository.save(m1);

        List<MemberDto> memberDto = memberRepository.findMemberDto();
        for (MemberDto dto : memberDto)
            System.out.println("dto = " + dto);
    }
    
    @Test
    @DisplayName(value = "findByNamesTest")
    public void findByNamesTest() throws Exception {
        Member m1 = new Member("AAA", 10);
        Member m2 = new Member("BBB", 20);
        memberRepository.save(m1);
        memberRepository.save(m2);

        List<Member> usernameList = memberRepository.findByNames(Arrays.asList("AAA", "BBB"));
        for (Member member : usernameList) {
            System.out.println("member = " + member);
        }
    }

    @Test
    @DisplayName(value = "?????? ??????")
    public void returnType() throws Exception {
        Member m1 = new Member("AAA", 10);
        Member m2 = new Member("BBB", 20);
        memberRepository.save(m1);
        memberRepository.save(m2);

        List<Member> aaa = memberRepository.findListByUsername("AAA"); // ????????? ??????
        Member aaa1 = memberRepository.findMemberByUsername("AAA"); // ?????? ??????
        Member aaa2 = memberRepository.findOptionalByUsername("AAA").orElseThrow(NoResultException::new); // Optional ??? ??????

        // ???????????? ???????????? ???????????? ?????? ????????? ???????????? ??????, Empty Collection ??? ?????????
        System.out.println(memberRepository.findListByUsername("asdfasdasdf").size());

        // ?????? JPA ??? getSingleResult ?????? ??? ?????? ????????? NoResultException ??? ????????????
        // ?????????, DataJPA ??? ?????? ????????? NoResultException ??? try catch ??? ????????? null ??? ??????????????? ?????????.
        Member res = memberRepository.findMemberByUsername("sdfads da");
        System.out.println(res);

        // ?????????, ?????? ????????? Optional ?????? ?????? ?????? ????????? ????????? ???????????? ????????? ????????? NonUniqueResultException ??? ??????
        // DataJpa ??? NonUniqueResultException ??? IncorrectResultSizeDataAccessException ????????? ????????? ????????? ???????????? ?????????.
        Optional<Member> aaa3 = memberRepository.findOptionalByUsername("AAA");
        System.out.println("aaa3 = " + aaa3);
    }

    @Test
    @DisplayName(value = "DataJPA ??? Paging ?????????")
    public void pagingTest() throws Exception {
        memberRepository.save(new Member("member1", 10));
        memberRepository.save(new Member("member2", 10));
        memberRepository.save(new Member("member3", 10));
        memberRepository.save(new Member("member4", 10));
        memberRepository.save(new Member("member5", 10));
        memberRepository.save(new Member("member6", 10));
        int age = 10;

        // username ???????????? ????????????????????????, ??????????????? 3?????? ????????? 0 ???????????? ?????????????????? ?????????. (PageRequest ??? Pageable ?????????????????? ?????????)
        PageRequest pageRequest = PageRequest.of(0, 4, Sort.by(Sort.Direction.DESC, "username"));

        // Pageable ??? ?????????(PageRequest) ??? ????????? ?????????????????? ???????????????, ??????????????? ?????????, count(*) ?????? ????????? ????????? ???????????? ?????????.
        // ??????????????? Page ??? count(*) ??? ????????? --> ???????????? ??????.
        // ??????????????? Slice ??? count(*) ??? ??????????????????, ?????????????????? ??????????????? ??????. --> ???, ????????? ??????????????? ??????????????? size ?????? 1 ?????? ???????????? ????????? ?????????????????? ??????????????? ??????.(getTotalElements(), getTotalPages() ??? ??????.)
        Page<Member> page = memberRepository.findByAge(age, pageRequest);

        Page<MemberDto> toMap = page.map(member -> new MemberDto(member.getId(), member.getUsername(), null));

        List<Member> content = page.getContent();               // ?????? ???????????? ????????? ????????? (row ???)
        for (Member member : content)
            System.out.println("member = " + member);

        System.out.println("getSize = " + page.getSize());      // ????????? ???????????? ???????????? ?????? (row ??????)
        System.out.println("totalElements = " + page.getTotalElements());   // page ??? ????????? ???, ??? row ??? ??????
        System.out.println("getTotalPages = " + page.getTotalPages());      // ??? page ???
        System.out.println("getNumber = " + page.getNumber());  // page ??? ?????? ???, ????????? ??? page ?????? (????????????)
        System.out.println("getNumberOfElements = " + page.getNumberOfElements()); // ?????? page ?????? ???????????? row ??? ???.
        System.out.println("isEmpty = " + page.isEmpty());      // ?????? ???????????? ???????????????.
        System.out.println("isFirst = " + page.isFirst());      // ????????? ???????????????
        System.out.println("isLast = " + page.isLast());        // ????????? ???????????????
        System.out.println("hasNext = " + page.hasNext());                 // ?????? ???????????? ??????
        System.out.println("hasPrevious" + page.hasPrevious());            // ?????? ???????????? ??????

        assertThat(page.getSize()).isEqualTo(4);
        assertThat(page.getTotalElements()).isEqualTo(6L);
        assertThat(page.getTotalPages()).isEqualTo(2);
        assertThat(page.getNumber()).isEqualTo(0);
        assertThat(page.getNumberOfElements()).isEqualTo(4);
        assertThat(page.isEmpty()).isFalse();
        assertThat(page.isFirst()).isTrue();
        assertThat(page.isLast()).isFalse();
        assertThat(page.hasNext()).isTrue();
        assertThat(page.hasPrevious()).isFalse();
    }

    @Test
    @DisplayName(value = "DataJPA ??? Slicing ?????????")
    public void slicingTest() throws Exception {
        memberRepository.save(new Member("member1", 10));
        memberRepository.save(new Member("member2", 10));
        memberRepository.save(new Member("member3", 10));
        memberRepository.save(new Member("member4", 10));
        memberRepository.save(new Member("member5", 10));
        memberRepository.save(new Member("member6", 10));
        int age = 10;

        PageRequest pageRequest = PageRequest.of(0, 3, Sort.by(Sort.Direction.DESC, "username"));

        // Pageable ??? ?????????(PageRequest) ??? ????????? ?????????????????? ???????????????, ??????????????? ?????????, count(*) ?????? ????????? ????????? ???????????? ?????????.
        // ??????????????? Page ??? count(*) ??? ????????? --> ???????????? ??????.
        // ??????????????? Slice ??? count(*) ??? ??????????????????, ?????????????????? ??????????????? ??????. --> ???, ????????? size() ?????? 1 ?????? ???????????? ????????? ?????????????????? ??????????????? ??????.
        Slice<Member> page = memberRepository.findByAge(age, pageRequest);

        List<Member> content = page.getContent();               // ?????? ???????????? ????????? ????????? (row ???)
        for (Member member : content)
            System.out.println("member = " + member);

        System.out.println("getSize = " + page.getSize());      // ????????? ???????????? ???????????? ?????? (row ??????)
        System.out.println("getNumber = " + page.getNumber());  // page ??? ?????? ???, ????????? ??? page ?????? (????????????)
        System.out.println("getNumberOfElements = " + page.getNumberOfElements()); // ?????? page ?????? ???????????? row ??? ???.
        System.out.println("isEmpty = " + page.isEmpty());      // ?????? ???????????? ???????????????.
        System.out.println("isFirst = " + page.isFirst());      // ????????? ???????????????
        System.out.println("isLast = " + page.isLast());        // ????????? ???????????????
        System.out.println("hasNext = " + page.hasNext());                 // ?????? ???????????? ??????
        System.out.println("hasPrevious" + page.hasPrevious());            // ?????? ???????????? ??????

        assertThat(page.getSize()).isEqualTo(4);
        assertThat(page.getNumber()).isEqualTo(1);
        assertThat(page.getNumberOfElements()).isEqualTo(2);
        assertThat(page.isEmpty()).isFalse();
        assertThat(page.isFirst()).isFalse();
        assertThat(page.isLast()).isTrue();
        assertThat(page.hasNext()).isFalse();
        assertThat(page.hasPrevious()).isTrue();
    }

    @Test
    @DisplayName(value = "DatJPA ??? ????????? bulkUpdate ?????????")
    public void bulkUpdateTest() {
        // given
        memberRepository.save(new Member("member1", 10));
        memberRepository.save(new Member("member2", 19));
        memberRepository.save(new Member("member3", 20));
        memberRepository.save(new Member("member4", 21));
        memberRepository.save(new Member("member5", 40));

        // when
        int resultCount = memberRepository.bulkAgePlus(20);
        List<Member> result = memberRepository.findByUsername("member5");
        Member member5 = result.get(0);
        System.out.println(member5.getAge());

        // then
        assertThat(resultCount).isEqualTo(3);
        assertThat(member5.getAge()).isEqualTo(41);
    }

    @Test
    @DisplayName(value = "findMemberLazy")
    public void findMemberLazy() throws Exception {
        // given
        Team teamA = new Team("teamA");
        Team teamB = new Team("teamB");
        teamRepository.save(teamA);
        teamRepository.save(teamB);
        Member member1 = new Member("member1", 10, teamA);
        Member member2 = new Member("member2", 10, teamB);
        memberRepository.save(member1);
        memberRepository.save(member2);
        em.flush();
        em.clear();

        // when (N + 1)
        System.out.println("=====================================LAZY FETCH=====================================");
        List<Member> lazyMembers = memberRepository.findAll();
        for (Member member : lazyMembers) {
            System.out.println("member.username = " + member.getUsername());
            System.out.println("member.team.class = " + member.getTeam().getClass()); // Proxy ??????
            System.out.println("member.team.name = " + member.getTeam().getName()); // Proxy ?????? ?????????
        }

        em.flush();
        em.clear();

        System.out.println("=====================================FETCH JOIN=====================================");
        List<Member> fetchJoinMembers = memberRepository.findMemberFetchJoin();        // fetch join ?????? lazy ????????? ??????????????? ?????? ?????????
        for (Member member : fetchJoinMembers) {
            System.out.println("member.username = " + member.getUsername());
            System.out.println("member.team.class = " + member.getTeam().getClass()); // Proxy ????????? ?????? ?????? Team Entity
            System.out.println("member.team.name = " + member.getTeam().getName());
        }
    }

    @Test
    @DisplayName(value = "JPA Hint ?????????")
    public void queryHint() throws Exception {
        // given
        Member member1 = memberRepository.save(new Member("member1", 10));
        em.flush();
        em.clear();

        // when
        Member findMember = memberRepository.findReadOnlyByUsername("member1"); // ???????????? ?????? X
        findMember.setUsername("member2");

        em.flush();
    }

    @Test
    @DisplayName(value = "JPA Lock ?????????")
    public void lockTest() throws Exception {
        // given
        Member member1 = new Member("member1", 10);
        memberRepository.save(member1);
        em.flush();
        em.clear();

        // when
        List<Member> result = memberRepository.findLockByUsername("member1");
    }

    @Test
    @DisplayName(value = "DataJPA ??? Custom Interface ??? ???????????? ???????????? ?????????")
    public void customInterface() throws Exception {
        List<Member> result = memberRepository.findMemberCustom();
    }

    @Test
    @DisplayName(value = "?????? ??????????????? ???????????? ???????????? ?????? ?????????")
    public void seperateLogic() {
        memberQueryRepository.findAllMembers();
    }

}