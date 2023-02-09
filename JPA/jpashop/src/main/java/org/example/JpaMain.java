package org.example;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.Persistence;
import org.example.domain.Member;
import org.example.domain.Order;


public class JpaMain {

    public static void main(String[] args) {

        EntityManagerFactory entityManagerFactory = Persistence.createEntityManagerFactory("hello");
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        EntityTransaction entityTransaction = entityManager.getTransaction();
        entityTransaction.begin();
        try {
            // TODO 객체 지향적이지 않음. 메서드체인이 일어나지않음.
            // 현재 방식은 객체 설계를 테이블 설계에 맞춘 방식
            // 테이블의 외래키를 객체에 그대로 가져옴
            // 객체 그래프 탐색이 불가능
            // 참조가 없으므로 UML 도 잘못됨.
            Order order = entityManager.find(Order.class, 1L);
            Long memberId = order.getMemberId();
            Member member = entityManager.find(Member.class, memberId);

            entityTransaction.commit();
        } catch (Exception e) {
            entityTransaction.rollback();
        } finally {
            entityManager.close();
        }
        entityManagerFactory.close();
    }
}

