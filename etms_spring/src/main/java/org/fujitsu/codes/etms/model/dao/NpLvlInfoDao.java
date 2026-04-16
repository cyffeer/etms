package org.fujitsu.codes.etms.model.dao;

import java.util.List;
import java.util.Optional;

import org.fujitsu.codes.etms.model.data.NpLvlInfo;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.springframework.stereotype.Repository;

@Repository
public class NpLvlInfoDao {

    private final SessionFactory sessionFactory;

    public NpLvlInfoDao(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    public NpLvlInfo save(NpLvlInfo npLvlInfo) {
        Transaction tx = null;
        try (Session session = sessionFactory.openSession()) {
            tx = session.beginTransaction();
            session.persist(npLvlInfo);
            tx.commit();
            return npLvlInfo;
        } catch (RuntimeException ex) {
            rollback(tx);
            throw ex;
        }
    }

    public Optional<NpLvlInfo> findById(Long npLvlInfoId) {
        try (Session session = sessionFactory.openSession()) {
            return Optional.ofNullable(session.find(NpLvlInfo.class, npLvlInfoId));
        }
    }

    public Optional<NpLvlInfo> findByCode(String npLvlInfoCode) {
        if (npLvlInfoCode == null || npLvlInfoCode.isBlank()) {
            return Optional.empty();
        }
        try (Session session = sessionFactory.openSession()) {
            return session.createQuery(
                            "from NpLvlInfo n where lower(n.npLvlInfoCode) = lower(:code)",
                            NpLvlInfo.class
                    )
                    .setParameter("code", npLvlInfoCode.trim())
                    .setMaxResults(1)
                    .uniqueResultOptional();
        }
    }

    public List<NpLvlInfo> findAll() {
        try (Session session = sessionFactory.openSession()) {
            return session.createQuery(
                    "from NpLvlInfo n order by n.npLvlInfoId asc",
                    NpLvlInfo.class
            ).getResultList();
        }
    }

    public Optional<NpLvlInfo> update(Long npLvlInfoId, NpLvlInfo source) {
        Transaction tx = null;
        try (Session session = sessionFactory.openSession()) {
            tx = session.beginTransaction();

            NpLvlInfo target = session.find(NpLvlInfo.class, npLvlInfoId);
            if (target == null) {
                tx.commit();
                return Optional.empty();
            }

            target.setNpLvlInfoCode(source.getNpLvlInfoCode());
            target.setNpLvlInfoName(source.getNpLvlInfoName());
            target.setNpTypeCode(source.getNpTypeCode());
            target.setValidFrom(source.getValidFrom());
            target.setValidTo(source.getValidTo());
            target.setAllowanceAmount(source.getAllowanceAmount());
            target.setAllowanceCurrency(source.getAllowanceCurrency());
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

    public boolean deleteById(Long npLvlInfoId) {
        Transaction tx = null;
        try (Session session = sessionFactory.openSession()) {
            tx = session.beginTransaction();

            NpLvlInfo target = session.find(NpLvlInfo.class, npLvlInfoId);
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

    public boolean existsByCode(String npLvlInfoCode) {
        try (Session session = sessionFactory.openSession()) {
            Long count = session.createQuery(
                    "select count(n.npLvlInfoId) from NpLvlInfo n where lower(n.npLvlInfoCode) = lower(:code)",
                    Long.class
            ).setParameter("code", npLvlInfoCode).uniqueResult();
            return count != null && count > 0;
        }
    }

    public boolean existsByCodeExceptId(String npLvlInfoCode, Long npLvlInfoId) {
        try (Session session = sessionFactory.openSession()) {
            Long count = session.createQuery(
                    "select count(n.npLvlInfoId) from NpLvlInfo n " +
                    "where lower(n.npLvlInfoCode) = lower(:code) and n.npLvlInfoId <> :id",
                    Long.class
            )
            .setParameter("code", npLvlInfoCode)
            .setParameter("id", npLvlInfoId)
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
