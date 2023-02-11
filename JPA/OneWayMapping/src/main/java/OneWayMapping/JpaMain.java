package OneWayMapping;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.Persistence;

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
            entityManager.persist(team);            // TODO 영속상태가 되기전에 PK 에 (id)가 세팅되고 영속상태가됨. (INSERT)

            // 회원 저장
            Member member = new Member();
            member.setUsername("member1");
            member.setTeam(team);                   // TODO 단방향 연관관계 설정. 참조 저장 (Member 에서 Team 을 참조할수있음. 하지만 Team 에서 Member 를 참조할 수 없음.)
            entityManager.persist(member);

            Member findMember = entityManager.find(Member.class, member.getId()); // TODO 라인 27 에 의해 영속화가 되었기 떄문에, 1차캐시에서 갖고옴. --> DB 에 Select 문이 나가지 않음.
            Long teamId = findMember.getTeam().getId();
            System.out.println(teamId);

            entityTransaction.commit();
        } catch (Exception e)  {
            entityTransaction.rollback();
        } finally {
            entityManager.close();
        }
        entityManagerFactory.close();
    }
}
