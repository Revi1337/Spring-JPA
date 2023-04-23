package com.example.jpawithquerydsl.repository;

import com.example.jpawithquerydsl.entity.Member;
import com.querydsl.jpa.JPQLQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.annotation.PostConstruct;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

import static com.example.jpawithquerydsl.entity.QMember.*;

@Repository
public class MemberQueryDslRepository {
    // TODO JPAQueryFactory 를 초기화시켜주는 방법은 취향차이임 (멀티스레드환경에서 걱정이없게 설계됨.)
    // TODO 1. 사용하고자하는 클래스에서 생성자를 통해 초기화시켜주거나 (테스트코드짤때 편함. 주입받아야하는게 하나)
    private final EntityManager entityManager;
    private final JPAQueryFactory queryFactory;
    public MemberQueryDslRepository(EntityManager entityManager) {
        this.entityManager = entityManager;
        queryFactory = new JPAQueryFactory(entityManager);
    }

    // TODO 2. JPAQueryFactory 타입의 Bean 을 생성해서 주입받는 방법이 있음. (인젝션을 두개나 해주어야 하기때문에 테스트때 귀찮. 하지만 @RequiredArgsConstructor 로 해결 가능)
//    private final EntityManager entityManager;
//    private final JPAQueryFactory queryFactory;
//    public MemberQueryDslRepository(EntityManager entityManager, JPAQueryFactory queryFactory) {
//        this.entityManager = entityManager;
//        this.queryFactory = queryFactory;
//    }

    public void save(Member member) {
        entityManager.persist(member);
    }

    public Optional<Member> findById(Long id) {
        return Optional.of(entityManager.find(Member.class, id));
    }

    public List<Member> findAll() {
        return queryFactory
                .select(member)
                .from(member)
                .fetch();
    }

    public List<Member> findByUsername(String username) {
        return queryFactory
                .selectFrom(member)
                .where(member.username.eq(username))
                .fetch();
    }
}
