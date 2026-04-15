package org.fujitsu.codes.etms.model.dao;

import java.time.LocalDateTime;
import java.util.List;

import org.fujitsu.codes.etms.model.data.AuditLog;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.query.Query;
import org.springframework.stereotype.Repository;

@Repository
public class AuditLogDao {

    private final SessionFactory sessionFactory;

    public AuditLogDao(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    public AuditLog save(AuditLog entity) {
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

    public List<AuditLog> search(
            String username,
            String entityType,
            String action,
            LocalDateTime loggedFrom,
            LocalDateTime loggedTo,
            int limit) {
        try (Session session = sessionFactory.openSession()) {
            StringBuilder hql = new StringBuilder("from AuditLog a where 1=1");

            if (username != null && !username.isBlank()) {
                hql.append(" and lower(a.username) like :username");
            }
            if (entityType != null && !entityType.isBlank()) {
                hql.append(" and lower(a.entityType) = lower(:entityType)");
            }
            if (action != null && !action.isBlank()) {
                hql.append(" and lower(a.action) = lower(:action)");
            }
            if (loggedFrom != null) {
                hql.append(" and a.loggedAt >= :loggedFrom");
            }
            if (loggedTo != null) {
                hql.append(" and a.loggedAt <= :loggedTo");
            }

            hql.append(" order by a.loggedAt desc, a.auditLogId desc");

            Query<AuditLog> query = session.createQuery(hql.toString(), AuditLog.class);
            if (username != null && !username.isBlank()) {
                query.setParameter("username", "%" + username.trim().toLowerCase() + "%");
            }
            if (entityType != null && !entityType.isBlank()) {
                query.setParameter("entityType", entityType.trim());
            }
            if (action != null && !action.isBlank()) {
                query.setParameter("action", action.trim());
            }
            if (loggedFrom != null) {
                query.setParameter("loggedFrom", loggedFrom);
            }
            if (loggedTo != null) {
                query.setParameter("loggedTo", loggedTo);
            }

            query.setMaxResults(Math.max(1, Math.min(limit, 500)));
            return query.getResultList();
        }
    }

    private void rollback(Transaction tx) {
        if (tx != null) {
            tx.rollback();
        }
    }
}
