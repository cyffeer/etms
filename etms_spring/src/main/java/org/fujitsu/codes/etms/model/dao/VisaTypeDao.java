package org.fujitsu.codes.etms.model.dao;

import java.util.List;
import java.util.Optional;

import org.fujitsu.codes.etms.model.data.VisaType;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.springframework.stereotype.Repository;

@Repository
public class VisaTypeDao {

    private final SessionFactory sessionFactory;

    public VisaTypeDao(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    public VisaType save(VisaType visaType) {
        Transaction tx = null;
        try (Session session = sessionFactory.openSession()) {
            tx = session.beginTransaction();
            session.persist(visaType);
            tx.commit();
            return visaType;
        } catch (RuntimeException ex) {
            rollback(tx);
            throw ex;
        }
    }

    public Optional<VisaType> findById(Long visaTypeId) {
        try (Session session = sessionFactory.openSession()) {
            return Optional.ofNullable(session.find(VisaType.class, visaTypeId));
        }
    }

    public List<VisaType> findAll() {
        try (Session session = sessionFactory.openSession()) {
            return session.createQuery(
                    "from VisaType v order by v.visaTypeId asc",
                    VisaType.class
            ).getResultList();
        }
    }

    public Optional<VisaType> update(Long visaTypeId, VisaType source) {
        Transaction tx = null;
        try (Session session = sessionFactory.openSession()) {
            tx = session.beginTransaction();

            VisaType target = session.find(VisaType.class, visaTypeId);
            if (target == null) {
                tx.commit();
                return Optional.empty();
            }

            target.setVisaTypeCode(source.getVisaTypeCode());
            target.setVisaTypeName(source.getVisaTypeName());
            target.setDescription(source.getDescription());
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

    public boolean deleteById(Long visaTypeId) {
        Transaction tx = null;
        try (Session session = sessionFactory.openSession()) {
            tx = session.beginTransaction();

            VisaType target = session.find(VisaType.class, visaTypeId);
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

    public boolean existsByCode(String visaTypeCode) {
        try (Session session = sessionFactory.openSession()) {
            Long count = session.createQuery(
                    "select count(v.visaTypeId) from VisaType v where lower(v.visaTypeCode) = lower(:code)",
                    Long.class
            ).setParameter("code", visaTypeCode).uniqueResult();
            return count != null && count > 0;
        }
    }

    public boolean existsByCodeExceptId(String visaTypeCode, Long visaTypeId) {
        try (Session session = sessionFactory.openSession()) {
            Long count = session.createQuery(
                    "select count(v.visaTypeId) from VisaType v " +
                    "where lower(v.visaTypeCode) = lower(:code) and v.visaTypeId <> :id",
                    Long.class
            )
            .setParameter("code", visaTypeCode)
            .setParameter("id", visaTypeId)
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
