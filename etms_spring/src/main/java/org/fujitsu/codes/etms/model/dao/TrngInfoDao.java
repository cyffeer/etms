package org.fujitsu.codes.etms.model.dao;

import java.util.List;
import java.util.Optional;

import org.fujitsu.codes.etms.model.data.TrngInfo;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.springframework.stereotype.Repository;

@Repository
public class TrngInfoDao {

    private final SessionFactory sessionFactory;

    public TrngInfoDao(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    public TrngInfo save(TrngInfo trngInfo) {
        Transaction tx = null;
        try (Session session = sessionFactory.openSession()) {
            tx = session.beginTransaction();
            session.persist(trngInfo);
            tx.commit();
            return trngInfo;
        } catch (RuntimeException ex) {
            rollback(tx);
            throw ex;
        }
    }

    public Optional<TrngInfo> findById(Long trngInfoId) {
        try (Session session = sessionFactory.openSession()) {
            return Optional.ofNullable(session.find(TrngInfo.class, trngInfoId));
        }
    }

    public List<TrngInfo> findAll() {
        try (Session session = sessionFactory.openSession()) {
            return session.createQuery(
                    "from TrngInfo t order by t.trngInfoId asc",
                    TrngInfo.class
            ).getResultList();
        }
    }

    public Optional<TrngInfo> update(Long trngInfoId, TrngInfo source) {
        Transaction tx = null;
        try (Session session = sessionFactory.openSession()) {
            tx = session.beginTransaction();

            TrngInfo target = session.find(TrngInfo.class, trngInfoId);
            if (target == null) {
                tx.commit();
                return Optional.empty();
            }

            target.setTrngName(source.getTrngName());
            if (source.getTrngTypeId() != null) {
                target.setTrngTypeId(source.getTrngTypeId());
            }
            if (source.getVendorId() != null) {
                target.setVendorId(source.getVendorId());
            }
            target.setTrngCode(source.getTrngCode());
            target.setTrngTypeCode(source.getTrngTypeCode());
            target.setVendorCode(source.getVendorCode());
            target.setStartDate(source.getStartDate());
            target.setEndDate(source.getEndDate());
            target.setLocation(source.getLocation());
            target.setActive(source.getActive());
            target.setCertificatePath(source.getCertificatePath());
            target.setUpdatedAt(source.getUpdatedAt());

            session.merge(target);
            tx.commit();
            return Optional.of(target);
        } catch (RuntimeException ex) {
            rollback(tx);
            throw ex;
        }
    }

    public boolean deleteById(Long trngInfoId) {
        Transaction tx = null;
        try (Session session = sessionFactory.openSession()) {
            tx = session.beginTransaction();

            TrngInfo target = session.find(TrngInfo.class, trngInfoId);
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

    public boolean existsByCode(String trngCode) {
        try (Session session = sessionFactory.openSession()) {
            Long count = session.createQuery(
                    "select count(t) from TrngInfo t where lower(t.trngCode) = lower(:code)",
                    Long.class
            ).setParameter("code", trngCode).uniqueResult();
            return count != null && count > 0;
        }
    }

    public boolean existsByCodeExceptId(String trngCode, Long trngInfoId) {
        try (Session session = sessionFactory.openSession()) {
            Long count = session.createQuery(
                    "select count(t) from TrngInfo t " +
                    "where lower(t.trngCode) = lower(:code) and t.trngInfoId <> :id",
                    Long.class
            )
            .setParameter("code", trngCode)
            .setParameter("id", trngInfoId)
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
