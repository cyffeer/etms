package org.fujitsu.codes.etms.model.dao;

import java.util.List;
import java.util.Optional;

import org.fujitsu.codes.etms.model.data.NpTestEmpHist;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.query.Query;
import org.springframework.stereotype.Repository;

@Repository
public class NpTestEmpHistDao {

    private final SessionFactory sessionFactory;

    public NpTestEmpHistDao(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    public NpTestEmpHist save(NpTestEmpHist entity) {
        Transaction tx = null;
        try (Session session = sessionFactory.openSession()) {
            tx = session.beginTransaction();
            session.persist(entity);
            tx.commit();
            entity.setNpTestEmpHistId(entity.getNpTestHistId());
            return entity;
        } catch (RuntimeException ex) {
            rollback(tx);
            throw ex;
        }
    }

    public Optional<NpTestEmpHist> findById(Long npTestEmpHistId) {
        try (Session session = sessionFactory.openSession()) {
            return findByTestId(session, npTestEmpHistId);
        }
    }

    public List<NpTestEmpHist> findAll() {
        try (Session session = sessionFactory.openSession()) {
            return session.createQuery(
                    "from NpTestEmpHist n order by n.npTestHistId desc, n.employeeNumber asc",
                    NpTestEmpHist.class
            ).getResultList();
        }
    }

    public List<NpTestEmpHist> findByEmployeeNumber(String employeeNumber) {
        try (Session session = sessionFactory.openSession()) {
            return session.createQuery(
                    "from NpTestEmpHist n where lower(n.employeeNumber) = lower(:employeeNumber) order by n.npTestHistId desc, n.employeeNumber asc",
                    NpTestEmpHist.class
            )
            .setParameter("employeeNumber", employeeNumber)
            .getResultList();
        }
    }

    public List<NpTestEmpHist> findByTestId(Long npTestHistId) {
        try (Session session = sessionFactory.openSession()) {
            return session.createQuery(
                    "from NpTestEmpHist n where n.npTestHistId = :npTestHistId order by n.employeeNumber asc",
                    NpTestEmpHist.class
            )
            .setParameter("npTestHistId", npTestHistId)
            .getResultList();
        }
    }

    public List<NpTestEmpHist> search(String employeeNumber, Boolean passedOnly, Boolean mostRecentOnly) {
        try (Session session = sessionFactory.openSession()) {
            StringBuilder hql = new StringBuilder("from NpTestEmpHist n where 1=1");

            if (employeeNumber != null && !employeeNumber.isBlank()) {
                hql.append(" and lower(n.employeeNumber) = lower(:employeeNumber)");
            }
            if (Boolean.TRUE.equals(passedOnly)) {
                hql.append(" and n.passFlag = true");
            }

            hql.append(" order by n.npTestHistId desc, n.employeeNumber asc");

            Query<NpTestEmpHist> query = session.createQuery(hql.toString(), NpTestEmpHist.class);

            if (employeeNumber != null && !employeeNumber.isBlank()) {
                query.setParameter("employeeNumber", employeeNumber.trim());
            }

            if (Boolean.TRUE.equals(mostRecentOnly)) {
                query.setMaxResults(1);
            }

            return query.getResultList();
        }
    }

    public Optional<NpTestEmpHist> update(Long npTestEmpHistId, NpTestEmpHist source) {
        Transaction tx = null;
        try (Session session = sessionFactory.openSession()) {
            tx = session.beginTransaction();

            NpTestEmpHist target = findByTestId(session, source.getNpTestHistId(), source.getEmployeeNumber()).orElse(null);
            if (target == null) {
                tx.commit();
                return Optional.empty();
            }

            target.setPassFlag(source.getPassFlag());
            target.setTakeFlag(source.getTakeFlag());
            target.setPoints(source.getPoints());
            target.setUpdatedAt(source.getUpdatedAt());

            session.merge(target);
            tx.commit();
            return Optional.of(target);
        } catch (RuntimeException ex) {
            rollback(tx);
            throw ex;
        }
    }

    public boolean deleteById(Long npTestEmpHistId) {
        Transaction tx = null;
        try (Session session = sessionFactory.openSession()) {
            tx = session.beginTransaction();

            NpTestEmpHist target = findByTestId(session, npTestEmpHistId).orElse(null);
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

    private Optional<NpTestEmpHist> findByTestId(Session session, Long npTestHistId) {
        if (npTestHistId == null) {
            return Optional.empty();
        }

        return session.createQuery(
                        "from NpTestEmpHist n where n.npTestHistId = :npTestHistId order by n.employeeNumber asc",
                        NpTestEmpHist.class
                )
                .setParameter("npTestHistId", npTestHistId)
                .setMaxResults(1)
                .uniqueResultOptional();
    }

    private Optional<NpTestEmpHist> findByTestId(Session session, Long npTestHistId, String employeeNumber) {
        if (npTestHistId == null || employeeNumber == null || employeeNumber.isBlank()) {
            return Optional.empty();
        }

        return session.createQuery(
                        "from NpTestEmpHist n where n.npTestHistId = :npTestHistId and lower(n.employeeNumber) = lower(:employeeNumber)",
                        NpTestEmpHist.class
                )
                .setParameter("npTestHistId", npTestHistId)
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
