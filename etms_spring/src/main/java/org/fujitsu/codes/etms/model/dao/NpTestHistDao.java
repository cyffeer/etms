package org.fujitsu.codes.etms.model.dao;

import java.util.List;
import java.util.Optional;

import org.fujitsu.codes.etms.model.data.NpTestHist;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.springframework.stereotype.Repository;

@Repository
public class NpTestHistDao {

    private final SessionFactory sessionFactory;

    public NpTestHistDao(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    public NpTestHist save(NpTestHist npTestHist) {
        Transaction tx = null;
        try (Session session = sessionFactory.openSession()) {
            tx = session.beginTransaction();
            session.persist(npTestHist);
            tx.commit();
            return npTestHist;
        } catch (RuntimeException ex) {
            rollback(tx);
            throw ex;
        }
    }

    public Optional<NpTestHist> findById(Long npTestHistId) {
        try (Session session = sessionFactory.openSession()) {
            return Optional.ofNullable(session.find(NpTestHist.class, npTestHistId));
        }
    }

    public List<NpTestHist> findAll() {
        try (Session session = sessionFactory.openSession()) {
            return session.createQuery(
                    "from NpTestHist n order by n.npTestHistId desc",
                    NpTestHist.class
            ).getResultList();
        }
    }

    public Optional<NpTestHist> update(Long npTestHistId, NpTestHist source) {
        Transaction tx = null;
        try (Session session = sessionFactory.openSession()) {
            tx = session.beginTransaction();

            NpTestHist target = session.find(NpTestHist.class, npTestHistId);
            if (target == null) {
                tx.commit();
                return Optional.empty();
            }

            target.setNpLvlInfoCode(source.getNpLvlInfoCode());
            target.setTestDate(source.getTestDate());
            target.setTestCenter(source.getTestCenter());
            target.setTestLevel(source.getTestLevel());
            target.setScore(source.getScore());
            target.setPassed(source.getPassed());
            target.setRemarks(source.getRemarks());
            target.setUpdatedAt(source.getUpdatedAt());

            session.merge(target);
            tx.commit();
            return Optional.of(target);
        } catch (RuntimeException ex) {
            rollback(tx);
            throw ex;
        }
    }

    public boolean deleteById(Long npTestHistId) {
        Transaction tx = null;
        try (Session session = sessionFactory.openSession()) {
            tx = session.beginTransaction();

            NpTestHist target = session.find(NpTestHist.class, npTestHistId);
            if (target == null) {
                tx.commit();
                return false;
            }

            session.remove(target);
            tx.commit();
            return true;
        } catch (RuntimeException ex) {
            rollback(tx);
            throw ex;
        }
    }

    private void rollback(Transaction tx) {
        if (tx != null) {
            tx.rollback();
        }
    }
}
