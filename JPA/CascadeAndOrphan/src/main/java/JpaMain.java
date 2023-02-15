import hellojpa.Child;
import hellojpa.Parent;
import jakarta.persistence.*;

public class JpaMain {

    public static void main(String... args) {
        EntityManagerFactory entityManagerFactory = Persistence.createEntityManagerFactory("hello");
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        EntityTransaction entityTransaction = entityManager.getTransaction();
        entityTransaction.begin();
        try {
            Child child1 = new Child();
            Child child2 = new Child();

            Parent parent = new Parent();
            parent.addChild(child1);
            parent.addChild(child2);

            // TODO 코드를 짤 때 Parent 중심으로 코드를 작성하려는데, 자꾸 child 가 보임.
            // TODO 여기서 생각할 수 있는 것은 parent 를 persist 할떄 자동으로 child 를 persist 해주는 방법이 없나 생각.. --> 이때 쓰는것이 CASCADE 임.
//            entityManager.persist(parent);
//            entityManager.persist(child1);
//            entityManager.persist(child2);

            // TODO CASCADE 옵션을 사용하면 parent 를 persist 할때 parent 와 연관된 child 를 자동으로 persist 해줌. (cascade = CascadeType.ALL 경우)
//            entityManager.persist(parent);

            // TODO 고아객체 제거 (orphanRemoval = true 경우)
            // TODO 참조가 제거된 엔티티는 다른곳에서 참조하지 않는 고아객체로 보고 삭제하는 기능임. (꼭 특정 엔티티가 개인소유할때만 사용해야함) CascadeType.REMOVE 와 비슷함
//            entityManager.persist(parent);
//            entityManager.persist(child1);
//            entityManager.persist(child2);
//            entityManager.flush();
//            entityManager.clear();
//            Parent findParent = entityManager.find(Parent.class, parent.getId());
//            entityManager.remove(findParent); // 부모를 삭제했으니 자식도 delete 됨.

            // TODO 개인 연습
            entityManager.persist(parent);
            entityManager.persist(child1);
            entityManager.persist(child2);

            entityManager.flush();
            entityManager.clear();

            Parent findParent = entityManager.find(Parent.class, parent.getId());
            findParent.getChildList().remove(0);    // (둘다 사용 시) 자식을삭제하면 자식만삭제됨.
            entityManager.remove(findParent);            // 둘중 하나, 혹은 둘다 사용했을떄 부모를 지우면 자식도 지워짐.

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
