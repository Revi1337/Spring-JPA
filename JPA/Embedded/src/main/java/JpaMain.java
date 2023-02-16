import hellojpa.Address;
import hellojpa.Member;
import hellojpa.Period;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.Persistence;



public class JpaMain {

    public static void main(String... args) {
        EntityManagerFactory entityManagerFactory = Persistence.createEntityManagerFactory("hello");
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        EntityTransaction entityTransaction = entityManager.getTransaction();
        entityTransaction.begin();
        try {
            // TODO Embedded 타입의 사용
//            Member member = new Member();
//            member.setUsername("hello");
//            member.setHomeAddress(new Address("city", "street", "100"));
//            member.setWorkPeriod(new Period());
//            entityManager.persist(member);

            // TODO Embedded 주의사항 (값 타입과 불변객체)
//            Address address = new Address("city", "street", "100");
//            Member member1 = new Member();
//            member1.setUsername("member1");
//            member1.setHomeAddress(address); // 1. member1 과 member2 모두 같은 Address 를사용하고있음.
//            entityManager.persist(member1);
//
//            Member member2 = new Member();
//            member2.setUsername("member2");
//            member2.setHomeAddress(address); // 2. member1 과 member2 모두 같은 Address 를사용하고있음.
//            entityManager.persist(member2);
//            member1.getHomeAddress().setCity("newCity"); // 3. 나는 member1 의 City 만 바꾸고 싶은데, member2 의 City 까지 변경되고있음. (Update 쿼리가 두번나감 (Side Effect) - 같은 address 를 객체를 사용하고있어서 값이 공유되는 것임.)
//
//            Member member3 = new Member();  // 4. 이를 해결하기 위해서 member 3 은 기존의 address 를 넣고
//            member3.setUsername("member3");
//            member3.setHomeAddress(address);
//            entityManager.persist(member3);
//
//            Address copyAddress = new Address(address.getCity(), address.getStreet(), address.getZipcode()); // 5. 새로운 Address 객체를 만들어준 다음
//            Member member4 = new Member();
//            member4.setUsername("member4");
//            member4.setHomeAddress(copyAddress);    // 6. member4 에는 새로운 copyAddress 를 넣어주면 객체가 공유되지 않아
//            entityManager.persist(member4);
//            member3.getHomeAddress().setCity("legacyCity");    // 7. member3 의 City 를 바꾸어도 member1,2 에게만 영향이 있을 뿐, member4 에게는 영향을 미치지 않는다.
//            // 하지만 누군가가  copyAddress 를 한번더 사용하게되면 또 객체가 공유되기 때문에 굉장히 위험함

            // TODO 따라서 해결방법은 Address 를 아예 불변객체로 만드는것임. (생성자로만 생성할수있게하고, setter 를 없애거나, private 에서만 사용할수있게하는 것을 말함.)
            // TODO 불변이라는 작은 제약으로 부작용이라는 큰 재앙을 막을 수 있음.
            Address immutableAddress = new Address("city", "street", "10000");
            Member member1 = new Member();
            member1.setHomeAddress(immutableAddress);
            entityManager.persist(member1);
            Address immutableAddress2 = new Address("newDummy", immutableAddress.getStreet(), immutableAddress.getZipcode());
            member1.setHomeAddress(immutableAddress2); // Address 의 City 를 바꾸려면 통으로 새로운 Address 를 만들어어함. --> 불편하지만 공유참조를 막을 수 있음.

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
