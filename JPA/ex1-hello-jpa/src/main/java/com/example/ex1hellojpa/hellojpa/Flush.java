package com.example.ex1hellojpa.hellojpa;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.Persistence;

public class Flush {

    public static void main(String... args) {

        EntityManagerFactory entityManagerFactory = Persistence.createEntityManagerFactory("hello");
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        EntityTransaction entityTransaction = entityManager.getTransaction();
        entityTransaction.begin();

        try {
            // 영속
            Member member = new Member(200L, "member200");
            entityManager.persist(member);
            entityManager.flush();  // 강제로 flush 호출해서 INSERT 쿼리를 DB에 즉시 날릴 수 있음. (1차 캐시를 지우고하는것이 아니라, 영속성 컨텍스트에 있는 "쓰기지연 SQL 저장소" 의 변화등을 DB 에 반영하는 것임. )
            System.out.println("========================"); // flush() 를 하면 === 전에 실행, flush() 를 주석치면 === 후에 실행. (증명)
            entityTransaction.commit();
        } catch (Exception e) {
            entityTransaction.rollback();
        } finally {
            entityManager.close();
        }
        entityManagerFactory.close();
    }

}
