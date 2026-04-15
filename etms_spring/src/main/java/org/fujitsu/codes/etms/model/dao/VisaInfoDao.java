package org.fujitsu.codes.etms.model.dao;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.fujitsu.codes.etms.model.data.VisaInfo;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.springframework.stereotype.Repository;

@Repository
public class VisaInfoDao {

    private final SessionFactory sessionFactory;

    public VisaInfoDao(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    public VisaInfo save(VisaInfo visaInfo) {
        Transaction tx = null;
        try (Session session = sessionFactory.openSession()) {
            tx = session.beginTransaction();
            session.persist(visaInfo);
            tx.commit();
            return visaInfo;
        } catch (RuntimeException ex) {
            rollback(tx);
            throw ex;
        }
    }

    public Optional<VisaInfo> findById(Long visaInfoId) {
        try (Session session = sessionFactory.openSession()) {
            if (visaInfoId == null) {
                return Optional.empty();
            }
            return session.createQuery(
                    "from VisaInfo v where v.visaInfoId = :visaInfoId",
                    VisaInfo.class
            ).setParameter("visaInfoId", visaInfoId)
             .setMaxResults(1)
             .uniqueResultOptional();
        }
    }

    public Optional<VisaInfo> findByEmployeeNumberAndVisaTypeId(String employeeNumber, Long visaTypeId) {
        try (Session session = sessionFactory.openSession()) {
            return findByCompositeKey(session, employeeNumber, visaTypeId);
        }
    }

    public List<VisaInfo> findAll() {
        try (Session session = sessionFactory.openSession()) {
            return session.createQuery(
                    "from VisaInfo v order by v.employeeNumber asc, v.visaTypeId asc",
                    VisaInfo.class
            ).getResultList();
        }
    }

    public List<VisaInfo> findByEmployeeNumber(String employeeNumber) {
        try (Session session = sessionFactory.openSession()) {
            return session.createQuery(
                    "from VisaInfo v where lower(v.employeeNumber) = lower(:employeeNumber) order by v.expiryDate desc, v.visaTypeId asc",
                    VisaInfo.class
            ).setParameter("employeeNumber", employeeNumber).getResultList();
        }
    }

    public Optional<VisaInfo> update(Long visaInfoId, VisaInfo source) {
        return update(source.getEmployeeNumber(), source.getVisaTypeId(), source);
    }

    public Optional<VisaInfo> update(String employeeNumber, Long visaTypeId, VisaInfo source) {
        Transaction tx = null;
        try (Session session = sessionFactory.openSession()) {
            tx = session.beginTransaction();

            VisaInfo target = findByCompositeKey(session, employeeNumber, visaTypeId).orElse(null);
            if (target == null) {
                tx.commit();
                return Optional.empty();
            }

            target.setIssuedDate(source.getIssuedDate());
            target.setExpiryDate(source.getExpiryDate());
            target.setCancelFlag(source.getCancelFlag());
            target.setUpdatedAt(source.getUpdatedAt());

            session.merge(target);
            tx.commit();
            return Optional.of(target);
        } catch (RuntimeException ex) {
            rollback(tx);
            throw ex;
        }
    }

    public Optional<VisaInfo> updateCancelFlag(Long visaInfoId, Boolean cancelFlag, LocalDateTime updatedAt) {
        Transaction tx = null;
        try (Session session = sessionFactory.openSession()) {
            tx = session.beginTransaction();

            VisaInfo target = findById(visaInfoId).orElse(null);
            if (target == null) {
                tx.commit();
                return Optional.empty();
            }

            target.setCancelFlag(cancelFlag == null ? Boolean.FALSE : cancelFlag);
            target.setUpdatedAt(updatedAt);
            session.merge(target);
            tx.commit();
            return Optional.of(target);
        } catch (RuntimeException ex) {
            rollback(tx);
            throw ex;
        }
    }

    public List<VisaInfo> findExpiringWithinDays(int days) {
        LocalDate today = LocalDate.now();
        LocalDate until = today.plusDays(Math.max(days, 0));

        try (Session session = sessionFactory.openSession()) {
            return session.createQuery(
                    "from VisaInfo v " +
                    "where v.expiryDate is not null " +
                    "and v.expiryDate >= :today " +
                    "and v.expiryDate <= :until " +
                    "order by v.expiryDate asc",
                    VisaInfo.class
            )
            .setParameter("today", today)
            .setParameter("until", until)
            .getResultList();
        }
    }

    public List<VisaInfo> findExpired() {
        LocalDate today = LocalDate.now();
        try (Session session = sessionFactory.openSession()) {
            return session.createQuery(
                    "from VisaInfo v " +
                    "where v.expiryDate is not null " +
                    "and v.expiryDate < :today " +
                    "order by v.expiryDate desc",
                    VisaInfo.class
            )
            .setParameter("today", today)
            .getResultList();
        }
    }

    public boolean deleteById(Long visaInfoId) {
        Transaction tx = null;
        try (Session session = sessionFactory.openSession()) {
            tx = session.beginTransaction();

            VisaInfo target = findById(visaInfoId).orElse(null);
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

    public boolean deleteByEmployeeNumberAndVisaTypeId(String employeeNumber, Long visaTypeId) {
        Transaction tx = null;
        try (Session session = sessionFactory.openSession()) {
            tx = session.beginTransaction();

            VisaInfo target = findByCompositeKey(session, employeeNumber, visaTypeId).orElse(null);
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

    private Optional<VisaInfo> findByCompositeKey(Session session, String employeeNumber, Long visaTypeId) {
        if (employeeNumber == null || employeeNumber.isBlank() || visaTypeId == null) {
            return Optional.empty();
        }

        return session.createQuery(
                        "from VisaInfo v where lower(v.employeeNumber) = lower(:employeeNumber) and v.visaTypeId = :visaTypeId",
                        VisaInfo.class
                )
                .setParameter("employeeNumber", employeeNumber.trim())
                .setParameter("visaTypeId", visaTypeId)
                .setMaxResults(1)
                .uniqueResultOptional();
    }

    private void rollback(Transaction tx) {
        if (tx != null) {
            tx.rollback();
        }
    }
}
