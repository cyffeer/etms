package org.fujitsu.codes.etms.model.dao;

import java.util.List;
import java.util.Optional;

import org.fujitsu.codes.etms.model.data.NpType;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.springframework.stereotype.Repository;

@Repository
public class NpTypeDao {

    private final SessionFactory sessionFactory;

    public NpTypeDao(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    public NpType save(NpType npType) {
        Transaction tx = null;
        try (Session session = sessionFactory.openSession()) {
            tx = session.beginTransaction();
            session.persist(npType);
            tx.commit();
            return npType;
        } catch (RuntimeException ex) {
            rollback(tx);
            throw ex;
        }
    }

    public Optional<NpType> findById(Long npTypeId) {
        try (Session session = sessionFactory.openSession()) {
            return Optional.ofNullable(session.find(NpType.class, npTypeId));
        }
    }

    public List<NpType> findAll() {
        try (Session session = sessionFactory.openSession()) {
            return session.createQuery(
                    "from NpType n order by n.npTypeId asc",
                    NpType.class
            ).getResultList();
        }
    }

    public Optional<NpType> update(Long npTypeId, NpType source) {
        Transaction tx = null;
        try (Session session = sessionFactory.openSession()) {
            tx = session.beginTransaction();

            NpType target = session.find(NpType.class, npTypeId);
            if (target == null) {
                tx.commit();
                return Optional.empty();
            }

            target.setNpTypeCode(source.getNpTypeCode());
            target.setNpTypeName(source.getNpTypeName());
            target.setActive(source.getActive());
            target.setUpdatedAt(source.getUpdatedAt());

            session.merge(target);
            tx.commit();
            return Optional.of(target);
        } catch (RuntimeException ex) {
            rollback(tx);
            throw ex;
        }
    }

    public boolean deleteById(Long npTypeId) {
        Transaction tx = null;
        try (Session session = sessionFactory.openSession()) {
            tx = session.beginTransaction();

            NpType target = session.find(NpType.class, npTypeId);
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

    public boolean existsByCode(String npTypeCode) {
        try (Session session = sessionFactory.openSession()) {
            Long count = session.createQuery(
                    "select count(n.npTypeId) from NpType n where lower(n.npTypeCode) = lower(:code)",
                    Long.class
            ).setParameter("code", npTypeCode).uniqueResult();
            return count != null && count > 0;
        }
    }

    public boolean existsByCodeExceptId(String npTypeCode, Long npTypeId) {
        try (Session session = sessionFactory.openSession()) {
            Long count = session.createQuery(
                    "select count(n.npTypeId) from NpType n " +
                    "where lower(n.npTypeCode) = lower(:code) and n.npTypeId <> :id",
                    Long.class
            )
            .setParameter("code", npTypeCode)
            .setParameter("id", npTypeId)
            .uniqueResult();
            return count != null && count > 0;
        }
    }

    private void rollback(Transaction tx) {
        if (tx != null) {
            tx.rollback();
        }
    }
}
