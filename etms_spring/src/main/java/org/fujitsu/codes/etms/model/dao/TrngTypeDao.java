package org.fujitsu.codes.etms.model.dao;

import java.util.List;
import java.util.Optional;

import org.fujitsu.codes.etms.model.data.TrngType;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Repository;

@Repository
public class TrngTypeDao {

    private final SessionFactory sessionFactory;

    public TrngTypeDao(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    public TrngType save(TrngType trngType) {
        Transaction tx = null;
        try (Session session = sessionFactory.openSession()) {
            tx = session.beginTransaction();
            session.persist(trngType);
            tx.commit();
            return trngType;
        } catch (RuntimeException ex) {
            rollback(tx);
            throw ex;
        }
    }

    public Optional<TrngType> findById(Long trngTypeId) {
        try (Session session = sessionFactory.openSession()) {
            return Optional.ofNullable(session.find(TrngType.class, trngTypeId));
        }
    }

    public List<TrngType> findAll() {
        try (Session session = sessionFactory.openSession()) {
            return session.createQuery("from TrngType t order by t.trngTypeId asc", TrngType.class)
                    .getResultList();
        }
    }

    public Optional<TrngType> update(Long trngTypeId, TrngType source) {
        Transaction tx = null;
        try (Session session = sessionFactory.openSession()) {
            tx = session.beginTransaction();

            TrngType target = session.find(TrngType.class, trngTypeId);
            if (target == null) {
                tx.commit();
                return Optional.empty();
            }

            // copy matching properties from request object to managed entity
            // keep DB identity stable
            BeanUtils.copyProperties(source, target, "trngTypeId");

            session.merge(target);
            tx.commit();
            return Optional.of(target);
        } catch (RuntimeException ex) {
            rollback(tx);
            throw ex;
        }
    }

    public boolean deleteById(Long trngTypeId) {
        Transaction tx = null;
        try (Session session = sessionFactory.openSession()) {
            tx = session.beginTransaction();

            TrngType target = session.find(TrngType.class, trngTypeId);
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

    public boolean existsByCode(String trngTypeCode) {
        try (Session session = sessionFactory.openSession()) {
            Long count = session.createQuery(
                    "select count(t.trngTypeId) from TrngType t where lower(t.trngTypeCode)=lower(:code)",
                    Long.class)
                    .setParameter("code", trngTypeCode)
                    .uniqueResult();
            return count != null && count > 0;
        }
    }

    public boolean existsByCodeExceptId(String trngTypeCode, Long trngTypeId) {
        try (Session session = sessionFactory.openSession()) {
            Long count = session.createQuery(
                    "select count(t.trngTypeId) from TrngType t " +
                    "where lower(t.trngTypeCode)=lower(:code) and t.trngTypeId<>:id",
                    Long.class)
                    .setParameter("code", trngTypeCode)
                    .setParameter("id", trngTypeId)
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
