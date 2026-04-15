package org.fujitsu.codes.etms.model.dao;

import java.util.List;
import java.util.Optional;

import org.fujitsu.codes.etms.model.data.SkillLvl;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.springframework.stereotype.Repository;

@Repository
public class SkillLvlDao {

    private final SessionFactory sessionFactory;

    public SkillLvlDao(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    public SkillLvl save(SkillLvl entity) {
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

    public Optional<SkillLvl> findById(Long skillLvlId) {
        try (Session session = sessionFactory.openSession()) {
            return Optional.ofNullable(session.find(SkillLvl.class, skillLvlId));
        }
    }

    public List<SkillLvl> findAll() {
        try (Session session = sessionFactory.openSession()) {
            return session.createQuery(
                    "from SkillLvl s order by s.skillId asc, s.lvlRank asc, s.skillLvlId asc",
                    SkillLvl.class
            ).getResultList();
        }
    }

    public List<SkillLvl> findBySkillId(Long skillId) {
        try (Session session = sessionFactory.openSession()) {
            return session.createQuery(
                    "from SkillLvl sl where sl.skillId = :skillId order by sl.lvlRank, sl.skillLvlId",
                    SkillLvl.class
            )
            .setParameter("skillId", skillId)
            .list();
        }
    }

    public List<SkillLvl> search(Long skillLvlId, Long skillId, String keyword) {
        if (skillLvlId == null && skillId == null && (keyword == null || keyword.isBlank())) {
            return findAll();
        }

        try (Session session = sessionFactory.openSession()) {
            StringBuilder hql = new StringBuilder("from SkillLvl sl where 1=1");

            if (skillLvlId != null) {
                hql.append(" and sl.skillLvlId = :skillLvlId");
            }
            if (skillId != null) {
                hql.append(" and sl.skillId = :skillId");
            }
            if (keyword != null && !keyword.isBlank()) {
                hql.append(" and (lower(sl.lvlCode) like :keyword or lower(sl.lvlName) like :keyword)");
            }

            hql.append(" order by sl.skillId asc, sl.lvlRank asc, sl.skillLvlId asc");

            var query = session.createQuery(hql.toString(), SkillLvl.class);
            if (skillLvlId != null) {
                query.setParameter("skillLvlId", skillLvlId);
            }
            if (skillId != null) {
                query.setParameter("skillId", skillId);
            }
            if (keyword != null && !keyword.isBlank()) {
                query.setParameter("keyword", "%" + keyword.trim().toLowerCase() + "%");
            }

            return query.getResultList();
        }
    }

    public Optional<SkillLvl> update(Long skillLvlId, SkillLvl source) {
        Transaction tx = null;
        try (Session session = sessionFactory.openSession()) {
            tx = session.beginTransaction();

            SkillLvl target = session.find(SkillLvl.class, skillLvlId);
            if (target == null) {
                tx.commit();
                return Optional.empty();
            }

            target.setSkillId(source.getSkillId());
            target.setLvlCode(source.getLvlCode());
            target.setLvlName(source.getLvlName());
            target.setLvlRank(source.getLvlRank());
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

    public boolean deleteById(Long skillLvlId) {
        Transaction tx = null;
        try (Session session = sessionFactory.openSession()) {
            tx = session.beginTransaction();

            SkillLvl target = session.find(SkillLvl.class, skillLvlId);
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

    public boolean existsBySkillIdAndLvlCode(Long skillId, String lvlCode) {
        try (Session session = sessionFactory.openSession()) {
            Long count = session.createQuery(
                    "select count(s.skillLvlId) from SkillLvl s " +
                    "where s.skillId = :skillId and lower(s.lvlCode) = lower(:lvlCode)",
                    Long.class
            )
            .setParameter("skillId", skillId)
            .setParameter("lvlCode", lvlCode)
            .uniqueResult();

            return count != null && count > 0;
        }
    }

    public boolean existsBySkillIdAndLvlCodeExceptId(Long skillId, String lvlCode, Long skillLvlId) {
        try (Session session = sessionFactory.openSession()) {
            Long count = session.createQuery(
                    "select count(s.skillLvlId) from SkillLvl s " +
                    "where s.skillId = :skillId and lower(s.lvlCode) = lower(:lvlCode) and s.skillLvlId <> :id",
                    Long.class
            )
            .setParameter("skillId", skillId)
            .setParameter("lvlCode", lvlCode)
            .setParameter("id", skillLvlId)
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
