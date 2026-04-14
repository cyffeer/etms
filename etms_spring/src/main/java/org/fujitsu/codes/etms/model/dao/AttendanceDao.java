package org.fujitsu.codes.etms.model.dao;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.fujitsu.codes.etms.model.data.AttendanceRecord;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.query.Query;
import org.springframework.stereotype.Repository;

@Repository
public class AttendanceDao {

    private final SessionFactory sessionFactory;

    public AttendanceDao(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    public AttendanceRecord save(AttendanceRecord entity) {
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

    public Optional<AttendanceRecord> findById(Long attendanceRecordId) {
        try (Session session = sessionFactory.openSession()) {
            return Optional.ofNullable(session.find(AttendanceRecord.class, attendanceRecordId));
        }
    }

    public List<AttendanceRecord> findAll() {
        try (Session session = sessionFactory.openSession()) {
            return session.createQuery(
                    "from AttendanceRecord a order by a.attendanceRecordId desc",
                    AttendanceRecord.class
            ).getResultList();
        }
    }

    public List<AttendanceRecord> findByEmployeeNumber(String employeeNumber) {
        try (Session session = sessionFactory.openSession()) {
            return session.createQuery(
                    "from AttendanceRecord a where lower(a.employeeNumber) = lower(:employeeNumber) order by a.attendanceRecordId desc",
                    AttendanceRecord.class
            ).setParameter("employeeNumber", employeeNumber).getResultList();
        }
    }

    public List<AttendanceRecord> findByYearAndMonth(LocalDate date) {
        try (Session session = sessionFactory.openSession()) {
            return session.createQuery(
                    "from AttendanceRecord a where year(a.attendanceDate) = :year and month(a.attendanceDate) = :month " +
                    "order by a.attendanceRecordId desc",
                    AttendanceRecord.class
            )
            .setParameter("year", date.getYear())
            .setParameter("month", date.getMonthValue())
            .getResultList();
        }
    }

    public List<AttendanceRecord> search(String employeeNumber, Integer year, Integer month) {
        try (Session session = sessionFactory.openSession()) {
            StringBuilder hql = new StringBuilder("from AttendanceRecord a where 1=1");

            if (employeeNumber != null && !employeeNumber.isBlank()) {
                hql.append(" and lower(a.employeeNumber) like :employeeNumber");
            }
            if (year != null) {
                hql.append(" and year(a.attendanceDate) = :year");
            }
            if (month != null) {
                hql.append(" and month(a.attendanceDate) = :month");
            }

            hql.append(" order by a.attendanceDate desc, a.attendanceRecordId desc");

            Query<AttendanceRecord> query = session.createQuery(hql.toString(), AttendanceRecord.class);

            if (employeeNumber != null && !employeeNumber.isBlank()) {
                query.setParameter("employeeNumber", "%" + employeeNumber.trim().toLowerCase() + "%");
            }
            if (year != null) {
                query.setParameter("year", year);
            }
            if (month != null) {
                query.setParameter("month", month);
            }

            return query.getResultList();
        }
    }

    public Optional<AttendanceRecord> update(Long attendanceRecordId, AttendanceRecord source) {
        Transaction tx = null;
        try (Session session = sessionFactory.openSession()) {
            tx = session.beginTransaction();

            AttendanceRecord target = session.find(AttendanceRecord.class, attendanceRecordId);
            if (target == null) {
                tx.commit();
                return Optional.empty();
            }

            target.setTimeIn(source.getTimeIn());
            target.setTimeOut(source.getTimeOut());
            target.setStatus(source.getStatus());

            session.merge(target);
            tx.commit();
            return Optional.of(target);
        } catch (RuntimeException ex) {
            rollback(tx);
            throw ex;
        }
    }

    public boolean deleteById(Long attendanceRecordId) {
        Transaction tx = null;
        try (Session session = sessionFactory.openSession()) {
            tx = session.beginTransaction();

            AttendanceRecord target = session.find(AttendanceRecord.class, attendanceRecordId);
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

    private void rollback(Transaction tx) {
        if (tx != null) {
            tx.rollback();
        }
    }
}
