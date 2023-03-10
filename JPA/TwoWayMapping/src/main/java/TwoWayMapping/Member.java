package TwoWayMapping;

import jakarta.persistence.*;

@Entity
public class Member {

    @Id @GeneratedValue @Column(name = "MEMBER_ID")
    private Long id;

    @Column(name = "USERNAME")
    private String username;

    @ManyToOne @JoinColumn(name = "TEAM_ID")
    private Team team;      // TODO 이놈이 주인이라는 거임.

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Team getTeam() {
        return team;
    }

    public void setTeam(Team team) {
        this.team = team;
        team.getMembers().add(this);        // TODO 연관관계 편의 메서드 생성
    }
}
