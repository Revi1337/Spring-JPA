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
    
    @Test
    @DisplayName(value = "DataJPA 의 쿼리메서드를 테스트")
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
    @DisplayName(value = "By 뒤에 아무것도 없으면 전체조회")
    public void findHelloBy() throws Exception {
        List<Member> helloBy = memberRepository.findHelloBy(); // 전체조회
        List<Member> top3HelloBy = memberRepository.findTop3HelloBy(); // LIMIT
    }

    @Test
    @DisplayName(value = "DataJPA 의 NamedQuery 실행 테스트")
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
    @DisplayName(value = "@Query 어노테이션을 사용해서 리퍼지토리 인터페이스에 직접 정의한것을 테스트 (이건 많이 사용)")
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
    @DisplayName(value = "@Query 어노테이션을 사용해서 Entity 가 아닌, 값을 조회. (@Embedded 값타입도 조회 가능)")
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
    @DisplayName(value = "@Query 어노테이션을 사용해서 Entity 와 값이 아닌, DTO 로 바로 반환")
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
    @DisplayName(value = "반환 타입")
    public void returnType() throws Exception {
        Member m1 = new Member("AAA", 10);
        Member m2 = new Member("BBB", 20);
        memberRepository.save(m1);
        memberRepository.save(m2);

        List<Member> aaa = memberRepository.findListByUsername("AAA"); // 컬렉션 반환
        Member aaa1 = memberRepository.findMemberByUsername("AAA"); // 단건 반환
        Member aaa2 = memberRepository.findOptionalByUsername("AAA").orElseThrow(NoResultException::new); // Optional 로 반환

        // 반환값이 리스트인 메서드의 값이 없어도 익셉션이 아닌, Empty Collection 이 반환됨
        System.out.println(memberRepository.findListByUsername("asdfasdasdf").size());

        // 순수 JPA 는 getSingleResult 했을 때 값이 없으면 NoResultException 이 터졌었음
        // 하지만, DataJPA 는 값이 없으면 NoResultException 을 try catch 로 받아서 null 이 반환되도록 변경됨.
        Member res = memberRepository.findMemberByUsername("sdfads da");
        System.out.println(res);

        // 하지만, 리턴 타입이 Optional 이나 단건 인데 쿼리의 결과가 두가지가 이상이 나오면 NonUniqueResultException 이 터짐
        // DataJpa 는 NonUniqueResultException 을 IncorrectResultSizeDataAccessException 이라는 스프링 에외로 변환해서 던져줌.
        Optional<Member> aaa3 = memberRepository.findOptionalByUsername("AAA");
        System.out.println("aaa3 = " + aaa3);
    }

    @Test
    @DisplayName(value = "DataJPA 의 Paging 테스트")
    public void pagingTest() throws Exception {
        memberRepository.save(new Member("member1", 10));
        memberRepository.save(new Member("member2", 10));
        memberRepository.save(new Member("member3", 10));
        memberRepository.save(new Member("member4", 10));
        memberRepository.save(new Member("member5", 10));
        memberRepository.save(new Member("member6", 10));
        int age = 10;

        // username 기준으로 내림차순정렬해서, 한페이지당 3개씩 끊어서 0 페이지만 갖고오겠다는 의미임. (PageRequest 는 Pageable 인터페이스의 구현체)
        PageRequest pageRequest = PageRequest.of(0, 4, Sort.by(Sort.Direction.DESC, "username"));

        // Pageable 의 구현체(PageRequest) 를 넘기면 페이징쿼리는 날라가는데, 반환타입에 따라서, count(*) 같은 쿼리를 날릴지 안날릴지 결정됨.
        // 반환타입이 Page 면 count(*) 를 가져옴 --> 세부적인 정보.
        // 반환타입이 Slice 면 count(*) 를 가져오지않고, 다음페이지가 있냐없냐만 판별. --> 즉, 설정한 페이지에서 갖고오기한 size 보다 1 크게 페이지를 갖고와 다음페이지가 있냐없냐만 판별.(getTotalElements(), getTotalPages() 가 없음.)
        Page<Member> page = memberRepository.findByAge(age, pageRequest);

        Page<MemberDto> toMap = page.map(member -> new MemberDto(member.getId(), member.getUsername(), null));

        List<Member> content = page.getContent();               // 현재 페이지에 조회된 데이터 (row 들)
        for (Member member : content)
            System.out.println("member = " + member);

        System.out.println("getSize = " + page.getSize());      // 하나에 페이지에 들어가는 크기 (row 개수)
        System.out.println("totalElements = " + page.getTotalElements());   // page 로 쪼개기 전, 총 row 의 개수
        System.out.println("getTotalPages = " + page.getTotalPages());      // 총 page 수
        System.out.println("getNumber = " + page.getNumber());  // page 로 쪼갠 후, 현재가 몇 page 인지 (몇쪽인지)
        System.out.println("getNumberOfElements = " + page.getNumberOfElements()); // 현재 page 안에 존재하는 row 의 수.
        System.out.println("isEmpty = " + page.isEmpty());      // 현재 페이지가 비어있는지.
        System.out.println("isFirst = " + page.isFirst());      // 첫번째 페이지인지
        System.out.println("isLast = " + page.isLast());        // 마지막 페이지인지
        System.out.println("hasNext = " + page.hasNext());                 // 다음 페이지가 있냐
        System.out.println("hasPrevious" + page.hasPrevious());            // 이전 페이지가 있냐

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
    @DisplayName(value = "DataJPA 의 Slicing 테스트")
    public void slicingTest() throws Exception {
        memberRepository.save(new Member("member1", 10));
        memberRepository.save(new Member("member2", 10));
        memberRepository.save(new Member("member3", 10));
        memberRepository.save(new Member("member4", 10));
        memberRepository.save(new Member("member5", 10));
        memberRepository.save(new Member("member6", 10));
        int age = 10;

        PageRequest pageRequest = PageRequest.of(0, 3, Sort.by(Sort.Direction.DESC, "username"));

        // Pageable 의 구현체(PageRequest) 를 넘기면 페이징쿼리는 날라가는데, 반환타입에 따라서, count(*) 같은 쿼리를 날릴지 안날릴지 결정됨.
        // 반환타입이 Page 면 count(*) 를 가져옴 --> 세부적인 정보.
        // 반환타입이 Slice 면 count(*) 를 가져오지않고, 다음페이지가 있냐없냐만 판별. --> 즉, 설정한 size() 보다 1 크게 페이지를 갖고와 다음페이지가 있냐없냐만 판별.
        Slice<Member> page = memberRepository.findByAge(age, pageRequest);

        List<Member> content = page.getContent();               // 현재 페이지에 조회된 데이터 (row 들)
        for (Member member : content)
            System.out.println("member = " + member);

        System.out.println("getSize = " + page.getSize());      // 하나에 페이지에 들어가는 크기 (row 개수)
        System.out.println("getNumber = " + page.getNumber());  // page 로 쪼갠 후, 현재가 몇 page 인지 (몇쪽인지)
        System.out.println("getNumberOfElements = " + page.getNumberOfElements()); // 현재 page 안에 존재하는 row 의 수.
        System.out.println("isEmpty = " + page.isEmpty());      // 현재 페이지가 비어있는지.
        System.out.println("isFirst = " + page.isFirst());      // 첫번째 페이지인지
        System.out.println("isLast = " + page.isLast());        // 마지막 페이지인지
        System.out.println("hasNext = " + page.hasNext());                 // 다음 페이지가 있냐
        System.out.println("hasPrevious" + page.hasPrevious());            // 이전 페이지가 있냐

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
    @DisplayName(value = "DatJPA 로 구현한 bulkUpdate 테스트")
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
            System.out.println("member.team.class = " + member.getTeam().getClass()); // Proxy 객체
            System.out.println("member.team.name = " + member.getTeam().getName()); // Proxy 객체 초기화
        }

        em.flush();
        em.clear();

        System.out.println("=====================================FETCH JOIN=====================================");
        List<Member> fetchJoinMembers = memberRepository.findMemberFetchJoin();        // fetch join 으로 lazy 애들을 한방쿼리로 모두 갖고옴
        for (Member member : fetchJoinMembers) {
            System.out.println("member.username = " + member.getUsername());
            System.out.println("member.team.class = " + member.getTeam().getClass()); // Proxy 객체가 아닌 진짜 Team Entity
            System.out.println("member.team.name = " + member.getTeam().getName());
        }
    }

    @Test
    @DisplayName(value = "JPA Hint 테스트")
    public void queryHint() throws Exception {
        // given
        Member member1 = memberRepository.save(new Member("member1", 10));
        em.flush();
        em.clear();

        // when
        Member findMember = memberRepository.findReadOnlyByUsername("member1"); // 변경감지 체크 X
        findMember.setUsername("member2");

        em.flush();
    }

    @Test
    @DisplayName(value = "JPA Lock 테스트")
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
    @DisplayName(value = "DataJPA 의 Custom Interface 를 생성해서 사용하는 테스트")
    public void customInterface() throws Exception {
        List<Member> result = memberRepository.findMemberCustom();
    }

    @Test
    @DisplayName(value = "핵심 비지로직과 아닌것을 분리하는 방법 테스트")
    public void seperateLogic() {
        memberQueryRepository.findAllMembers();
    }

}