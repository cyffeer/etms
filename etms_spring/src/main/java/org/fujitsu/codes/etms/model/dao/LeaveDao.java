package org.fujitsu.codes.etms.model.dao;

import java.util.List;
import java.util.Optional;

import org.fujitsu.codes.etms.exception.DataNotFoundException;
import org.fujitsu.codes.etms.exception.InvalidInputException;
import org.fujitsu.codes.etms.model.data.LeaveRecord;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.query.Query;
import org.springframework.stereotype.Repository;

@Repository
public class LeaveDao {

    private final SessionFactory sessionFactory;

    public LeaveDao(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    public LeaveRecord save(LeaveRecord entity) {
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

    public Optional<LeaveRecord> findById(Long leaveRecordId) {
        try (Session session = sessionFactory.openSession()) {
            return Optional.ofNullable(session.find(LeaveRecord.class, leaveRecordId));
        }
    }

    public LeaveRecord getByIdOrThrow(Long leaveRecordId) {
        if (leaveRecordId == null) {
            throw new InvalidInputException("Leave record id is required");
        }
        return findById(leaveRecordId)
                .orElseThrow(() -> new DataNotFoundException("Leave record not found: " + leaveRecordId));
    }

    public List<LeaveRecord> findAll() {
        try (Session session = sessionFactory.openSession()) {
            return session.createQuery(
                    "from LeaveRecord l order by l.leaveRecordId desc",
                    LeaveRecord.class
            ).getResultList();
        }
    }

    public List<LeaveRecord> findAll(int page, int size) {
        try (Session session = sessionFactory.openSession()) {
            Query<LeaveRecord> query = session.createQuery(
                    "from LeaveRecord l order by l.leaveRecordId desc",
                    LeaveRecord.class
            );
            query.setFirstResult(page * size);
            query.setMaxResults(size);
            return query.getResultList();
        }
    }

    public List<LeaveRecord> findByEmployeeNumber(String employeeNumber) {
        try (Session session = sessionFactory.openSession()) {
            return session.createQuery(
                    "from LeaveRecord l where lower(l.employeeNumber) = lower(:employeeNumber) order by l.leaveRecordId desc",
                    LeaveRecord.class
            ).setParameter("employeeNumber", employeeNumber).getResultList();
        }
    }

    public List<LeaveRecord> findByStatus(String status) {
        try (Session session = sessionFactory.openSession()) {
            return session.createQuery(
                    "from LeaveRecord l where lower(l.status) = lower(:status) order by l.leaveRecordId desc",
                    LeaveRecord.class
            ).setParameter("status", status).getResultList();
        }
    }

    public List<LeaveRecord> search(String employeeNumber, String leaveType, String status, java.time.LocalDate startDate, java.time.LocalDate endDate) {
        try (Session session = sessionFactory.openSession()) {
            StringBuilder hql = new StringBuilder("from LeaveRecord l where 1=1");

            if (employeeNumber != null && !employeeNumber.isBlank()) {
                hql.append(" and lower(l.employeeNumber) like :employeeNumber");
            }
            if (leaveType != null && !leaveType.isBlank()) {
                hql.append(" and lower(l.leaveType) like :leaveType");
            }
            if (status != null && !status.isBlank()) {
                hql.append(" and lower(l.status) like :status");
            }
            if (startDate != null) {
                hql.append(" and l.startDate >= :startDate");
            }
            if (endDate != null) {
                hql.append(" and l.startDate <= :endDate");
            }

            hql.append(" order by l.leaveRecordId desc");

            Query<LeaveRecord> query = session.createQuery(hql.toString(), LeaveRecord.class);

            if (employeeNumber != null && !employeeNumber.isBlank()) {
                query.setParameter("employeeNumber", "%" + employeeNumber.trim().toLowerCase() + "%");
            }
            if (leaveType != null && !leaveType.isBlank()) {
                query.setParameter("leaveType", "%" + leaveType.trim().toLowerCase() + "%");
            }
            if (status != null && !status.isBlank()) {
                query.setParameter("status", "%" + status.trim().toLowerCase() + "%");
            }
            if (startDate != null) {
                query.setParameter("startDate", startDate);
            }
            if (endDate != null) {
                query.setParameter("endDate", endDate);
            }

            return query.getResultList();
        }
    }

    public List<LeaveRecord> search(String employeeNumber, String leaveType, String status) {
        return search(employeeNumber, leaveType, status, null, null);
    }

    public long countAll() {
        try (Session session = sessionFactory.openSession()) {
            Long count = session.createQuery(
                    "select count(l) from LeaveRecord l",
                    Long.class
            ).uniqueResult();
            return count == null ? 0L : count;
        }
    }

    public Optional<LeaveRecord> update(Long leaveRecordId, LeaveRecord source) {
        Transaction tx = null;
        try (Session session = sessionFactory.openSession()) {
            tx = session.beginTransaction();

            LeaveRecord target = session.find(LeaveRecord.class, leaveRecordId);
            if (target == null) {
                tx.commit();
                return Optional.empty();
            }

            target.setLeaveType(source.getLeaveType());
            target.setStartDate(source.getStartDate());
            target.setEndDate(source.getEndDate());
            target.setStatus(source.getStatus());
            target.setRemarks(source.getRemarks());

            session.merge(target);
            tx.commit();
            return Optional.of(target);
        } catch (RuntimeException ex) {
            rollback(tx);
            throw ex;
        }
    }

    public boolean deleteById(Long leaveRecordId) {
        Transaction tx = null;
        try (Session session = sessionFactory.openSession()) {
            tx = session.beginTransaction();

            LeaveRecord target = session.find(LeaveRecord.class, leaveRecordId);
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

    public void validateStatus(String status) {
        if (status == null || status.trim().isEmpty()) {
            throw new InvalidInputException("Leave status is required");
        }
    }
}
