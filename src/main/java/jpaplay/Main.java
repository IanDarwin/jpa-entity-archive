package jpaplay;

import java.time.*;
import java.util.List;

import jakarta.persistence.*;

public class Main {
	public static void main(String[] args) throws Exception {
		EntityManagerFactory emf = Persistence.createEntityManagerFactory("test");
		EntityManager em = emf.createEntityManager();
		try {
			run(em);
		} finally {
			if (em.getTransaction().isActive()) {
				em.getTransaction().rollback();
			}
			em.close();
			emf.close();
		}
	}

	static void run(EntityManager em) {
		var tx = em.getTransaction();
		tx.begin();
		nukeDataForTest(em);
		tx.commit();

		Member m = new Member();
		m.name = "Old Fogey";
		m.address = "Home on the Range";
		Member m2 = new Member();
		m2.name = "Baby Bunny";
		m2.address = "London, Ontario";

		tx = em.getTransaction();
		tx.begin();
		em.persist(m);
		em.persist(m2);
		tx.commit();

		tx = em.getTransaction();
		tx.begin();
		delete(m, em);
		tx.commit();

		tx = em.getTransaction();
		tx.begin();
		listMembers(em);
		var l2 = listArchived(em);
		tx.commit();

		tx.begin();
		unDelete(l2.get(0), em);
		tx.commit();

		listMembers(em);
	}

	static void nukeDataForTest(EntityManager em) {
		var delM = em.createQuery("DELETE FROM Member");
		int retM = delM.executeUpdate();
		var delMA = em.createQuery("DELETE FROM MemberArchived");
		int retMA = delMA.executeUpdate();
		System.out.printf("Pre-cleanup deleted %d Member and %d MemberArchived\n", retM, retMA);
	}

	static List<Member> listMembers(EntityManager em) {
		var q1 = em.createQuery("from Member", Member.class);
		var l1 = q1.getResultList();
		System.out.println("Query for Member found " + l1.size());
		l1.forEach(System.out::println);
		return l1;
	}

	static List<MemberArchived> listArchived(EntityManager em) {
		var q2 = em.createQuery("from MemberArchived", MemberArchived.class);
		var l2 = q2.getResultList();
		System.out.println("Query for MemberArchived found " + l2.size());
		l2.forEach(System.out::println);
		return l2;
	}

	/** "Delete" a Member instance by converting it to MemberArchived.
	 */
	static void delete(Member m, EntityManager em) {
		System.out.println("Archiving " + m);
		em.flush();		// send any pending changes to m before we go around JPA
		var query = em.createNativeQuery(
			"update member set type = 'MemberArchived', archivedDate = ? where id = ?");
		query.setParameter(1, LocalDate.now());
		query.setParameter(2, m.id);
		int ret = query.executeUpdate();
		System.out.println("delete: Native update returned " + ret);
		// Changed the type w/o telling JPA, so evict it.
		em.detach(m);
	}

	/** Undelete the Member */
	static void unDelete(MemberArchived m, EntityManager em) {
		System.out.println("Un-archiving " + m);
		em.flush();
		var query = em.createNativeQuery("update member set type = 'Member', archivedDate = null where id = ?");
		query.setParameter(1, m.id);
		int ret = query.executeUpdate();
		System.out.println("unDelete: Native query returned " + ret);
		em.detach(m);	// same reason as in delete(): now it is really a Member again
	}
}
