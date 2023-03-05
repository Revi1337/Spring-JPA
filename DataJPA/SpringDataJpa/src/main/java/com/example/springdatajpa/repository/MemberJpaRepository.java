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

    // 순수 JPA 로 페이징 구현 1
    public List<Member> findByPage(int age, int offset, int limit) {
        return em.createQuery("select m from Member as m where m.age = :age order by m.username desc", Member.class)
                .setParameter("age", age)
                .setFirstResult(offset)
                .setMaxResults(limit)
                .getResultList();
    }

    // 순수 JPA 로 페이징 구현 2
    public long totalCount(int age) {
        return em.createQuery("select count(m) from Member as m where m.age = :age", Long.class)
                .setParameter("age", age)
                .getSingleResult();
    }

    // 순수 JPA 로 Bulk 연산
    // 영속성 컨텍스트를 무시함 --> 영속성 컨텍스트를 무시한다는말은 --> 무조건 쿼리가 나감 --> 영속성컨텍스트를 무시하기 때문에, 업데이트 결과를 1차캐시에 반영하지 않음.
    // 결과적으로 DB 와 영속성컨텍스트가 일치하지않는 문제 발생 --> 해결책은 벌크연산후, .clear() 를 통해 컨텍스트를 초기화시켜주어야함.
    public int bulkAgePlus(int age) {
        return em.createQuery("update Member m set m.age = m.age + 1 where m.age >= :age")
                .setParameter("age", age)
                .executeUpdate();
    }

}
