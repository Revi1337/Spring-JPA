package com.example.ex1hellojpa.hellojpa;

import jakarta.persistence.*;

import java.util.List;

public class JpaMain {
    public static void main(String[] args) {
        EntityManagerFactory entityManagerFactory = Persistence.createEntityManagerFactory("hello"); // EntityManagerFactory 는 애플리케이션 로딩 시점에 딱 한개만 만들어야 함.
        EntityManager entityManager = entityManagerFactory.createEntityManager(); // 트랜잭션 단위 (고객의 행위) 마다 EntityManager 를 꼭 만들어주어야함.

        // 트랜잭션시작 (JPA 에선 트랜잭션 단위가 굉장히 중요함. 모든 데이터 변경하는 작업은 꼭 트랜잭션 안에서 작업해야함.)
        EntityTransaction entityTransaction = entityManager.getTransaction();
        entityTransaction.begin();
        try {
            // TODO INSERT
//            Member member = new Member();
//            member.setId(2L);
//            member.setName("HelloB");
//            entityManager.persist(member);
//            entityTransaction.commit();                               // 끝 DB 에 커밋 (트랜잭션 종료)

            // TODO DELETE
//            Member member = entityManager.find(Member.class, 1L);     // 조회 select
//            entityManager.remove(member);
//            entityTransaction.commit();

            // TODO UPDATE
//            Member member = entityManager.find(Member.class, 1L);
//            member.setName("HelloJPA");
//            entityTransaction.commit();                               // JPA 는 변경이 됬는지 안됬는지 트랜잭션이 커밋하는 시점에 체크함. 그래서 persist 를 하지 않아도 자동으로 update 쿼리를 날리게되는 것임.

            // TODO JPQL
            List<Member> result = entityManager.createQuery("select m from Member as m", Member.class) // 테이블말고 Member 객체에 쿼리를 날린다고 보면 됨. (페이징처리에 굉장히 유리)
                    .setFirstResult(5)                                  // LIMIT 절로 끊어옴 (각 DB 의 고유특성인 방언에 상관없이 알아서 해석됨. Mysql: LIMIT, Oracle: RowNum)
                    .setMaxResults(8)
                    .getResultList();
            for (Member member : result) {
                System.out.println("member.name = " + member.getName());
            }
            entityTransaction.commit();
        } catch (Exception e) {
            entityTransaction.rollback();                               // 끝 롤백 (오류나서 되돌리는 것.)
        } finally {
            entityManager.close();                                      // EntityManager 는 내부적으로 DB 커넥션을 물고 동작하기때문에 하나의 사용을하고 닫아주어야한다.
        }
        entityManagerFactory.close();
    }
}
