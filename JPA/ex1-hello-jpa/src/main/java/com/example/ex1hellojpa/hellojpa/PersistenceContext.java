package com.example.ex1hellojpa.hellojpa;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.Persistence;

public class PersistenceContext {

    public static void main(String[] args) {
        EntityManagerFactory entityManagerFactory = Persistence.createEntityManagerFactory("hello");
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        EntityTransaction entityTransaction = entityManager.getTransaction();
        entityTransaction.begin();
        try {
            // 비영속
            Member member = new Member();
            member.setId(100L);
            member.setName("HelloJPA");

            // 영속
            System.out.println("===== BEFORE =====");
            entityManager.persist(member);              // TODO 1. 영속성 컨텍스트에 저장 (영속화) - 영속상태가 된다해서 DB 에 쿼리가 바로 날라가는 것이 아님.
//            entityManager.detach(member); // 회원 엔티티를영속성 컨텍스트에서 분리, 준영속 상태
//            entityManager.remove(member); // 객체를 삭제한 상태 (삭제)
            System.out.println("===== AFTER =====");

            entityTransaction.commit();                 // TODO 2. 트랜잭션을 커밋하는 시점에 DB 에 쿼리가 날라가고 반영이됨.
        } catch (Exception e) {
            entityTransaction.rollback();
        } finally {
            entityManager.close();
        }
        entityManagerFactory.close();

    }
}
