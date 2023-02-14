package hellojpa;

import jakarta.persistence.*;

@Entity
@Inheritance(strategy = InheritanceType.JOINED) // 조인전략 (부모에 쓰는것임.)
@DiscriminatorColumn(name = "DIS_TYPE")    // DiscriminatorColumn (name 으로 생길 컬럼명을 바꿔줄수있음. 디폴트는 DTYPE 임)
public class Item {

    @Id @GeneratedValue
    private Long id;
    private String name;
    private int price;

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

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }
}
