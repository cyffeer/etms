package org.fujitsu.codes.etms.model.dao;

import java.util.List;
import java.util.Optional;

import org.fujitsu.codes.etms.model.data.Skills;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.springframework.stereotype.Repository;

@Repository
public class SkillsDao {

    private final SessionFactory sessionFactory;

    public SkillsDao(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    public Skills save(Skills entity) {
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

    public Optional<Skills> findById(Long skillId) {
        try (Session session = sessionFactory.openSession()) {
            return Optional.ofNullable(session.find(Skills.class, skillId));
        }
    }

    public List<Skills> findAll() {
        try (Session session = sessionFactory.openSession()) {
            return session.createQuery("from Skills s order by s.skillId asc", Skills.class).getResultList();
        }
    }

    public Optional<Skills> update(Long skillId, Skills source) {
        Transaction tx = null;
        try (Session session = sessionFactory.openSession()) {
            tx = session.beginTransaction();

            Skills target = session.find(Skills.class, skillId);
            if (target == null) {
                tx.commit();
                return Optional.empty();
            }

            target.setSkillCode(source.getSkillCode());
            target.setSkillName(source.getSkillName());
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

    public boolean deleteById(Long skillId) {
        Transaction tx = null;
        try (Session session = sessionFactory.openSession()) {
            tx = session.beginTransaction();

            Skills target = session.find(Skills.class, skillId);
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

    public boolean existsByCode(String skillCode) {
        try (Session session = sessionFactory.openSession()) {
            Long count = session.createQuery(
                    "select count(s.skillId) from Skills s where lower(s.skillCode) = lower(:code)",
                    Long.class
            ).setParameter("code", skillCode).uniqueResult();
            return count != null && count > 0;
        }
    }

    public boolean existsByCodeExceptId(String skillCode, Long skillId) {
        try (Session session = sessionFactory.openSession()) {
            Long count = session.createQuery(
                    "select count(s.skillId) from Skills s " +
                    "where lower(s.skillCode) = lower(:code) and s.skillId <> :id",
                    Long.class
            )
            .setParameter("code", skillCode)
            .setParameter("id", skillId)
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
