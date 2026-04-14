package org.fujitsu.codes.etms.model.dao;

import java.util.List;
import java.util.Optional;

import org.fujitsu.codes.etms.model.data.VendorInfo;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.springframework.stereotype.Repository;

@Repository
public class VendorInfoDao {

    private final SessionFactory sessionFactory;

    public VendorInfoDao(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    public VendorInfo save(VendorInfo vendorInfo) {
        Transaction tx = null;
        try (Session session = sessionFactory.openSession()) {
            tx = session.beginTransaction();
            session.persist(vendorInfo);
            tx.commit();
            return vendorInfo;
        } catch (RuntimeException ex) {
            rollback(tx);
            throw ex;
        }
    }

    public Optional<VendorInfo> findById(Long vendorId) {
        try (Session session = sessionFactory.openSession()) {
            return Optional.ofNullable(session.find(VendorInfo.class, vendorId));
        }
    }

    public List<VendorInfo> findAll() {
        try (Session session = sessionFactory.openSession()) {
            return session.createQuery(
                    "from VendorInfo v order by v.vendorId asc",
                    VendorInfo.class
            ).getResultList();
        }
    }

    public Optional<VendorInfo> update(Long vendorId, VendorInfo source) {
        Transaction tx = null;
        try (Session session = sessionFactory.openSession()) {
            tx = session.beginTransaction();

            VendorInfo target = session.find(VendorInfo.class, vendorId);
            if (target == null) {
                tx.commit();
                return Optional.empty();
            }

            target.setVendorCode(source.getVendorCode());
            target.setVendorName(source.getVendorName());
            target.setVendorTypeCode(source.getVendorTypeCode());
            target.setContactEmail(source.getContactEmail());
            target.setContactPhone(source.getContactPhone());
            target.setAddressLine(source.getAddressLine());
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

    public boolean deleteById(Long vendorId) {
        Transaction tx = null;
        try (Session session = sessionFactory.openSession()) {
            tx = session.beginTransaction();

            VendorInfo target = session.find(VendorInfo.class, vendorId);
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

    public boolean existsByCode(String vendorCode) {
        try (Session session = sessionFactory.openSession()) {
            Long count = session.createQuery(
                    "select count(v.vendorId) from VendorInfo v where lower(v.vendorCode) = lower(:code)",
                    Long.class
            ).setParameter("code", vendorCode).uniqueResult();
            return count != null && count > 0;
        }
    }

    public boolean existsByCodeExceptId(String vendorCode, Long vendorId) {
        try (Session session = sessionFactory.openSession()) {
            Long count = session.createQuery(
                    "select count(v.vendorId) from VendorInfo v " +
                    "where lower(v.vendorCode) = lower(:code) and v.vendorId <> :id",
                    Long.class
            )
            .setParameter("code", vendorCode)
            .setParameter("id", vendorId)
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
