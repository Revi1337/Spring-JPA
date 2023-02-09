package com.example.ex1hellojpa.hellojpa;


import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.Persistence;

public class Detach {
    public static void main(String[] args) {
        EntityManagerFactory entityManagerFactory = Persistence.createEntityManagerFactory("hello");
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        EntityTransaction entityTransaction = entityManager.getTransaction();
        entityTransaction.begin();
        try {
            Member member1 = entityManager.find(Member.class, 100L); // 1. 영속성컨텍스트의 1차캐시에 없어, DB 에 조회 후, 그 객체를 1차 캐시에 올림 ---> 영속화된 상태
//            entityManager.detach(member1);        // TODO 2. 영속성 컨텍스트에서 해당 객체를 분리시켜 준영속시킴. (이놈을 주석하면 select 는 한번 일어남.)
            Member member2 = entityManager.find(Member.class, 100L);    // 3. 준영속 시켰기 때문에, 영속성컨텍스트의 1차캐시에 없어, DB 에 다시 Select 문을 날리게됨.

            entityTransaction.commit();
        } catch (Exception e){
            entityTransaction.rollback();
        } finally {
            entityManager.close();
        }
        entityManagerFactory.close();
    }
}
