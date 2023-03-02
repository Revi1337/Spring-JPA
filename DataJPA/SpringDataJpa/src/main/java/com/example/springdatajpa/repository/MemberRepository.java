package com.example.springdatajpa.repository;

import com.example.springdatajpa.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

// TODO 개발자는 JpaRepository 인터페이스만 선언해주면, Spring DataJpa 스스로 JpaRepository 의 구현체를 만들어서 인젝션을 해주는 것임.
// Spring DataJpa 가 애플리케이션 로딩시점에 JpaRepository 인터페이스와 관련된 레포지토리(여기서는 MemberRepository)의 구현체를 만들어서 인젝션해주는 것임.
public interface MemberRepository extends JpaRepository<Member, Long> {

    List<Member> findByUsernameAndAgeGreaterThan(String username, int age);

    List<Member> findHelloBy();

    List<Member> findTop3HelloBy();

    @Query(name = "Member.findByUsername")  // DataJPA 의 Named 쿼리 실행 (Entity 가서 @NamedQuery 를 찾음)
    List<Member> findByUsername(@Param("username") String username); // DataJPA 에서의 파라미터 바이딩은 @Param 사용
}
