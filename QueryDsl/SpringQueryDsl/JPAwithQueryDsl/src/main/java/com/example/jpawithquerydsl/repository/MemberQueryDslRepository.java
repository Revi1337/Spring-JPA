package com.example.jpawithquerydsl.repository;

import com.example.jpawithquerydsl.dto.MemberSearchCondition;
import com.example.jpawithquerydsl.dto.MemberTeamDto;
import com.example.jpawithquerydsl.dto.QMemberTeamDto;
import com.example.jpawithquerydsl.entity.Member;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.JPQLQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.annotation.PostConstruct;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Optional;

import static com.example.jpawithquerydsl.entity.QMember.*;
import static com.example.jpawithquerydsl.entity.QTeam.team;

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

    public List<MemberTeamDto> searchByBuilder(MemberSearchCondition condition) {
        BooleanBuilder booleanBuilder = new BooleanBuilder();
        if (StringUtils.hasText(condition.getUsername()))
            booleanBuilder.and(member.username.eq(condition.getUsername()));
        if (StringUtils.hasText(condition.getTeamName()))
            booleanBuilder.and(team.name.eq(condition.getTeamName()));
        if (condition.getAgeGoe() != null)
            booleanBuilder.and(member.age.goe(condition.getAgeGoe()));
        if (condition.getAgeLoe() != null)
            booleanBuilder.and(member.age.loe(condition.getAgeLoe()));

        return queryFactory
                .select(new QMemberTeamDto(
                        member.id.as("memberId"),
                        member.username,
                        member.age,
                        team.id.as("teamId"),
                        team.name.as("teamName")
                ))
                .from(member)
                .leftJoin(member.team, team)
                .where(booleanBuilder)
                .fetch();
    }

    public List<MemberTeamDto> search(MemberSearchCondition condition) {
        return queryFactory
                .select(new QMemberTeamDto(
                        member.id.as("memberId"),
                        member.username,
                        member.age,
                        team.id.as("teamId"),
                        team.name.as("teamName")))
                .from(member)
                .leftJoin(member.team, team)
                .where(
                        usernameEq(condition.getUsername()),
                        teamNameEq(condition.getTeamName()),
                        ageGoe(condition.getAgeGoe()),
                        ageLoe(condition.getAgeLoe()))
                .fetch();
    }

    private BooleanExpression usernameEq(String username) {
        return StringUtils.hasText(username) ? member.username.eq(username) : null;
    }

    private BooleanExpression teamNameEq(String teamName) {
        return StringUtils.hasText(teamName) ? team.name.eq(teamName) : null;
    }

    private BooleanExpression ageGoe(Integer ageGoe) {
        return ageGoe != null ? member.age.goe(ageGoe) : null;
    }

    private BooleanExpression ageLoe(Integer ageLoe) {
        return ageLoe != null ? member.age.loe(ageLoe) : null;
    }

}
