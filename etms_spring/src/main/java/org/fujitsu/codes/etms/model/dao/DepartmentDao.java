package org.fujitsu.codes.etms.model.dao;

import java.util.List;
import java.util.Optional;

import org.fujitsu.codes.etms.model.data.Department;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.query.Query;
import org.springframework.stereotype.Repository;

@Repository
public class DepartmentDao {

    private final SessionFactory sessionFactory;

    public DepartmentDao(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    public Department save(Department department) {
        Transaction tx = null;
        try (Session session = sessionFactory.openSession()) {
            tx = session.beginTransaction();
            session.persist(department);
            tx.commit();
            return department;
        } catch (RuntimeException ex) {
            rollback(tx);
            throw ex;
        }
    }

    public Department findById(Long departmentId) {
        try (Session session = sessionFactory.openSession()) {
            return session.find(Department.class, departmentId);
        }
    }

    public List<Department> findAll() {
        try (Session session = sessionFactory.openSession()) {
            return session.createQuery(
                    "from Department d order by d.departmentId asc",
                    Department.class
            ).getResultList();
        }
    }

    public List<Department> findAll(int page, int size) {
        try (Session session = sessionFactory.openSession()) {
            Query<Department> query = session.createQuery(
                    "from Department d order by d.departmentId asc",
                    Department.class
            );
            query.setFirstResult(page * size);
            query.setMaxResults(size);
            return query.getResultList();
        }
    }

    public List<Department> search(String keyword) {
        if (keyword == null || keyword.isBlank()) {
            return findAll();
        }

        try (Session session = sessionFactory.openSession()) {
            return session.createQuery(
                    "from Department d where lower(d.departmentCode) like :keyword or lower(d.departmentName) like :keyword " +
                    "order by d.departmentId asc",
                    Department.class
            ).setParameter("keyword", "%" + keyword.trim().toLowerCase() + "%")
             .getResultList();
        }
    }

    public long countAll() {
        try (Session session = sessionFactory.openSession()) {
            Long count = session.createQuery(
                    "select count(d) from Department d",
                    Long.class
            ).uniqueResult();
            return count == null ? 0L : count;
        }
    }

    public Optional<Department> update(Long departmentId, Department source) {
        Transaction tx = null;
        try (Session session = sessionFactory.openSession()) {
            tx = session.beginTransaction();

            Department target = session.find(Department.class, departmentId);
            if (target == null) {
                tx.commit();
                return Optional.empty();
            }

            target.setDepartmentCode(source.getDepartmentCode());
            target.setDepartmentName(source.getDepartmentName());
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

    public boolean deleteById(Long departmentId) {
        Transaction tx = null;
        try (Session session = sessionFactory.openSession()) {
            tx = session.beginTransaction();

            Department department = session.find(Department.class, departmentId);
            if (department == null) {
                tx.commit();
                return false;
            }

            session.remove(department);
            tx.commit();
            return true;
        } catch (RuntimeException ex) {
            rollback(tx);
            throw ex;
        }
    }

    public boolean existsByDepartmentCode(String departmentCode) {
        try (Session session = sessionFactory.openSession()) {
            Long count = session.createQuery(
                    "select count(d.departmentId) from Department d where lower(d.departmentCode) = lower(:departmentCode)",
                    Long.class
            ).setParameter("departmentCode", departmentCode).uniqueResult();
            return count != null && count > 0;
        }
    }

    public boolean existsByDepartmentCodeExceptId(String departmentCode, Long departmentId) {
        try (Session session = sessionFactory.openSession()) {
            Long count = session.createQuery(
                    "select count(d.departmentId) from Department d " +
                    "where lower(d.departmentCode) = lower(:departmentCode) and d.departmentId <> :departmentId",
                    Long.class
            )
            .setParameter("departmentCode", departmentCode)
            .setParameter("departmentId", departmentId)
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
