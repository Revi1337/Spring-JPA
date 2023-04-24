package com.example.datajpawithquerydsl.repository;

import com.example.datajpawithquerydsl.dto.MemberSearchCondition;
import com.example.datajpawithquerydsl.dto.MemberTeamDto;
import com.example.datajpawithquerydsl.dto.QMemberTeamDto;
import com.example.datajpawithquerydsl.entity.Member;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Optional;

import static com.example.datajpawithquerydsl.entity.QMember.member;
import static com.example.datajpawithquerydsl.entity.QTeam.team;

@Repository
public class MemberQueryDslRepository {

    private final EntityManager entityManager;
    private final JPAQueryFactory queryFactory;
    public MemberQueryDslRepository(EntityManager entityManager) {
        this.entityManager = entityManager;
        queryFactory = new JPAQueryFactory(entityManager);
    }

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
