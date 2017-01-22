

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;
import javax.persistence.Query;

import model.User;

public class TestMain {
	public static void main(String[] args) {
        final EntityManagerFactory emf = Persistence.createEntityManagerFactory("ogm-neo4j");
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();
        tx.begin();
        User person = new User("test");
//        em.persist(person);
        em.merge(person);
        tx.commit();
        em.clear();
        em.close();
        emf.close();
	}
}
