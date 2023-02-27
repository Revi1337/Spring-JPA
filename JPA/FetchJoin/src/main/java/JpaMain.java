import hellojpa.Member;
import hellojpa.Team;
import jakarta.persistence.*;

import java.util.List;

public class JpaMain {

    public static void main(String[] args) {
        EntityManagerFactory entityManagerFactory = Persistence.createEntityManagerFactory("hello");
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        EntityTransaction entityTransaction = entityManager.getTransaction();
        entityTransaction.begin();
        try {
            Team teamA = new Team();
            teamA.setName("팀A");
            entityManager.persist(teamA);

            Team teamB = new Team();
            teamB.setName("팀B");
            entityManager.persist(teamB);

            Member member1 = new Member();
            member1.setUsername("회원1");
            member1.setAge(0);
            member1.setTeam(teamA);
            entityManager.persist(member1);

            Member member2 = new Member();
            member2.setUsername("회원2");
            member2.setAge(0);
            member2.setTeam(teamA);
            entityManager.persist(member2);

            Member member3 = new Member();
            member3.setUsername("회원3");
            member3.setAge(0);
            member3.setTeam(teamB);
            entityManager.persist(member3);

            // TODO fetch join 사용하기 전 --> N + 1 문제
//            entityManager.flush();
//            entityManager.clear();
//            String query = "select m from Member as m";
//            List<Member> resultList =   entityManager.createQuery(query, Member.class).getResultList();
//            for (Member member : resultList) {
//                System.out.println("member = " + member.getUsername() + ", " + member.getTeam().getName());
//                // 회원1, 팀A (SQL) (영속성컨텍스트(1차캐시)에 팀 A 가 없기떄문에 SQL 쿼리문이 나감. --> 팀 A 를 DB 에서 갖고와서 영속성컨텍스트(1차캐시)에 저장)
//                // 회원2, 팀A (1차캐시) (영속성컨텍스트(1차캐시)에 팀 A 가 있으니까, 쿼리문이나가지않고, 영속성컨텍스트(1차캐시) 에서 갖고옴.)
//                // 회원3, 팀B (SQL) (영속성컨텍스트(1차캐시)에 팀 B 가 없기때문에 SQL 쿼리문이 나감. --> 팀 B 를 DB 에서 갖고와서 영속성컨텍스트(1차캐시)에 저장)
//            }
            // TODO @ManyToOne fetch join 을 사용했을 때 --> N + 1 문제 해결
//            entityManager.flush();
//            entityManager.clear();
//            String query = "select m from Member as m inner join fetch m.team"; // team 까지 조인해서 한번의 쿼리로 다 갖고오겠다는 의미임. (지연로딩없이 깔끔하게 해결)
//            List<Member> result = entityManager.createQuery(query, Member.class).getResultList(); // 이 result 에 담기는 시점에서 이미, Team 은 Proxy 객체아닌, 실제 엔티티가 담겨있음.
//            for (Member member : result) {
//                System.out.println("member = " + member.getUsername() + ", " + member.getTeam().getName());
//            }
            // TODO @OneToMany fetch join 을 사용했을 때 --> N + 1 문제 해결
//            entityManager.flush();
//            entityManager.clear();
//            String query = "select t from Team as t join fetch t.members";
//            List<Team> resultList = entityManager.createQuery(query, Team.class).getResultList();
//            for (Team team : resultList) {
//                System.out.println("team = " + team.getName() + " | members=" + team.getMembers().size());
//                for (Member member : team.getMembers()) {
//                    System.out.println("-->  member = " + member);
//                }
//            }

            // TODO 엔티티 직접사용
            // --- PK 값 --- (member 를 파라미터 바인딩해도 member 의 id 값으로 조회됨. PK)
//            entityManager.flush();
//            entityManager.clear();
//            String query = "select m from Member as m where m = :member";
//            Member singleResult = entityManager.createQuery(query, Member.class).setParameter("member", member2).getSingleResult();
//            System.out.println("singleResult = " + singleResult);
            // --- FK 값 --- (team 을 파라미터 바인딩해도 team 의 id 값으로 조회됨. FK )
//            entityManager.flush();
//            entityManager.clear();
//            String query1 = "select m from Member as m where m.team = :team";
//            List<Member> resultList = entityManager.createQuery(query1, Member.class).setParameter("team", teamA).getResultList();
//            for (Member member : resultList) {
//                System.out.println("member = " + member);
//            }

            // TODO Named 쿼리 --> 나중에 Data JPA 에서  Navtive Query 를사용하기떄문에 중요하진않음.
//            entityManager.flush();
//            entityManager.clear();
//            List<Member> resultList = entityManager.createNamedQuery("Member.findByUsername", Member.class)
//                    .setParameter("username", "회원1")
//                    .getResultList();
//            for (Member member : resultList) {
//                System.out.println("member = " + member);
//            }

            // TODO 벌크 연산 (Flush 자동 호출 - Flush 는 영속성 컨텍스트에 있는 것을 DB 에 반영하는것이지, 컨텍스트를 비우는것이 아님. 비우는것은 clear() ㅇㅇ )
            int resultCount = entityManager.createQuery("update Member m set m.age = 20").executeUpdate(); // 벌크연산의 리턴값으로는 영향을 받은 row 의 개수를 출력함.
            Member res = entityManager.find(Member.class, member1.getId());
            System.out.println("res = " + res.getAge());
            // 벌크연산을 통해 age 를 모두 20 으로 업데이트했지만, .getAge() 의 결과로는 0 이 나옴. 이유는 아래와같음
            // 1. 처음에 age 를 모두 0 으로 셋팅                                                                                        (DB 의 age : 0 , 컨텍스트 의 age : 0)
            // 2. 벌크연산을 통해 flush() 를 호출하여 DB 의 age 만 20 으로 업데이트. (벌크연산은 컨텍스트를 무시하고 바로 DB 에 update 를 떄림 )    (DB 의 age : 20 , 컨텍스트의 age : 0 )
            // 3. DB 의 age 는 20 이지만, find() 시점의 Member 는 영속성컨텍스트에 아직 남아있기때문에 반영되지 않은 age : 0 이 출력되는것임.
            // 해결방법으로는 벌크 연산 후에는 컨텍스트를 clear() 로 비워줘야 함.

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
