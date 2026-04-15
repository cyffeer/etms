package org.fujitsu.codes.etms.model.dao;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.fujitsu.codes.etms.model.data.EmployeeEvent;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.query.Query;
import org.springframework.stereotype.Repository;

@Repository
public class EmployeeEventDao {

    private final SessionFactory sessionFactory;

    public EmployeeEventDao(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    public EmployeeEvent save(EmployeeEvent entity) {
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

    public Optional<EmployeeEvent> findById(Long employeeEventId) {
        try (Session session = sessionFactory.openSession()) {
            return Optional.ofNullable(session.find(EmployeeEvent.class, employeeEventId));
        }
    }

    public List<EmployeeEvent> findAll() {
        try (Session session = sessionFactory.openSession()) {
            return session.createQuery(
                    "from EmployeeEvent e order by e.effectiveDate desc, e.employeeEventId desc",
                    EmployeeEvent.class
            ).getResultList();
        }
    }

    public List<EmployeeEvent> search(
            String employeeNumber,
            String eventType,
            String status,
            String keyword,
            LocalDate startDate,
            LocalDate endDate) {
        try (Session session = sessionFactory.openSession()) {
            StringBuilder hql = new StringBuilder("from EmployeeEvent e where 1=1");

            if (employeeNumber != null && !employeeNumber.isBlank()) {
                hql.append(" and lower(e.employeeNumber) like :employeeNumber");
            }
            if (eventType != null && !eventType.isBlank()) {
                hql.append(" and lower(e.eventType) = lower(:eventType)");
            }
            if (status != null && !status.isBlank()) {
                hql.append(" and lower(e.status) = lower(:status)");
            }
            if (keyword != null && !keyword.isBlank()) {
                hql.append(" and (lower(e.title) like :keyword or lower(coalesce(e.description, '')) like :keyword or lower(coalesce(e.referenceCode, '')) like :keyword)");
            }
            if (startDate != null) {
                hql.append(" and e.effectiveDate >= :startDate");
            }
            if (endDate != null) {
                hql.append(" and e.effectiveDate <= :endDate");
            }

            hql.append(" order by e.effectiveDate desc, e.employeeEventId desc");

            Query<EmployeeEvent> query = session.createQuery(hql.toString(), EmployeeEvent.class);
            if (employeeNumber != null && !employeeNumber.isBlank()) {
                query.setParameter("employeeNumber", "%" + employeeNumber.trim().toLowerCase() + "%");
            }
            if (eventType != null && !eventType.isBlank()) {
                query.setParameter("eventType", eventType.trim());
            }
            if (status != null && !status.isBlank()) {
                query.setParameter("status", status.trim());
            }
            if (keyword != null && !keyword.isBlank()) {
                query.setParameter("keyword", "%" + keyword.trim().toLowerCase() + "%");
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

    public Optional<EmployeeEvent> update(Long employeeEventId, EmployeeEvent source) {
        Transaction tx = null;
        try (Session session = sessionFactory.openSession()) {
            tx = session.beginTransaction();

            EmployeeEvent target = session.find(EmployeeEvent.class, employeeEventId);
            if (target == null) {
                tx.commit();
                return Optional.empty();
            }

            target.setEmployeeNumber(source.getEmployeeNumber());
            target.setEventType(source.getEventType());
            target.setTitle(source.getTitle());
            target.setDescription(source.getDescription());
            target.setDepartmentCode(source.getDepartmentCode());
            target.setReferenceCode(source.getReferenceCode());
            target.setEffectiveDate(source.getEffectiveDate());
            target.setEndDate(source.getEndDate());
            target.setStatus(source.getStatus());
            target.setUpdatedAt(source.getUpdatedAt());

            session.merge(target);
            tx.commit();
            return Optional.of(target);
        } catch (RuntimeException ex) {
            rollback(tx);
            throw ex;
        }
    }

    public boolean deleteById(Long employeeEventId) {
        Transaction tx = null;
        try (Session session = sessionFactory.openSession()) {
            tx = session.beginTransaction();

            EmployeeEvent target = session.find(EmployeeEvent.class, employeeEventId);
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
