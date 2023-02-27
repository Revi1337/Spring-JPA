import hellojpa.Member;
import hellojpa.Team;
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
            Team team = new Team();
            entityManager.persist(team);

            Member member1 = new Member();
            member1.setUsername("관리자1");
            member1.setTeam(team);
            entityManager.persist(member1);

            Member member2 = new Member();
            member2.setUsername("관리자2");
            member2.setTeam(team);
            entityManager.persist(member2);

            entityManager.flush();
            entityManager.clear();

            // TODO 경로표현식
            // --- 상태필드 경로 : 경로 탐색의 끝, 탐색 X ---
//            String query = "select m.username from Member as m";
//            List<String> resultList1 = entityManager.createQuery(query, String.class).getResultList();
//            for (String s : resultList1) {
//                System.out.println("s = " + s);
//            }
            // --- 단일 값 연관 경로 : 묵시적 내부 조인 발생, 탐색 O --- (묵시적으로 join 문이 나가서 성능에 영향을 미침)
//            String query = "select m.team from Member as m";
//            String query = "select t from Member as m inner join m.team t"; // 아래와같이 JOIN 이 나가는 쿼리라고 명시적으로 선언해주는 것이 좋음 (쿼리는 동일함.)
//            List<Team> resultList = entityManager.createQuery(query, Team.class).getResultList();
//            for (Team team : resultList) {
//                System.out.println("team = " + team);
//            }
            // --- 컬렉션 값 연관 경로 : 묵시적 내부 조인 발생, 탐색 X --- (t.members. 뒤에 탐색 불가. 사용할수 있는것은 .size 정도)
            String query = "select m.username from Team as t inner join t.members m"; // 더 탐색하려면 FROM 절에서 명시적 조인을 통해 별칭을 얻고, 별칭을 통해 탐색 가능
            List<String> resultList = entityManager.createQuery(query, String.class).getResultList();
            for (String s : resultList) {
                System.out.println("s = " + s);
            }


            entityTransaction.commit();
        } catch (Exception e) {
            entityTransaction.rollback();
            e.printStackTrace();
        } finally {
            entityManager.close();
        }
        entityManagerFactory.close();
    }
}
