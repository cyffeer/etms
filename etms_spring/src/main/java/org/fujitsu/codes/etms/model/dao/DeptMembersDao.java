package org.fujitsu.codes.etms.model.dao;

import java.util.List;
import java.util.Optional;

import org.fujitsu.codes.etms.model.data.DeptMembers;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.springframework.stereotype.Repository;

@Repository
public class DeptMembersDao {

    private final SessionFactory sessionFactory;

    public DeptMembersDao(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    public DeptMembers save(DeptMembers deptMembers) {
        Transaction tx = null;
        try (Session session = sessionFactory.openSession()) {
            tx = session.beginTransaction();
            session.persist(deptMembers);
            tx.commit();
            return deptMembers;
        } catch (RuntimeException ex) {
            rollback(tx);
            throw ex;
        }
    }

    public Optional<DeptMembers> findById(Long deptMemberId) {
        try (Session session = sessionFactory.openSession()) {
            return findByDeptMemberId(session, deptMemberId);
        }
    }

    public List<DeptMembers> findAll() {
        try (Session session = sessionFactory.openSession()) {
            return session.createQuery(
                    "from DeptMembers dm order by dm.deptMemberId asc",
                    DeptMembers.class
            ).getResultList();
        }
    }

    public List<DeptMembers> findByEmployeeNumber(String employeeNumber) {
        try (Session session = sessionFactory.openSession()) {
            return session.createQuery(
                    "from DeptMembers dm where lower(dm.employeeNumber) = lower(:employeeNumber) " +
                    "order by dm.memberStart desc, dm.deptMemberId desc",
                    DeptMembers.class
            ).setParameter("employeeNumber", employeeNumber).getResultList();
        }
    }

    public List<DeptMembers> findByDepartmentCode(String departmentCode) {
        try (Session session = sessionFactory.openSession()) {
            return session.createQuery(
                    "from DeptMembers dm where lower(dm.departmentCode) = lower(:departmentCode) " +
                    "order by dm.memberStart desc, dm.deptMemberId desc",
                    DeptMembers.class
            ).setParameter("departmentCode", departmentCode).getResultList();
        }
    }

    public List<DeptMembers> search(String departmentCode, String employeeNumber) {
        if ((departmentCode == null || departmentCode.isBlank())
                && (employeeNumber == null || employeeNumber.isBlank())) {
            return findAll();
        }

        try (Session session = sessionFactory.openSession()) {
            StringBuilder hql = new StringBuilder("from DeptMembers dm where 1=1");

            if (departmentCode != null && !departmentCode.isBlank()) {
                hql.append(" and lower(dm.departmentCode) = lower(:departmentCode)");
            }
            if (employeeNumber != null && !employeeNumber.isBlank()) {
                hql.append(" and lower(dm.employeeNumber) = lower(:employeeNumber)");
            }

            hql.append(" order by dm.memberStart desc, dm.deptMemberId desc");

            var query = session.createQuery(hql.toString(), DeptMembers.class);
            if (departmentCode != null && !departmentCode.isBlank()) {
                query.setParameter("departmentCode", departmentCode.trim());
            }
            if (employeeNumber != null && !employeeNumber.isBlank()) {
                query.setParameter("employeeNumber", employeeNumber.trim());
            }
            return query.getResultList();
        }
    }

    public Optional<DeptMembers> update(Long deptMemberId, DeptMembers source) {
        Transaction tx = null;
        try (Session session = sessionFactory.openSession()) {
            tx = session.beginTransaction();

            DeptMembers target = findByDeptMemberId(session, deptMemberId).orElse(null);
            if (target == null) {
                tx.commit();
                return Optional.empty();
            }

            target.setMemberTypeId(source.getMemberTypeId());
            target.setMemberStart(source.getMemberStart());
            target.setMemberEnd(source.getMemberEnd());
            target.setUpdatedAt(source.getUpdatedAt());

            session.merge(target);
            tx.commit();
            return Optional.of(target);
        } catch (RuntimeException ex) {
            rollback(tx);
            throw ex;
        }
    }

    public boolean deleteById(Long deptMemberId) {
        Transaction tx = null;
        try (Session session = sessionFactory.openSession()) {
            tx = session.beginTransaction();

            DeptMembers target = findByDeptMemberId(session, deptMemberId).orElse(null);
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

    private Optional<DeptMembers> findByDeptMemberId(Session session, Long deptMemberId) {
        if (deptMemberId == null) {
            return Optional.empty();
        }

        return session.createQuery(
                        "from DeptMembers dm where dm.deptMemberId = :deptMemberId",
                        DeptMembers.class
                )
                .setParameter("deptMemberId", deptMemberId)
                .setMaxResults(1)
                .uniqueResultOptional();
    }

    private void rollback(Transaction tx) {
        if (tx != null) {
            tx.rollback();
        }
    }
}
