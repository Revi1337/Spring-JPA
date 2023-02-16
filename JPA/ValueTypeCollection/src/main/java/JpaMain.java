import hellojpa.Address;
import hellojpa.AddressEntity;
import hellojpa.Member;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.Persistence;

import java.util.List;
import java.util.Set;

public class JpaMain {
    public static void main(String... args) {
        EntityManagerFactory entityManagerFactory = Persistence.createEntityManagerFactory("hello");
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        EntityTransaction entityTransaction = entityManager.getTransaction();
        entityTransaction.begin();
        try {
            // TODO 값타입컬렉션 저장 예제 - (값타입 컬렉션의 LifeCycle 은 Member 에 의존함)
//            Member member = new Member();
//            member.setUsername("member1");
//            member.setHomeAddress(new Address("homeCity", "street", "10000")); // private Address homeAddress 에 해당
//            // private Set<String> favoriteFoods = new HashSet<>(); 에 해당
//            member.getFavoriteFoods().add("치킨");    // 다른 테이블에도 불구하고 Member 가 저장될때 같이 저장됨. (LifeCycle 이 Member 에 의존함.)
//            member.getFavoriteFoods().add("족발");
//            member.getFavoriteFoods().add("피자");
//            // private List<Address> addressHistory = new ArrayList<>(); 에 해당
//            member.getAddressHistory().add(new Address("old1", "street", "10000")); // 다른 테이블에도 불구하고 Member 가 저장될때 같이 저장됨. (LifeCycle 이 Member 에 의존함.)
//            member.getAddressHistory().add(new Address("old2", "street", "10000"));
//            // 저장
//            entityManager.persist(member);
//            entityManager.flush();
//            entityManager.clear();
//            System.out.println("================= START =================");
//            Member findMember = entityManager.find(Member.class, member.getId()); // Member 만 갖고옴. 즉, 값타입컬렉션들은 디폴트가 지연로딩이라는것임.
//            List<Address> addressHistory = findMember.getAddressHistory();
//            for (Address address : addressHistory) {
//                System.out.println("address = " + address.getCity());
//            }

            // TODO 값타입컬렉션 수정 예제 - (값타입 컬렉션의 LifeCycle 은 Member 에 의존함)
            Member member = new Member();
            member.setUsername("revi1337");
            member.setHomeAddress(new Address("Dummy", "Dummy", "Dummy"));
            member.getFavoriteFoods().add("치킨");
            member.getFavoriteFoods().add("피자");
            member.getFavoriteFoods().add("THREE");
            member.getAddressHistory().add(new AddressEntity("old1", "street", "10000"));
            member.getAddressHistory().add(new AddressEntity("old2", "street", "10000"));
            entityManager.persist(member);
            entityManager.flush();
            entityManager.clear();
            // 수정 시작
            System.out.println("======== START ========");
            Member findMember = entityManager.find(Member.class, member.getId());
            Address address = findMember.getHomeAddress();
            findMember.setHomeAddress(new Address("newCity", address.getStreet(), address.getZipcode())); // --> 이렇게 새롭게 인스턴스 Address 를 깔아끼워야함
            // 치킨 -> 피자 변경 : Set<String> 에서 String 자체가 값타입이기 떄문에 삭제하고 새로 넣어야함.
//            findMember.getFavoriteFoods().remove("치킨");
//            findMember.getFavoriteFoods().add("한식");
            findMember.getAddressHistory().remove(new AddressEntity("old1", "street", "10000"));
            findMember.getAddressHistory().add(new AddressEntity("newCity1", "street", "10000"));

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
