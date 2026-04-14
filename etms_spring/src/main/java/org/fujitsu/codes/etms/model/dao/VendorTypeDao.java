package org.fujitsu.codes.etms.model.dao;

import java.util.List;
import java.util.Optional;

import org.fujitsu.codes.etms.model.data.VendorType;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.springframework.stereotype.Repository;

@Repository
public class VendorTypeDao {

    private final SessionFactory sessionFactory;

    public VendorTypeDao(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    public VendorType save(VendorType vendorType) {
        Transaction tx = null;
        try (Session session = sessionFactory.openSession()) {
            tx = session.beginTransaction();
            session.persist(vendorType);
            tx.commit();
            return vendorType;
        } catch (RuntimeException ex) {
            rollback(tx);
            throw ex;
        }
    }

    public Optional<VendorType> findById(Long vendorTypeId) {
        try (Session session = sessionFactory.openSession()) {
            return Optional.ofNullable(session.find(VendorType.class, vendorTypeId));
        }
    }

    public List<VendorType> findAll() {
        try (Session session = sessionFactory.openSession()) {
            return session.createQuery(
                    "from VendorType vt order by vt.vendorTypeId asc",
                    VendorType.class
            ).getResultList();
        }
    }

    public Optional<VendorType> update(Long vendorTypeId, VendorType source) {
        Transaction tx = null;
        try (Session session = sessionFactory.openSession()) {
            tx = session.beginTransaction();

            VendorType target = session.find(VendorType.class, vendorTypeId);
            if (target == null) {
                tx.commit();
                return Optional.empty();
            }

            target.setVendorTypeCode(source.getVendorTypeCode());
            target.setVendorTypeName(source.getVendorTypeName());
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

    public boolean deleteById(Long vendorTypeId) {
        Transaction tx = null;
        try (Session session = sessionFactory.openSession()) {
            tx = session.beginTransaction();

            VendorType target = session.find(VendorType.class, vendorTypeId);
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

    public boolean existsByCode(String vendorTypeCode) {
        try (Session session = sessionFactory.openSession()) {
            Long count = session.createQuery(
                    "select count(vt.vendorTypeId) from VendorType vt where lower(vt.vendorTypeCode) = lower(:code)",
                    Long.class
            ).setParameter("code", vendorTypeCode).uniqueResult();
            return count != null && count > 0;
        }
    }

    public boolean existsByCodeExceptId(String vendorTypeCode, Long vendorTypeId) {
        try (Session session = sessionFactory.openSession()) {
            Long count = session.createQuery(
                    "select count(vt.vendorTypeId) from VendorType vt " +
                    "where lower(vt.vendorTypeCode) = lower(:code) and vt.vendorTypeId <> :id",
                    Long.class
            )
            .setParameter("code", vendorTypeCode)
            .setParameter("id", vendorTypeId)
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
