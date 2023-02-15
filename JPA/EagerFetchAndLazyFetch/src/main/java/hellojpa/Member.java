package hellojpa;

import jakarta.persistence.*;

@Entity
public class Member {

    @Id @GeneratedValue
    @Column(name = "MEMBER_ID")
    private Long id;

    @Column(name = "USERNAME")
    private String username;

    // TODO FetchType.LAZY 를 하면 team 을 프록시객체로 조회함(Member 클래스만 DB 에서 조회한다는 말). --> getTeam() 하면 proxy 객체가 반환됨. 이제 getTeam().속성으로 team 의 속성을 건드려주면 이떄 쿼리가 나가게됨.
    // TODO 죽, getTeam().속성 을 하기전까지는 select 쿼리가 나가지않기 때문에 성능이 좋아지는 이점이 있음.
    @ManyToOne(fetch = FetchType.LAZY)
    // TODO FetchType.EAGER 를 하면 getTeam() 하면 일반 Team 이 출력됨. --> Proxy 객체가 아니기때문에 초기화라는 개념이 없음.
    // TODO 이미 Member 를 조회(find())하는 시점에 연관객체인 Team 까지 조인해서 싹다 DB 에서 가져옴
//    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "TEAM_ID")
    private Team team;

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
    }
}
