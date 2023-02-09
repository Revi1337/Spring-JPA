package com.example.primarymapping.tables;

import jakarta.persistence.*;

// TABLE 전략
// 키 생성 전용 테이블을 하나 만들어서 데이터베이스 시퀀스를 흉내내는 전략
// 장점 : 모든 데이터베이스에 적용 가능
// 단점 : 성능

@Entity
@TableGenerator(
        name = "MEMBER_SEQ_GENERATOR",                      // 제너레이터 이름 (식별자 생성기 이름)
        table = "MY_SEQUENCES",                             // MEMBER_SEQUENCE 라는 테이블을 생성 (키생성 테이블명)
        pkColumnValue = "MEMBER_SEQ", allocationSize = 1)   // MEMBER_SEQUENCE 테이블의 PKI 컬럼을 지정. (시퀀스 컬럼명)
public class Dummy {
    @Id
    @GeneratedValue(strategy = GenerationType.TABLE, generator = "MEMBER_SEQ_GENERATOR")
    private Long id;

    @Column(name = "name", nullable = false)
    private String username;

    public Dummy() {}

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
}
