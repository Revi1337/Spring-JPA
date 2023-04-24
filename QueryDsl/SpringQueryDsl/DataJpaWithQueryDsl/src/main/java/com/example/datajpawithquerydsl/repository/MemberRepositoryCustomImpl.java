package com.example.datajpawithquerydsl.repository;

import com.example.datajpawithquerydsl.dto.MemberSearchCondition;
import com.example.datajpawithquerydsl.dto.MemberTeamDto;
import com.example.datajpawithquerydsl.dto.QMemberTeamDto;
import com.example.datajpawithquerydsl.entity.Member;
import com.querydsl.core.QueryResults;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.util.StringUtils;

import java.util.List;

import static com.example.datajpawithquerydsl.entity.QMember.member;
import static com.example.datajpawithquerydsl.entity.QTeam.team;

public class MemberRepositoryCustomImpl implements MemberRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    public MemberRepositoryCustomImpl(EntityManager entityManager) {
        queryFactory = new JPAQueryFactory(entityManager);
    }

    @Override
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

    // Data JPA 의 페이징 추가
    @Override @SuppressWarnings("deprecation")
    public Page<MemberTeamDto> searchPageSimple(MemberSearchCondition condition, Pageable pageable) {
        QueryResults<MemberTeamDto> results = queryFactory
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
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetchResults();

        List<MemberTeamDto> content = results.getResults();
        long totalCount = results.getTotal();

        return new PageImpl<>(content, pageable, totalCount);
    }

    @Override @SuppressWarnings("deprecation")
    public Page<MemberTeamDto> searchPageComplex(MemberSearchCondition condition, Pageable pageable) {
        // 컨텐츠만 따로 갖고온 다음
        List<MemberTeamDto> content = queryFactory
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
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        // count 쿼리르 따로 분리
//        long totalCount = queryFactory
//                .selectFrom(member)
//                .leftJoin(member.team, team)
//                .where(
//                        usernameEq(condition.getUsername()),
//                        teamNameEq(condition.getTeamName()),
//                        ageGoe(condition.getAgeGoe()),
//                        ageLoe(condition.getAgeLoe()))
//                .fetchCount();
//
//        return new PageImpl<>(content, pageable, totalCount);


        // count 쿼리가 생략 가능한 경우는 아래 두가지
        // 1. 페이지 시작이면서 컨텐츠 사이즈가 페이지 사이즈보다 작을 때
        // 2. 마지막 페이지 일 때 (offset + 컨텐츠 사이즈를 더해서 전체 사이즈 구함)
        JPAQuery<Member> countQuery = queryFactory                             // .fetchCount() 로 카운트 쿼리를 일단 보류
                .selectFrom(member)
                .leftJoin(member.team, team)
                .where(
                        usernameEq(condition.getUsername()),
                        teamNameEq(condition.getTeamName()),
                        ageGoe(condition.getAgeGoe()),
                        ageLoe(condition.getAgeLoe())
                );

        // 페이지 시작이면서 컨텐츠 사이즈가 페이지 사이즈보다 작거나, 마지막 페이지일때만 countQuery 를 호출해줌.
        return PageableExecutionUtils.getPage(content, pageable, () -> countQuery.fetchCount());
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
