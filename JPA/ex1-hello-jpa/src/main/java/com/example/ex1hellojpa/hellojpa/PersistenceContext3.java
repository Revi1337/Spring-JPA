package com.example.ex1hellojpa.hellojpa;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.Persistence;

public class PersistenceContext3 {
    public static void main(String[] args) {
        EntityManagerFactory entityManagerFactory = Persistence.createEntityManagerFactory("hello");
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        EntityTransaction entityTransaction = entityManager.getTransaction();

        entityTransaction.begin();  // EntityManager 는 데이터 변경시 트랜잭션을 시작해야 한다.
        try {
            // 비영속
            Member memberA= new Member(150L, "A");
            Member memberB = new Member(160L, "B");

            // 영속
            // TODO 엔티티 등록 (트랜잭션을 지원하는 쓰기 지연)
            // TODO 영속성 컨텍스트 안에는 1차캐시도 있지만, 쓰기 지연 SQL 저장소가 있음.
            // 1. persist(memberA) 를 하면 memberA 가 1차캐시에 들어과 동시에, JPA 가 memberA 를 분석하고 INSERT 쿼리를 생성하여 쓰기 지연 SQL 저자소에 쌓아둠. (이떄까지 DB에 뭘 넣는것이 없음)
            // 2. persist(memberB) 를 하면 memberA 와 마찬가지로, 1차 캐시에 들어가고 INSERT 문이 쓰기 지연 SQL 저장소에 쌓임.
            entityManager.persist(memberA);
            entityManager.persist(memberB);
            System.out.println("================");         // commit 전이라 아무런 출력이 없음.
            // 3. 트랜잭션을 커밋하는 시점에, 쓰기 지연 SQL 저장소에 있던 쿼리들이 Flush 되면서 실제 DB 에 INSERT 쿼리가 날라가고 반영됨.
            entityTransaction.commit();
        } catch (Exception e) {
            entityTransaction.rollback();
        } finally {
            entityManager.close();
        }
        entityManagerFactory.close();
    }
}
