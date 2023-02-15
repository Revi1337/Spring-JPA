import hellojpa.Member;
import hellojpa.Team;
import jakarta.persistence.*;

import java.util.List;

public class JpaMain {

    public static void main(String... args) {
        EntityManagerFactory entityManagerFactory = Persistence.createEntityManagerFactory("hello");
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        EntityTransaction entityTransaction = entityManager.getTransaction();
        entityTransaction.begin();
        try {
            Team team = new Team();
            team.setName("teamA");
            entityManager.persist(team);

            Team teamB = new Team();
            teamB.setName("teamA");
            entityManager.persist(teamB);

            Member member1 = new Member();
            member1.setUsername("member1");
            member1.setTeam(team);
            entityManager.persist(member1);

            Member member2 = new Member();
            member2.setUsername("member2");
            member2.setTeam(teamB);
            entityManager.persist(member2);

            entityManager.flush();
            entityManager.clear();

            // TODO FetchType.LAZY 의 동작 (지연로딩 - 연관된객체를 Proxy 로 갖고옴)
//            Member findMember = entityManager.find(Member.class, member1.getId()); // Member 를 조회할때는 Member 가져오고 Team 은 프록시로 가져옴.
//            System.out.println("findMember = " + findMember.getTeam().getClass()); // 결과로 Team 이 아닌 Proxy 객체가 출력됨 (FetchType.Lazy 를 걸어주었기 때문)
//            System.out.println("=================");
//            findMember.getTeam().getName(); // Team 의 속성을 사용하는 시점에 Proxy 객체가 초기화되면서 DB 에서 값을 가져옴 (실제 Team 을 사용하는 시점에 쿼리가 나감)
//            System.out.println("=================");

            // TODO FetchType.EAGER 의 동작 (즉시로딩 - 연관돤객체를 일반객체로 갖고옴)
//            Member findMember = entityManager.find(Member.class, member1.getId()); // Member 를 조회하는 시점에 Team 까지 같이 조인해서 싹 다 한번에 갖고옴.
//            System.out.println("findMember = " + findMember.getTeam().getClass()); // 결과로 진짜 Team 이 출력됨. Proxy 객체가 아니기때문에 초기화라는 개념이 없음.
//            System.out.println("=================");
//            System.out.println("teamName = " + findMember.getTeam().getName()); // 이미 find() 로 Member 를 가져오는 시점에서 Team 까지 조인해서 쫙 다 가져왔기 때문에 이시점에서는 아무일도 일어나지 않음. (그냥 이미 가져온 값을 출력하는것임.)
//            System.out.println("=================");

            // TODO 즉시로딩읠 단점 (JPQL 에서 N+1 문제를 일으킴)
            // JPQL 은 작성한 쿼리문이 그대로 SQL 로 번역되기 때문에 당연히 Member 만 조회하게됨. 근데? Member 를 가져왔더니 Team 이 즉시로딩인(EAGER 타입이어서), Team 의 개수만큼을 갖고오기 위한 쿼리가 별도로 나감.
            // JPQL 의 N + 1 쿼리의 해결방법으로는 모든연관관계를 LAZY 로 잡으면됨. 그러면 Team 을 사용하기전까진 Team 조회 쿼리는 안나감.
            // 또한, N + 1 쿼리의 해결방법으로는 모든연관관계를 LAZY 로 잡되, fetch join 을 사용하는것임. (지금은 몰라도됨.)
//            List<Member> members = entityManager.createQuery("select m from Member m", Member.class).getResultList(); // 쿼리가  Team 의 개수 N개 만큼 더나감. (1 이 작성한 쿼리임)
            List<Member> members = entityManager.createQuery("select m from Member m join fetch m.team", Member.class).getResultList(); // 해결방법 fetch join (지금은 몰라도됨.)


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
