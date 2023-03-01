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

}
