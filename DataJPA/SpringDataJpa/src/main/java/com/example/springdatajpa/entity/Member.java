package com.example.springdatajpa.entity;

import com.example.springdatajpa.repository.BaseEntity;
import com.example.springdatajpa.repository.JpaBaseEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter @Setter @NoArgsConstructor(access = AccessLevel.PROTECTED) @ToString(of = {"id", "username", "age"})
@NamedQuery(                            // 순수 JPA 의 Named Query
        name = "Member.findByUsername", // 메서드이름은 아무렇게나 해도 되지만, 관례상 엔티티명.메서드명
        query = "select m from Member as m where m.username=:username" // 여기다가 JPQL 직접 선언
)
@NamedEntityGraph(                      // 순수 JPA 표준 스펙 (잘 사용 X)
        name="Member.all", attributeNodes = @NamedAttributeNode("team")
)
public class Member extends BaseEntity {

    @Id @GeneratedValue
    @Column(name = "member_id")
    private Long id;

    private String username;

    private int age;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "team_id")
    private Team team;

    public Member(String username) {
        this.username = username;
    }

    public Member(String username, int age, Team team) {
        this.username = username;
        this.age = age;
        if (team != null) {
            changeTeam(team);
        }
    }

    public Member(String username, int age) {
        this.username = username;
        this.age = age;
    }

    public void changeTeam(Team team) {
        this.team = team;
        team.getMembers().add(this);
    }
}
