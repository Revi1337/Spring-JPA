package com.example.primarymapping.tables;

import jakarta.persistence.*;

@Entity
@SequenceGenerator(
        name = "GENERATOR",                 // 식별자 생성기 이름
        sequenceName = "MEMBER_SEQUENCE",   // 데이터베이스에 등록되어 있는 시퀀스 이름 (매핑할 데이터베이스 시퀀스 이름)
        allocationSize = 1,                 // 시퀀스 한번 호출에 증가하는 수 (성능 최적화에 사용됨. DB 시퀀스 값이 하나씩 증가되도록 설정되어 있으면 이 값을 반드시 1로 설정해야 함.)
        initialValue = 1)                   // DDL 생성시에만 사용됨. 시퀀스 DDL 을 생성할 떄 처음 1, 시작하는 수를 지정한다.
public class Member {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "GENERATOR")
    private Long id;        // GeneratedValue 의 타입은 String 이 될 수 없음.

    @Column(name = "name", nullable = false)
    private String username;

    public Member() {}

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
