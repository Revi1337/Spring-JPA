package com.example.springdatajpa.repository;

import com.example.springdatajpa.entity.Member;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

// 순수하게 JPA 로만 만든 Member 의 CRUD Repository
@Repository @RequiredArgsConstructor
public class MemberJpaRepository {

    private final EntityManager em;

    public Member save(Member member) {
        em.persist(member);
        return member;
    }

    public void delete(Member member) {
        em.remove(member);
    }

    public List<Member> findAll() {
        return em.createQuery("select m from Member as m", Member.class)
                .getResultList();
    }

    public Optional<Member> findById(Long id) {
        Member member = em.find(Member.class, id);
        return Optional.ofNullable(member);
    }

    public long count() {
        return em.createQuery("select count(m) from Member as m", Long.class) // count 는 long 으로 반환해줌
                .getSingleResult();
    }

    public Member find(Long id) {
        return em.find(Member.class, id);
    }

    // DataJPA 의 쿼리메서드를 순수한 JPA 로 구현 --> 매우 귀찮은 일 --> DataJPA 가 이러한 쿼리메서드기능을 지원
    public List<Member> findByUsernameAndAgeGreaterThen(String username, int age) {
        return em.createQuery("select m from Member as m where m.username=:username and m.age > :age", Member.class)
                .setParameter("username", username)
                .setParameter("age", age)
                .getResultList();
    }

    // Member 엔티티에 정의한 NamedQuery 를 호출하여 사용하는 것임. (사용하는것은 JPQL 이랑 동일함)
    public List<Member> findByUsername(String username) {
          return em.createNamedQuery("Member.findByUsername", Member.class)
                .setParameter("username", username)
                .getResultList();
    }

}
