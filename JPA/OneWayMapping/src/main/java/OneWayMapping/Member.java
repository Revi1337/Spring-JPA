package OneWayMapping;

import jakarta.persistence.*;

@Entity
public class Member {

    @Id @GeneratedValue @Column(name = "MEMBER_ID") private Long id;
    @Column(name = "USERNAME") private String username;
//    @Column(name = "TEAM_ID") private Long teamId;  // TODO 객체지향스럽지 못한 방법. (직접 ID 를 외래키로 들고있음.)

    // TODO 1. Member 와 Team 의 관계가 어떤관계인지 JPA 에게 말해주어야함. (하나의 Team 에는 여러명의 Member 가 들어갈 수 있음. 따라서 Member 입장에서 Team 은 N:1 이니까 ManyToOne 으로 매핑해야함.)
    // TODO 2. 여기서 중요한 것은, JOIN 할 Team 의 컬럼을 선택해주어야 함. (Team 의 PK 를 Member 테이블에 TEAM_ID 컬럼으로 저장하겠다는 것임(FK). 이렇게하면 Member 테이블에 TEAM_ID 컬럼이 생김.)
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
