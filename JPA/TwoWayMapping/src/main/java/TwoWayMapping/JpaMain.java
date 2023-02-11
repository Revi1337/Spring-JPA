package TwoWayMapping;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.Persistence;

import java.util.List;

public class JpaMain {

    public static void main(String[] args) {

        EntityManagerFactory entityManagerFactory = Persistence.createEntityManagerFactory("hello");
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        EntityTransaction entityTransaction = entityManager.getTransaction();
        entityTransaction.begin();

        try {
            // 팀저장
            Team team = new Team();
            team.setName("TeamA");
            entityManager.persist(team);

            // 회원 저장
            Member member = new Member();
            member.setUsername("member1");
            member.setTeam(team);
            entityManager.persist(member);

            entityManager.flush();
            entityManager.clear();

            Member findMember = entityManager.find(Member.class, member.getId());
            List<Member> members = findMember.getTeam().getMembers();
            for (Member member1 : members) {
                System.out.println("member = " + member1.getUsername());
            }


            entityTransaction.commit();
        } catch (Exception e)  {
            entityTransaction.rollback();
        } finally {
            entityManager.close();
        }
        entityManagerFactory.close();
    }
}
