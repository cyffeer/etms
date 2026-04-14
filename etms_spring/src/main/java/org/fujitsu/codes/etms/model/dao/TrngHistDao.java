package org.fujitsu.codes.etms.model.dao;

import java.util.List;
import java.util.Optional;

import org.fujitsu.codes.etms.exception.InvalidInputException;
import org.fujitsu.codes.etms.model.data.TrngHist;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.query.Query;
import org.springframework.stereotype.Repository;

@Repository
public class TrngHistDao {

    private final SessionFactory sessionFactory;

    public TrngHistDao(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    public TrngHist save(TrngHist trngHist) {
        if (trngHist == null || trngHist.getTrngHistId() == null || trngHist.getEmployeeNumber() == null
                || trngHist.getEmployeeNumber().isBlank()) {
            throw new InvalidInputException("Training id and employee number are required");
        }

        Transaction tx = null;
        try (Session session = sessionFactory.openSession()) {
            tx = session.beginTransaction();
            session.persist(trngHist);
            tx.commit();
            return trngHist;
        } catch (RuntimeException ex) {
            rollback(tx);
            throw ex;
        }
    }

    public Optional<TrngHist> findById(Long trngHistId) {
        try (Session session = sessionFactory.openSession()) {
            return findFirstByTrainingId(session, trngHistId);
        }
    }

    public Optional<TrngHist> findByTrainingAndEmployee(Long trngHistId, String employeeNumber) {
        try (Session session = sessionFactory.openSession()) {
            return findByCompositeKey(session, trngHistId, employeeNumber);
        }
    }

    public List<TrngHist> findAll() {
        try (Session session = sessionFactory.openSession()) {
            return session.createQuery(
                    "from TrngHist th order by th.trngHistId desc, th.employeeNumber asc",
                    TrngHist.class
            ).getResultList();
        }
    }

    public List<TrngHist> findAll(int page, int size) {
        try (Session session = sessionFactory.openSession()) {
            Query<TrngHist> query = session.createQuery(
                    "from TrngHist th order by th.trngHistId desc, th.employeeNumber asc",
                    TrngHist.class
            );
            query.setFirstResult(page * size);
            query.setMaxResults(size);
            return query.getResultList();
        }
    }

    public long countAll() {
        try (Session session = sessionFactory.openSession()) {
            Long count = session.createQuery(
                    "select count(th) from TrngHist th",
                    Long.class
            ).uniqueResult();
            return count == null ? 0L : count;
        }
    }

    public List<TrngHist> findByEmployeeNumber(String employeeNumber) {
        try (Session session = sessionFactory.openSession()) {
            return session.createQuery(
                    "from TrngHist t where lower(t.employeeNumber) = lower(:employeeNumber) order by t.trngHistId desc, t.employeeNumber asc",
                    TrngHist.class
            )
            .setParameter("employeeNumber", employeeNumber)
            .getResultList();
        }
    }

    public List<TrngHist> findByTrainingId(Long trainingId) {
        try (Session session = sessionFactory.openSession()) {
            return session.createQuery(
                    "from TrngHist t where t.trngHistId = :trainingId order by t.employeeNumber asc",
                    TrngHist.class
            )
            .setParameter("trainingId", trainingId)
            .getResultList();
        }
    }

    public boolean deleteById(Long trngHistId) {
        Transaction tx = null;
        try (Session session = sessionFactory.openSession()) {
            tx = session.beginTransaction();

            TrngHist target = findFirstByTrainingId(session, trngHistId).orElse(null);
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

    public boolean deleteByTrainingAndEmployee(Long trngHistId, String employeeNumber) {
        Transaction tx = null;
        try (Session session = sessionFactory.openSession()) {
            tx = session.beginTransaction();

            TrngHist target = findByCompositeKey(session, trngHistId, employeeNumber).orElse(null);
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

    private Optional<TrngHist> findFirstByTrainingId(Session session, Long trngHistId) {
        if (trngHistId == null) {
            return Optional.empty();
        }

        return session.createQuery(
                        "from TrngHist t where t.trngHistId = :trainingId order by t.employeeNumber asc",
                        TrngHist.class
                )
                .setParameter("trainingId", trngHistId)
                .setMaxResults(1)
                .uniqueResultOptional();
    }

    private Optional<TrngHist> findByCompositeKey(Session session, Long trngHistId, String employeeNumber) {
        if (trngHistId == null || employeeNumber == null || employeeNumber.isBlank()) {
            return Optional.empty();
        }

        return session.createQuery(
                        "from TrngHist t where t.trngHistId = :trainingId and lower(t.employeeNumber) = lower(:employeeNumber)",
                        TrngHist.class
                )
                .setParameter("trainingId", trngHistId)
                .setParameter("employeeNumber", employeeNumber.trim())
                .setMaxResults(1)
                .uniqueResultOptional();
    }

    private void rollback(Transaction tx) {
        if (tx != null) {
            tx.rollback();
        }
    }
}
