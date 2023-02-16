package hellojpa;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

@Embeddable // @Embeddable : 값 타입을 정의하는곳에 표시한다.
public class Address {
    private String city;
    private String street;

    @Column(name = "ZIPCODE")
    private String zipcode;

    public Address() {
    }

    public Address(String city, String street, String zipcode) {
        this.city = city;
        this.street = street;
        this.zipcode = zipcode;
    }

    public String getCity() {
        return city;
    }

    public String getStreet() {
        return street;
    }

    public String getZipcode() {
        return zipcode;
    }

}