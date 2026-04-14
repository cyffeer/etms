package org.fujitsu.codes.etms.model.dao;

import java.util.List;
import java.util.Optional;

import org.fujitsu.codes.etms.model.data.MemberType;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.springframework.stereotype.Repository;

@Repository
public class MemberTypeDao {

    private final SessionFactory sessionFactory;

    public MemberTypeDao(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    public MemberType save(MemberType memberType) {
        Transaction tx = null;
        try (Session session = sessionFactory.openSession()) {
            tx = session.beginTransaction();
            session.persist(memberType);
            tx.commit();
            return memberType;
        } catch (RuntimeException ex) {
            rollback(tx);
            throw ex;
        }
    }

    public Optional<MemberType> findById(Long memberTypeId) {
        try (Session session = sessionFactory.openSession()) {
            return Optional.ofNullable(session.find(MemberType.class, memberTypeId));
        }
    }

    public List<MemberType> findAll() {
        try (Session session = sessionFactory.openSession()) {
            return session.createQuery(
                    "from MemberType mt order by mt.memberTypeId asc",
                    MemberType.class
            ).getResultList();
        }
    }

    public Optional<MemberType> update(Long memberTypeId, MemberType source) {
        Transaction tx = null;
        try (Session session = sessionFactory.openSession()) {
            tx = session.beginTransaction();

            MemberType target = session.find(MemberType.class, memberTypeId);
            if (target == null) {
                tx.commit();
                return Optional.empty();
            }

            target.setMemberTypeCode(source.getMemberTypeCode());
            target.setMemberTypeName(source.getMemberTypeName());
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

    public boolean deleteById(Long memberTypeId) {
        Transaction tx = null;
        try (Session session = sessionFactory.openSession()) {
            tx = session.beginTransaction();

            MemberType target = session.find(MemberType.class, memberTypeId);
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

    public boolean existsByCode(String memberTypeCode) {
        try (Session session = sessionFactory.openSession()) {
            Long count = session.createQuery(
                    "select count(mt.memberTypeId) from MemberType mt where lower(mt.memberTypeCode) = lower(:code)",
                    Long.class
            ).setParameter("code", memberTypeCode).uniqueResult();
            return count != null && count > 0;
        }
    }

    public boolean existsByCodeExceptId(String memberTypeCode, Long memberTypeId) {
        try (Session session = sessionFactory.openSession()) {
            Long count = session.createQuery(
                    "select count(mt.memberTypeId) from MemberType mt " +
                    "where lower(mt.memberTypeCode) = lower(:code) and mt.memberTypeId <> :id",
                    Long.class
            )
            .setParameter("code", memberTypeCode)
            .setParameter("id", memberTypeId)
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
