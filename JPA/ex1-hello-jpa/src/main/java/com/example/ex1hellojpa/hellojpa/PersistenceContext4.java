package com.example.ex1hellojpa.hellojpa;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.Persistence;

public class PersistenceContext4 {
    public static void main(String[] args) {
        EntityManagerFactory entityManagerFactory = Persistence.createEntityManagerFactory("hello");
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        EntityTransaction entityTransaction = entityManager.getTransaction();
        entityTransaction.begin();
        try {
            // TODO 엔티티 수정 (변경 감지)
            Member member = entityManager.find(Member.class, 150L);       // 영속 엔티티 조회
            member.setName("ZZZZZ");                                        // 영속 엔티티 데이터 수정

            // 여기서 중요한 것. 값의 변경 후, persist() 를 호출해야할까?. 정답은 아님.
            // 정답은 JPA 컨셉 혹은 목적에 있음. JPA 의 목적은 객체를 컬렉션 다루듯이 다루기 위함임. 컬렉션에서 값을 꺼내고, 그 값을 수정한 후, 그 값을 다시 집어넣지 않는다는 얘기와 같음. 오히려 persist() 를 쓰면 안됨.
            // 사실, 영속성 컨텍스트의 1차 캐시안에는 PKI, Entity, SnapShot 이 있는데, 스냅샷은 값을 읽어온 최초 시점의 상태를 의미함.
            // JPA 는 트랜잭션의 커밋 시점에 내부적으로 Flush 가 호출이 되면서 Entity 와 스냅샷을 비교함. 여기서 값의 변경이 확인되면 UPDATE 쿼리를 쓰기지연 SQL 저장소에 만들어둠.
            // 그리고 UPDATE 를 반영하고 커밋하게 됨.
            // 그냥 JPA 는 값을 바꾸면 트랜잭션이 커밋되는 시점에 변경을 반영하는구나라고 생각하면 됨.
            System.out.println("===============");

            entityTransaction.commit();
        } catch (Exception e) {
            entityTransaction.rollback();
        } finally {
            entityManager.close();
        }
        entityManagerFactory.close();

    }
}
