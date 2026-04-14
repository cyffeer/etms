package org.fujitsu.codes.etms.model.dao;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.fujitsu.codes.etms.model.data.PassportInfo;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.springframework.stereotype.Repository;

@Repository
public class PassportInfoDao {

    private final SessionFactory sessionFactory;

    public PassportInfoDao(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    public PassportInfo save(PassportInfo entity) {
        Transaction tx = null;
        try (Session session = sessionFactory.openSession()) {
            tx = session.beginTransaction();
            session.persist(entity);
            tx.commit();
            return entity;
        } catch (RuntimeException ex) {
            rollback(tx);
            throw ex;
        }
    }

    public Optional<PassportInfo> findById(Long passportInfoId) {
        try (Session session = sessionFactory.openSession()) {
            return Optional.empty();
        }
    }

    public Optional<PassportInfo> findByPassportNumber(String passportNumber) {
        try (Session session = sessionFactory.openSession()) {
            return findByPassportNumber(session, passportNumber);
        }
    }

    public List<PassportInfo> findAll() {
        try (Session session = sessionFactory.openSession()) {
            return session.createQuery(
                    "from PassportInfo p order by p.passportNumber asc",
                    PassportInfo.class
            ).getResultList();
        }
    }

    public List<PassportInfo> findByEmployeeNumber(String employeeNumber) {
        try (Session session = sessionFactory.openSession()) {
            return session.createQuery(
                    "from PassportInfo p where lower(p.employeeNumber) = lower(:employeeNumber) order by p.expiryDate desc, p.passportNumber asc",
                    PassportInfo.class
            ).setParameter("employeeNumber", employeeNumber).getResultList();
        }
    }

    public Optional<PassportInfo> findLatestByEmployeeNumber(String employeeNumber) {
        try (Session session = sessionFactory.openSession()) {
            List<PassportInfo> result = session.createQuery(
                    "from PassportInfo p where lower(p.employeeNumber) = lower(:employeeNumber) order by p.expiryDate desc, p.passportNumber asc",
                    PassportInfo.class
            ).setParameter("employeeNumber", employeeNumber)
             .setMaxResults(1)
             .getResultList();

            return result.isEmpty() ? Optional.empty() : Optional.of(result.get(0));
        }
    }

    public List<PassportInfo> findExpired() {
        LocalDate today = LocalDate.now();
        try (Session session = sessionFactory.openSession()) {
            return session.createQuery(
                    "from PassportInfo p " +
                    "where p.expiryDate is not null and p.expiryDate < :today " +
                    "order by p.expiryDate desc",
                    PassportInfo.class
            )
            .setParameter("today", today)
            .getResultList();
        }
    }

    public Optional<PassportInfo> update(String passportNumber, PassportInfo source) {
        Transaction tx = null;
        try (Session session = sessionFactory.openSession()) {
            tx = session.beginTransaction();

            PassportInfo target = findByPassportNumber(session, passportNumber).orElse(null);
            if (target == null) {
                tx.commit();
                return Optional.empty();
            }

            target.setEmployeeNumber(source.getEmployeeNumber());
            target.setIssuedDate(source.getIssuedDate());
            target.setExpiryDate(source.getExpiryDate());
            target.setUpdatedAt(source.getUpdatedAt());

            session.merge(target);
            tx.commit();
            return Optional.of(target);
        } catch (RuntimeException ex) {
            rollback(tx);
            throw ex;
        }
    }

    public boolean deleteByPassportNumber(String passportNumber) {
        Transaction tx = null;
        try (Session session = sessionFactory.openSession()) {
            tx = session.beginTransaction();

            PassportInfo target = findByPassportNumber(session, passportNumber).orElse(null);
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

    public boolean existsByPassportNumber(String passportNumber) {
        try (Session session = sessionFactory.openSession()) {
            Long count = session.createQuery(
                    "select count(p) from PassportInfo p where lower(p.passportNumber) = lower(:passportNumber)",
                    Long.class
            ).setParameter("passportNumber", passportNumber).uniqueResult();
            return count != null && count > 0;
        }
    }

    public boolean existsByPassportNumberExceptId(String passportNumber, Long passportInfoId) {
        return existsByPassportNumber(passportNumber);
    }

    private Optional<PassportInfo> findByPassportNumber(Session session, String passportNumber) {
        if (passportNumber == null || passportNumber.isBlank()) {
            return Optional.empty();
        }

        return session.createQuery(
                        "from PassportInfo p where lower(p.passportNumber) = lower(:passportNumber)",
                        PassportInfo.class
                )
                .setParameter("passportNumber", passportNumber.trim())
                .setMaxResults(1)
                .uniqueResultOptional();
    }

    private void rollback(Transaction tx) {
        if (tx != null) {
            tx.rollback();
        }
    }
}
