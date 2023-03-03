package com.example.springdatajpa.repository;

import com.example.springdatajpa.dto.MemberDto;
import com.example.springdatajpa.entity.Member;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

// TODO 개발자는 JpaRepository 인터페이스만 선언해주면, Spring DataJpa 스스로 JpaRepository 의 구현체를 만들어서 인젝션을 해주는 것임.
// Spring DataJpa 가 애플리케이션 로딩시점에 JpaRepository 인터페이스와 관련된 레포지토리(여기서는 MemberRepository)의 구현체를 만들어서 인젝션해주는 것임.
public interface MemberRepository extends JpaRepository<Member, Long> {

    List<Member> findByUsernameAndAgeGreaterThan(String username, int age);

    List<Member> findHelloBy();

    List<Member> findTop3HelloBy();

    @Query(name = "Member.findByUsername")  // DataJPA 의 Named 쿼리 실행 (Entity 가서 @NamedQuery 를 찾음)
    List<Member> findByUsername(@Param("username") String username); // DataJPA 에서의 파라미터 바인딩

    @Query(value = "select m from Member as m where m.username = :username and m.age = :age") // @Query 어노테이션을 사용해서 리퍼지토리 인터페이스에 직접 정의. name 은 Entity 에 작성한 NamedQuery 를 사용했을 때사용하고, value 는 쿼리문을 바로 실행하는 것임.
    List<Member> findUser(@Param("username") String username, @Param("age") int age);

    @Query(value = "select m.username from Member as m") // 실무 많이 사용 (엔티티가 아닌 단순히 값 한 를 조회)
    List<String> findUsernameList();

    @Query(value = "select new com.example.springdatajpa.dto.MemberDto(m.id, m.username, t.name) from Member m inner join m.team t") // DTO 로 바로 반환하는 방법
    List<MemberDto> findMemberDto();

    @Query(value = "select m from Member as m where m.username in :names") // 여러개의 값을 Collection 으로 조회, SQL 에서는 IN 절로 수행됨.
    List<Member> findByNames(@Param("names") Collection<String> names);

    List<Member> findListByUsername(String username); // 반환타입 컬렉션
    Member findMemberByUsername(String username); // 단건
    Optional<Member> findOptionalByUsername(String username); // 단건 Optional

    @Query(value = "select m from Member as m left outer join m.team t")
    Page<Member> findByAge(int age, Pageable pageable); // DataJPA 의 페이징 처리. 두번째 파라미터에 Pageable 인터페이스를 넣어주면 됨. (즉, 들어오는것은 Pageable 의 구현체들)
//    Slice<Member> findByAge(int age, Pageable pageable);
//    List<Member> findByAge(int age, Pageable pageable);

    // 순수 JPA 의 executeUpdate() 와 같은 역할이며 bulk 시 꼭 명시해주어야함. 적지 않으면 QueryExecutionRequestException 터짐.
    @Modifying(clearAutomatically = true) // clearAutomatically 옵션은 DB 와 영속성컨텍스트가 일치하지않는 문제를 자동으로 해결하기 위한 옵션 --> 자동으로 영속성컨텍스트를 비워주는 역할임. --> 순수 JPA 에서 bulk 연산을 날리고 clear() 하는 것을 자동으로 시켜주는 것임.
    @Query(value = "update Member as m set m.age = m.age + 1 where m.age >= :age")
    int bulkAgePlus(@Param("age") int age);             // DataJPa 에서의 bulk 연산 처리

    @Query(value = "select m from Member as m inner join fetch m.team t")
    List<Member> findMemberFetchJoin();     // fetch join 으로 연관된 Entity 들을 한방쿼리로 갖고오기 (DataJPA 에서 제공하는 fetch join (1))

    @Override @EntityGraph(attributePaths = {"team"}) // 내부적으로 team 을 fetch join 하겠다는 의미임.
    List<Member> findAll();                 // @EntityGraph 를 사용 JPQL 을 사용하지않아도 됨.. (DataJPA 에서 제공하는 fetch join (2))
}
