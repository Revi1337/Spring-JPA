package hellojpa;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity
public class Parent {
    @Id @GeneratedValue
    private Long id;

    private String name;

    // TODO cascade = CascadeType.ALL 은 엔티티를 영속화활때 연관된 엔티티도 함께 영속화하는 편리함을 제공함. (참조하는 곳이 하나일떄 사용해야함. child 가 다른 객체와 연관관계가 되어있으면 안다는 말.)
    @OneToMany(mappedBy = "parent", cascade = CascadeType.ALL)
    // TODO orphanRemoval = true 은 참조가 제거된 엔티티는 다른곳에서 참조하지 않는 고아객체로 보고 삭제하는 기능임. (참조하는 곳이 하나일떄 사용해야함. child 가 다른 객체와 연관관계가 되어있으면 안다는 말. - 특정 엔티티가 개인 소유할때 사용), CascadeType.REMOVE 처럼 동작함.
//    @OneToMany(mappedBy = "parent", orphanRemoval = true)
    // TODO 둘다 사용시 부모를 지우면 자식도 지워지고, 자식을 지우면 자식만 지워짐.
//    @OneToMany(mappedBy = "parent", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Child> childList = new ArrayList<>();

    public void addChild(Child child) {
        childList.add(child);
        child.setParent(this);
    }

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

    public List<Child> getChildList() {
        return childList;
    }

    public void setChildList(List<Child> childList) {
        this.childList = childList;
    }
}
