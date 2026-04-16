package org.fujitsu.codes.etms.model.dao;

import java.util.List;
import java.util.Optional;

import org.fujitsu.codes.etms.model.data.LeaveType;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.query.Query;
import org.springframework.stereotype.Repository;

@Repository
public class LeaveTypeDao {

    private final SessionFactory sessionFactory;

    public LeaveTypeDao(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    public LeaveType save(LeaveType entity) {
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

    public Optional<LeaveType> findById(Long leaveTypeId) {
        try (Session session = sessionFactory.openSession()) {
            return Optional.ofNullable(session.find(LeaveType.class, leaveTypeId));
        }
    }

    public List<LeaveType> findAll() {
        try (Session session = sessionFactory.openSession()) {
            return session.createQuery("from LeaveType l order by l.leaveTypeCode asc", LeaveType.class)
                    .getResultList();
        }
    }

    public List<LeaveType> search(Long leaveTypeId, String keyword) {
        try (Session session = sessionFactory.openSession()) {
            String hql = "from LeaveType l where 1=1";
            if (leaveTypeId != null) {
                hql += " and l.leaveTypeId = :leaveTypeId";
            }
            if (keyword != null && !keyword.isBlank()) {
                hql += " and (lower(l.leaveTypeCode) like :keyword or lower(l.leaveTypeName) like :keyword)";
            }
            hql += " order by l.leaveTypeCode asc";

            Query<LeaveType> query = session.createQuery(hql, LeaveType.class);
            if (leaveTypeId != null) {
                query.setParameter("leaveTypeId", leaveTypeId);
            }
            if (keyword != null && !keyword.isBlank()) {
                query.setParameter("keyword", "%" + keyword.trim().toLowerCase() + "%");
            }
            return query.getResultList();
        }
    }

    public Optional<LeaveType> findByCode(String leaveTypeCode) {
        try (Session session = sessionFactory.openSession()) {
            return session.createQuery(
                            "from LeaveType l where lower(l.leaveTypeCode) = lower(:leaveTypeCode)",
                            LeaveType.class)
                    .setParameter("leaveTypeCode", leaveTypeCode)
                    .uniqueResultOptional();
        }
    }

    public Optional<LeaveType> findByCodeOrName(String value) {
        try (Session session = sessionFactory.openSession()) {
            return session.createQuery(
                            "from LeaveType l where lower(l.leaveTypeCode) = lower(:value) or lower(l.leaveTypeName) = lower(:value)",
                            LeaveType.class)
                    .setParameter("value", value)
                    .uniqueResultOptional();
        }
    }

    public boolean existsByCode(String leaveTypeCode) {
        return findByCode(leaveTypeCode).isPresent();
    }

    public boolean existsByCodeExceptId(String leaveTypeCode, Long leaveTypeId) {
        try (Session session = sessionFactory.openSession()) {
            Long count = session.createQuery(
                            "select count(l) from LeaveType l where lower(l.leaveTypeCode) = lower(:leaveTypeCode) and l.leaveTypeId <> :leaveTypeId",
                            Long.class)
                    .setParameter("leaveTypeCode", leaveTypeCode)
                    .setParameter("leaveTypeId", leaveTypeId)
                    .uniqueResult();
            return count != null && count > 0;
        }
    }

    public Optional<LeaveType> update(Long leaveTypeId, LeaveType source) {
        Transaction tx = null;
        try (Session session = sessionFactory.openSession()) {
            tx = session.beginTransaction();
            LeaveType target = session.find(LeaveType.class, leaveTypeId);
            if (target == null) {
                tx.commit();
                return Optional.empty();
            }

            target.setLeaveTypeCode(source.getLeaveTypeCode());
            target.setLeaveTypeName(source.getLeaveTypeName());
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

    public boolean deleteById(Long leaveTypeId) {
        Transaction tx = null;
        try (Session session = sessionFactory.openSession()) {
            tx = session.beginTransaction();
            LeaveType target = session.find(LeaveType.class, leaveTypeId);
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
