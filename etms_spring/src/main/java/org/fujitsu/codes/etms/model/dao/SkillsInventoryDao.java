package org.fujitsu.codes.etms.model.dao;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.fujitsu.codes.etms.model.data.SkillsInventory;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.springframework.stereotype.Repository;

@Repository
public class SkillsInventoryDao {

    private final SessionFactory sessionFactory;

    public SkillsInventoryDao(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    public SkillsInventory save(SkillsInventory entity) {
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

    public Optional<SkillsInventory> findById(Long skillsInventoryId) {
        try (Session session = sessionFactory.openSession()) {
            return findByGeneratedId(session, skillsInventoryId);
        }
    }

    public List<SkillsInventory> findAll() {
        try (Session session = sessionFactory.openSession()) {
            String jpql = "select si from SkillsInventory si order by si.skillsInventoryId desc";
            return session.createQuery(jpql, SkillsInventory.class).list();
        }
    }

    public List<SkillsInventory> findByEmployeeNumber(Integer employeeNumber) {
        try (Session session = sessionFactory.openSession()) {
            return session.createQuery(
                    "from SkillsInventory s where s.employeeNumber = :employeeNumber order by s.skillsInventoryId desc",
                    SkillsInventory.class
            ).setParameter("employeeNumber", employeeNumber).getResultList();
        }
    }

    public List<SkillsInventory> findByEmployeeNumber(String employeeNumber) {
        if (employeeNumber == null || employeeNumber.isBlank()) {
            return findAll();
        }
        try (Session session = sessionFactory.openSession()) {
            return session.createQuery(
                    "from SkillsInventory s where lower(str(s.employeeNumber)) like :employeeNumber order by s.skillsInventoryId desc",
                    SkillsInventory.class
            ).setParameter("employeeNumber", "%" + employeeNumber.trim().toLowerCase() + "%").getResultList();
        }
    }

    public List<SkillsInventory> findBySkillId(Long skillId) {
        try (Session session = sessionFactory.openSession()) {
            return session.createQuery(
                    "from SkillsInventory s where s.skillId = :skillId order by s.skillsInventoryId desc",
                    SkillsInventory.class
            ).setParameter("skillId", skillId).getResultList();
        }
    }

    public boolean existsByEmployeeNumberAndSkillId(Integer employeeNumber, Long skillId) {
        try (Session session = sessionFactory.openSession()) {
            Long count = session.createQuery(
                    "select count(s) from SkillsInventory s " +
                    "where s.employeeNumber = :employeeNumber and s.skillId = :skillId",
                    Long.class
            )
            .setParameter("employeeNumber", employeeNumber)
            .setParameter("skillId", skillId)
            .uniqueResult();
            return count != null && count > 0;
        }
    }

    public Optional<SkillsInventory> update(Long skillsInventoryId, SkillsInventory source) {
        Transaction tx = null;
        try (Session session = sessionFactory.openSession()) {
            tx = session.beginTransaction();

            SkillsInventory target = findByGeneratedId(session, skillsInventoryId).orElse(null);
            if (target == null) {
                tx.commit();
                return Optional.empty();
            }

            target.setSkillLvlId(source.getSkillLvlId());
            target.setUpdatedAt(source.getUpdatedAt());

            session.merge(target);
            tx.commit();
            return Optional.of(target);
        } catch (RuntimeException ex) {
            rollback(tx);
            throw ex;
        }
    }

    public Optional<SkillsInventory> updateSkillLevel(Long skillsInventoryId, Long skillLvlId, LocalDateTime updatedAt) {
        Transaction tx = null;
        try (Session session = sessionFactory.openSession()) {
            tx = session.beginTransaction();

            SkillsInventory target = findByGeneratedId(session, skillsInventoryId).orElse(null);
            if (target == null) {
                tx.commit();
                return Optional.empty();
            }

            target.setSkillLvlId(skillLvlId);
            target.setUpdatedAt(updatedAt == null ? LocalDateTime.now() : updatedAt);

            session.merge(target);
            tx.commit();
            return Optional.of(target);
        } catch (RuntimeException ex) {
            rollback(tx);
            throw ex;
        }
    }

    public boolean deleteById(Long skillsInventoryId) {
        Transaction tx = null;
        try (Session session = sessionFactory.openSession()) {
            tx = session.beginTransaction();

            SkillsInventory target = findByGeneratedId(session, skillsInventoryId).orElse(null);
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

    private Optional<SkillsInventory> findByGeneratedId(Session session, Long skillsInventoryId) {
        if (skillsInventoryId == null) {
            return Optional.empty();
        }

        return session.createQuery(
                        "from SkillsInventory s where s.skillsInventoryId = :skillsInventoryId",
                        SkillsInventory.class
                )
                .setParameter("skillsInventoryId", skillsInventoryId)
                .setMaxResults(1)
                .uniqueResultOptional();
    }

    private void rollback(Transaction tx) {
        if (tx != null) {
            tx.rollback();
        }
    }

}
