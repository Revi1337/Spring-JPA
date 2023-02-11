package TwoWayMapping;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity
public class Team {

    @Id @GeneratedValue @Column(name = "TEAM_ID")
    private Long id;
    private String name;
    // TODO 1. Member -> Team = N : 1 이었으니까, Team 입장에서는 1: N 임
    // TODO 2. 제일 중요한건 FK 를 주인을 지정하는 mappedBy 속성임. 이놈의 속성값으로는 FK 의 주인이되는 클래스의 Field 명을 적어주는 것임.
    // TODO 주인이 아니기떄문에, 값을 변경하지못하고 조회만 가능.
    @OneToMany(mappedBy = "team")       // TODO 나는 team 에 의해서 관리가되.
    private List<Member> members = new ArrayList<>();

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Member> getMembers() {
        return members;
    }

    public void setMembers(List<Member> members) {
        this.members = members;
    }
}
