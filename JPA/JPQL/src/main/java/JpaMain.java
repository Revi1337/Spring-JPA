import hellojpa.Address;
import hellojpa.Member;
import hellojpa.Team;
import hellojpa.dto.MemberDto;
import jakarta.persistence.*;

import java.util.List;


public class JpaMain {
    public static void main(String[] args) {
        EntityManagerFactory entityManagerFactory = Persistence.createEntityManagerFactory("hello");
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        EntityTransaction entityTransaction = entityManager.getTransaction();
        entityTransaction.begin();
        try {
            Team team = new Team();
            team.setName("teamA");
            entityManager.persist(team);

            Member member = new Member();
            member.setUsername("member1");
            member.setAge(10);
            member.setTeam(team);
            entityManager.persist(member);

            // TODO TypeQuery, Query
//            TypedQuery<Member> query1 = entityManager.createQuery("select m from Member as m", Member.class); // 반환타입이 명확. (TypedQuery 사용 가능, 두번쨰 파라미터에는 반환할 타입을 명시해야함.)
//            Query query2 = entityManager.createQuery("select m.username, m.age from Member as m"); // 반환타입이 명확하지 않음. (m.username 은 String 이고, m.age 은 int 이기 때문에 TypedQuery 사용 불가. Query 를 사용해야함.)
//            TypedQuery<String> query3 = entityManager.createQuery("select m.username from Member as m", String.class); // TypedQuery 를 사용하려면 리턴타입을 m.username 인 String 을할것인지, m.age 인 int 를 할것인지 argv[2] 에 명시해주어야함.

            // TODO 결과조회 API
            // 여러개 (Collection 일 경우) --> 결과가 없으면 빈 리스트를 반환 (NPE 에 안전)
//            TypedQuery<Member> query1 = entityManager.createQuery("select m from Member as m", Member.class);
//            List<Member> resultList = query1.getResultList();
//            for (Member member1 : resultList) {
//                System.out.println("member1 = " + member1);
//            }
            // 한개일 경우 (Single 일 경우) -> 결과가없으면 오류가터지기때문에 try catch 필요 -->  결과가 두개여도안되고, 없어서도 안됨. 즉, 결과로 하나가 무조건 있어야함  --> 굉장히 별로
//            TypedQuery<Member> query = entityManager.createQuery("select m from Member as m where m.id = 1L", Member.class);
//            Member singleResult = query.getSingleResult();
//            System.out.println("singleResult = " + singleResult);

            // TODO 파라미터 바인딩 (이름 혹은 위치 기준)
            // 이름 기준 --> 체이닝으로 사용
//            Member singleResult = entityManager.createQuery("select m from Member as m where m.username=:username", Member.class)
//                    .setParameter("username", "member1")      // argv[1] 으로 createQuery() 에서 바인딩하고있는 파라미터(:username)를 명시해야함.
//                    .getSingleResult();                       // 보통 체이닝으로 묶음.
//            System.out.println("singleResult.username = " + singleResult.getUsername());
            // 위치기반 --> 사용하지않는 것이 좋음 (중간에 파라미터가 한개 추가되면 순서가 다 밀리기때문임.)
//            Member singleResult1 = entityManager.createQuery("select m from Member as m where m.username=?1", Member.class)
//                    .setParameter(1, "member1")
//                    .getSingleResult();
//            System.out.println("singleResult1 = " + singleResult1.getUsername());

            // TODO 프로젝션
            // --- 엔티티 프로젝션 (1) ---
//            entityManager.flush();
//            entityManager.clear();
//            List<Member> resultList = entityManager.createQuery("select m from Member as m", Member.class)
//                    .getResultList();
//            Member findMember = resultList.get(0);
//            findMember.setAge(20);
            // --- 엔티티 프로젝션 (2) ---
//            entityManager.flush();
//            entityManager.clear();
//            List<Team> resultList = entityManager.createQuery("select m.team from Member as m", Team.class)
//                    .getResultList(); // 방법 1.inner join(그냥 join) 쿼리가 나감.
//            List<Team> resultList1 = entityManager.createQuery("select t from Member as m inner join m.team t", Team.class)
//                    .getResultList(); // 방법 2. 방법 1과 나가는 쿼리는 동일함. (이런방식을 쓰는것을 권장함. 그렇게해야 DB 에 조인이 나가는 쿼리 예측할 수 있음.)
            // --- 임베디드 타입 프로젝션 ---
//            entityManager.flush();
//            entityManager.clear();
//            List<Address> resultList = entityManager.createQuery("select o.address from Order as o", Address.class)
//                    .getResultList();
            // --- 스칼라 타입 프로젝션 --- (그냥 일반 sql 의 select 프로제겻과 동일하다 보면됨.)
//            entityManager.flush();
//            entityManager.clear();
//            entityManager.createQuery("select distinct m.username, m.age from Member as m").getResultList();

            // TODO 프로젝션 여러 값 조회 (여기서 고민인것은 반환타입이 두개일 경우임. Ex. select m.username, m.age from Member as m)
            // --- 깔끔하지 않은 방법 ---
//            entityManager.flush();
//            entityManager.clear();
//            List<Object[]> resultList = entityManager.createQuery("select m.username, m.age from Member as m").getResultList();
//            Object[] result = resultList.get(0);
//            System.out.println("username = " + result[0]);
//            System.out.println("age = " + result[1]);
            // --- 제일깔끔한 방법 --- (new 명령어로 조회하는 것. 단순 값을 DTO 로 바로 조회, 패키지 명을 포함한 전체 클래스명을 입력해야함, 순서와 타입이 일치하는 생성자 필요.)
//            entityManager.flush();
//            entityManager.clear();
//            List<MemberDto> resultList = entityManager.createQuery("select new hellojpa.dto.MemberDto(m.username, m.age) from Member m", MemberDto.class)
//                    .getResultList();
//            MemberDto memberDto = resultList.get(0);
//            System.out.println("memberDto = " + memberDto.getUsername());
//            System.out.println("memberDto = " + memberDto.getAge());

            // TODO 페이징 API
//            for (int i = 2; i < 100; i++) {
//                Member member1 = new Member();
//                member1.setUsername("member" + i);
//                member1.setAge(i);
//                entityManager.persist(member1);
//            }
//            entityManager.flush();
//            entityManager.clear();
//            List<Member> resultList = entityManager.createQuery("select m from Member m order by m.age desc", Member.class)
//                    .setFirstResult(1) // 나는 0번쨰부터
//                    .setMaxResults(10) // 10개 가져올거야
//                    .getResultList();
//            System.out.println("resultList.size = " + resultList.size());
//            for (Member member1 : resultList) {
//                System.out.println("member1 = " + member1);
//            }

            // TODO Join
            // --- inner join --- (inner 는 생략 가능)
//            entityManager.flush();
//            entityManager.clear();
//            entityManager.createQuery("select m from Member as m inner join m.team t").getResultList(); // inner join 으로 t 를 가져왔기때문에, t 를 사용해짐.
            // --- left outer join --- (outer 는 생략 가능)
//            entityManager.flush();
//            entityManager.clear();
//            entityManager.createQuery("select m from Member as m left join m.team t", Member.class).getResultList();
            // --- cross join --- (일명 세타조인 = 막조인)
//            entityManager.flush();
//            entityManager.clear();
//            String query = "select m from Member as m, Team t where m.username = t.name";
//            List<Member> resultList = entityManager.createQuery(query, Member.class).getResultList();
//            System.out.println("resultList = " + resultList.size());
            // --- ON join ---
            entityManager.flush();
            entityManager.clear();
            String query1 = "select m from Member as m left outer join m.team t on t.name = 'teamA'"; // 조인 대상 필터링
            List<Member> resultList = entityManager.createQuery(query1, Member.class).getResultList();
            entityManager.flush();
            entityManager.clear();
            String query2 = "select m from Member as m left outer join Team t on m.username = t.name"; // 연관관계가 없는 엔티티 외부 조인
            List<Member> resultList2 = entityManager.createQuery(query2, Member.class).getResultList();



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
