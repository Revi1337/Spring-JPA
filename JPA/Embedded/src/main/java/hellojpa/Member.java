package hellojpa;

import jakarta.persistence.*;

@Entity
public class Member {

    @Id @GeneratedValue
    @Column(name = "MEMBER_ID")
    private Long id;

    @Column(name = "USERNAME")
    private String username;

    @Embedded // @Embedded : 값 타입을 사용하는 곳에 표시한다. (Period 객체의 컬럼들이 Member 테이블에 들어오게 된다.)
    private Period workPeriod;

    @Embedded // @Embedded : 값 타입을 사용하는 곳에 표시한다. (Period 객체의 컬럼들이 Member 테이블에 들어오게 된다.)
    private Address homeAddress;

    @Embedded // 한 엔티티에서 같은 (Embedded)값 타입을 사용할때 사용한다. @AttributeOverrides 와 @AttributeOverride 로 컬럼명 속성을 재정의해야 함. (city 는 @Embeddable 객체의 필드명, column 에는 새롭게 정의할 컬럼이름을 명시하면 됨.)
    @AttributeOverrides(value = {
            @AttributeOverride(name = "city", column = @Column(name = "WORK_CITY")),
            @AttributeOverride(name = "street", column = @Column(name = "WORK_STREET")),
            @AttributeOverride(name = "zipcode", column = @Column(name = "WORK_ZIPCODE"))
    })
    private Address workAddress;

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

    public Period getWorkPeriod() {
        return workPeriod;
    }

    public void setWorkPeriod(Period workPeriod) {
        this.workPeriod = workPeriod;
    }

    public Address getHomeAddress() {
        return homeAddress;
    }

    public void setHomeAddress(Address homeAddress) {
        this.homeAddress = homeAddress;
    }
}
