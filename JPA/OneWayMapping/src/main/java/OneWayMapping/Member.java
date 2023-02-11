package OneWayMapping;

import jakarta.persistence.*;

@Entity
public class Member {

    @Id @GeneratedValue @Column(name = "MEMBER_ID") private Long id;
    @Column(name = "USERNAME") private String username;

    // TODO 1. Member 와 Team 의 관계가 어떤관계인지 JPA 에게 말해주어야함. (하나의 Team 에는 여러명의 Member 가 들어갈 수 있음. 따라서 Member 입장에서 Team 은 N:1 이니까 ManyToOne 으로 매핑해야함.)
    // TODO 2. 여기서 중요한 것은, JOIN 할 컬럼을 선택해주어야 함.
    // - @JoinColumn 의 name 속성은 그저 `MEMBER 테이블의 컬럼명을 지정` 하는 것뿐 Team 과 매핑하는것과는 아무 관련이 없음. (실제로 MEMBER 테이블에 TEAM_ID 라는 컬럼이 생겨셔 참조할수있게됨.)
    // - Member 와 Team 을 연관관계(매핑)맺는것은 referencedColumnName 속성이하는데, 이것을 생략하면 자동으로 Team 의 PK 값으로 연관관계를 맺어줘서 생략이 가능하다.
    //   결과적으로 Team 의 PK 는 MEMBER 테이블의 FK 로 사용되며, 이 FK 컬럼의 이름이 TEAM_ID 인 것임.
    @ManyToOne @JoinColumn(name = "TEAM_ID") private Team team;


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
