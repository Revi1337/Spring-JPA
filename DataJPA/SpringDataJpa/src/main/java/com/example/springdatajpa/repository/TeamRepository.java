package com.example.springdatajpa.repository;

import com.example.springdatajpa.entity.Team;
import org.springframework.data.jpa.repository.JpaRepository;

// TODO 개발자는 JpaRepository 인터페이스만 선언해주면, Spring DataJpa 스스로 JpaRepository 의 구현체를 만들어서 인젝션을 해주는 것임.
// Spring DataJpa 가 애플리케이션 로딩시점에 JpaRepository 인터페이스와 관련된 레포지토리(여기서는 MemberRepository)의 구현체를 만들어서 인젝션해주는 것임.
public interface TeamRepository extends JpaRepository<Team, Long> {

}
