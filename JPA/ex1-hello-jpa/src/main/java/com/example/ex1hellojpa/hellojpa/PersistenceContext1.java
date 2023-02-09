package com.example.ex1hellojpa.hellojpa;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.Persistence;

public class PersistenceContext1 {
    public static void main(String... args) {
        EntityManagerFactory entityManagerFactory = Persistence.createEntityManagerFactory("hello");
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        EntityTransaction entityTransaction = entityManager.getTransaction();
        entityTransaction.begin();
        try {
            // 비영속
            Member member = new Member();
            member.setId(101L);
            member.setName("HelloJPA");

            // 영속
            System.out.println("=== BEFORE ===");
            entityManager.persist(member);                  // DB에 저장하는 것이 아니라, 영속성 컨텍스트의 1차 캐시에 저장하는 것임. (영속화)
            System.out.println("=== AFTER ===");

            // TODO find()
            // (1차캐시에 없을 때) 먼저, 영속성컨텍스트의 1차캐시에서 값을 조회함. --> 1차 캐시에 없으면 DB 에 Select 문으로 조회함 --> 조회 후, 1차 캐시에저장 --> 1차 캐시에 저장되었기 때문에, 이후 find()부터는 1차캐시에서 찾아오기 때문에 DB 에 Select 문이 나가지 않음.
            // (1차 캐시에 있을 때) 먼저, 영속성컨텍스트의 1차캐시에서 값을 조회함. --> 1차 캐시에 있기 때문에 DB 에 Select 문으로 조회를 하지 않음.
            // 아래의 예는 이미 22 라인에서 persist() 를 통해 영속성컨텍스트의 1차캐시에 저장했기 때문에, DB 에 Select 문이 나가지 않음.
            Member member1 = entityManager.find(Member.class, 101L);
            System.out.println("findMember.id = " + member1.getId());
            System.out.println("findMember.name = " + member1.getName());

            entityTransaction.commit();
        } catch (Exception e) {
            entityTransaction.rollback();
        } finally {
            entityManager.close();
        }
        entityManagerFactory.close();

    }
}
