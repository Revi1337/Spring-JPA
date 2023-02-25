import hellojpa.Member;
import jakarta.persistence.*;
import org.hibernate.Hibernate;


public class JpaMain {
    public static void main(String[] args) {

        EntityManagerFactory entityManagerFactory = Persistence.createEntityManagerFactory("hello");
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        EntityTransaction entityTransaction = entityManager.getTransaction();
        entityTransaction.begin();
        try {
            Member member1 = new Member();
            member1.setUsername("hello");
            entityManager.persist(member1);

            Member member2 = new Member();
            member2.setUsername("hello");
            entityManager.persist(member2);

            // TODO find() 의 동작. (find() 롤 호출하는 시점에 select 쿼리가 나감.)
            // 쌓여있던 쿼리를 보내고 영속성 컨텍스트를 비우는 과정
//            entityManager.flush();
//            entityManager.clear();
//            Member findMember = entityManager.find(Member.class, member.getId());
//            System.out.println("findMember.id = " + findMember.getId());
//            System.out.println("findMember.getUsername = " + findMember.getUsername());

            // TODO getReference() 의 동작. (getReference() 를 호출하는 시점에 select 쿼리가 나가는 것이 아닌, 얻어온 값을 사용할때 select 쿼리가 나감.)
//            entityManager.flush();
//            entityManager.clear();
//            Member findMember = entityManager.getReference(Member.class, member.getId());   // 가짜 Member 를 갖고온것임. (껍데기는 같은데 안에는 비어있음, )
//            System.out.println("findMember = " + findMember.getClass());   // 이놈의 정체는 Member$HibernateProxy$z1dIvaoW --> hibernate 에서 강제로 만든 가짜 클래스 (이놈이 프록시클래스)
//            System.out.println("findMember.id = " + findMember.getId()); // 현재 시점에서 select 문이 나가지 않는 이유는 getReference() 에서 파라미터로 id 값을 넣어주었기때문임. --> 쿼리를 날릴 필요가 있음.
//            System.out.println("findMember.getUsername = " + findMember.getUsername()); // 이 시점에서 select 문이 나감. 그이유는 username() 은 DB 에 있기 때문임.

            // TODO (프록시의 특징) 프록시 객체는 원본 엔티티를 상속받음. 따라서 타입체크시 주의해야함. find() 로 Member 고 getReference() 로는 Proxy.
//            entityManager.flush();
//            entityManager.clear();
//            Member mem1 = entityManager.find(Member.class, member1.getId());
//            Member mem2 = entityManager.getReference(Member.class, member2.getId());
//            System.out.println("mem1 == mem2 : " + (mem1.getClass() == mem2.getClass()));
//            System.out.println("mem2's Parent is Member? : " + (mem2 instanceof Member));

            // TODO (프록시의 특징) 영속성 컨텍스트에 찾는 엔티티가 이미 있으면 em.getReference() 를 호출해도 실제엔티티 반환함. (동일한 트랜잭션에서 영속성 컨텍스트의 기능 중 동일성 보장을 위해 참조값을 그대로 유지되어야 하기 때문에, 프록시로 조회했으면 프록시로, 엔티티로 조회했으면 엔티티로 반환)
//            Member findMember = entityManager.getReference(Member.class, member1.getId());
//            System.out.println("findMember.class = " + findMember.getClass().getName());

            // TODO (프록시의 특징) 영속성 컨텍스트의 도움을 받을 수 없는 준영속 상태일때, 프록시를 초기호하면 문제발생 (이놈 ㅈㄴ 중요 - LazyInitializationException)
//            entityManager.flush();
//            entityManager.clear();
//            Member refMember = entityManager.getReference(Member.class, member1.getId());
//            System.out.println("refMember = " + refMember.getClass()); // Proxy
//            // entityManager.clear(); // Proxy 객체를 초기화 전에 영속성컨텍스트를 clear, detach, close 로 비우거나 닫으면 LazyInitializationException 발생 (주석치면서 확인해볼것)
//            refMember.getUsername(); // 이때 영속성컨텍스트의 도움을 받아 DB 에 쿼리를 날림을 통해, 실제객체를 불러오는 Proxy 객체의 초기화를 해야하는데, 영속성컨텍스트가 비워지면서 오류
                                      // could not initialize proxy(no-session) : 프록시객체를 초기화할 수 없음.

            // TODO 프록시 초기화 확인
//            entityManager.flush();
//            entityManager.clear();
//            Member refMember = entityManager.getReference(Member.class, member1.getId());
//            PersistenceUnitUtil persistenceUnitUtil = entityManagerFactory.getPersistenceUnitUtil();
//            System.out.println("Initialize? : " + persistenceUnitUtil.isLoaded(refMember));
//            refMember.getUsername(); // 프록시 객체 초기화 (쿼리가 이때 날라감)
//            System.out.println("Initialize? : " + persistenceUnitUtil.isLoaded(refMember));

            // TODO 프록시 강제 초기화 (JPA 표준에서는 강제 초기화가 없음. 약간 flush 랑 비슷)
            entityManager.flush();
            entityManager.clear();
            Member refMember = entityManager.getReference(Member.class, member1.getId());
            PersistenceUnitUtil persistenceUnitUtil = entityManagerFactory.getPersistenceUnitUtil();
            System.out.println("Initialize? : " + persistenceUnitUtil.isLoaded(refMember));
            Hibernate.initialize(refMember);    // 프록시 객체 강제 초기화 (refMember.getUsername () 과도 같은 역할)
            System.out.println("Initialize? : " + persistenceUnitUtil.isLoaded(refMember));

            entityTransaction.commit();
        } catch (Exception e) {
            entityTransaction.rollback();
            e.printStackTrace(); // LazyInitializationException 를 확인
        } finally {
            entityManager.close();
        }
        entityManagerFactory.close();

    }
}
