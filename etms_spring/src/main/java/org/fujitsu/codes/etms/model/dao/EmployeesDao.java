package org.fujitsu.codes.etms.model.dao;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.fujitsu.codes.etms.exception.DataNotFoundException;
import org.fujitsu.codes.etms.exception.InvalidInputException;
import org.fujitsu.codes.etms.model.data.Employees;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.query.Query;
import org.springframework.stereotype.Repository;

@Repository
public class EmployeesDao {

    private final SessionFactory sessionFactory;

    public EmployeesDao(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    public Employees save(Employees employee) {
        Transaction tx = null;
        try (Session session = sessionFactory.openSession()) {
            tx = session.beginTransaction();
            session.persist(employee);
            tx.commit();
            return employee;
        } catch (RuntimeException ex) {
            if (tx != null) {
                tx.rollback();
            }
            throw ex;
        }
    }

    public Optional<Employees> findById(Long employeeId) {
        try (Session session = sessionFactory.openSession()) {
            return Optional.ofNullable(session.find(Employees.class, employeeId));
        }
    }

    public Employees getByIdOrThrow(Long employeeId) {
        if (employeeId == null) {
            throw new InvalidInputException("Employee id is required");
        }
        return findById(employeeId)
                .orElseThrow(() -> new DataNotFoundException("Employee not found: " + employeeId));
    }

    public void validateEmployeeNumber(String employeeNumber) {
        if (employeeNumber == null || employeeNumber.trim().isEmpty()) {
            throw new InvalidInputException("Employee number is required");
        }
        if (!existsByEmployeeCode(employeeNumber.trim())) {
            throw new DataNotFoundException("Employee number does not exist: " + employeeNumber);
        }
    }

    public List<Employees> findAll() {
        try (Session session = sessionFactory.openSession()) {
            return session.createQuery(
                    "from Employees e order by e.employeeId asc",
                    Employees.class
            ).list();
        }
    }

    public List<Employees> findAll(int page, int size) {
        try (Session session = sessionFactory.openSession()) {
            Query<Employees> query = session.createQuery(
                    "from Employees e order by e.employeeId asc",
                    Employees.class
            );
            query.setFirstResult(page * size);
            query.setMaxResults(size);
            return query.getResultList();
        }
    }

    public long countAll() {
        try (Session session = sessionFactory.openSession()) {
            Long count = session.createQuery(
                    "select count(e) from Employees e",
                    Long.class
            ).uniqueResult();
            return count == null ? 0L : count;
        }
    }

    public Optional<Employees> update(Long employeeId, Employees source) {
        Transaction tx = null;
        try (Session session = sessionFactory.openSession()) {
            tx = session.beginTransaction();

            Employees target = session.find(Employees.class, employeeId);
            if (target == null) {
                tx.commit();
                return Optional.empty();
            }

            target.setEmployeeCode(source.getEmployeeCode());
            target.setFirstName(source.getFirstName());
            target.setLastName(source.getLastName());
            target.setEmail(source.getEmail());
            target.setHireDate(source.getHireDate());
            target.setActive(source.getActive());
            target.setUpdatedAt(source.getUpdatedAt());

            session.merge(target);
            tx.commit();
            return Optional.of(target);
        } catch (RuntimeException ex) {
            if (tx != null) {
                tx.rollback();
            }
            throw ex;
        }
    }

    public boolean deleteById(Long employeeId) {
        Transaction tx = null;
        try (Session session = sessionFactory.openSession()) {
            tx = session.beginTransaction();

            Employees employee = session.find(Employees.class, employeeId);
            if (employee == null) {
                tx.commit();
                return false;
            }

            session.remove(employee);
            tx.commit();
            return true;
        } catch (RuntimeException ex) {
            if (tx != null) {
                tx.rollback();
            }
            throw ex;
        }
    }

    public boolean existsByEmployeeCode(String employeeCode) {
        try (Session session = sessionFactory.openSession()) {
            Long count = session.createQuery(
                    "select count(e) from Employees e where lower(e.employeeCode) = lower(:employeeCode)",
                    Long.class
            ).setParameter("employeeCode", employeeCode).uniqueResult();
            return count != null && count > 0;
        }
    }

    public boolean existsByEmployeeCodeExceptId(String employeeCode, Long employeeId) {
        try (Session session = sessionFactory.openSession()) {
            Long count = session.createQuery(
                    "select count(e) from Employees e " +
                    "where lower(e.employeeCode) = lower(:employeeCode) and e.employeeId <> :employeeId",
                    Long.class
            )
            .setParameter("employeeCode", employeeCode)
            .setParameter("employeeId", employeeId)
            .uniqueResult();
            return count != null && count > 0;
        }
    }

    public boolean existsByEmail(String email) {
        try (Session session = sessionFactory.openSession()) {
            Long count = session.createQuery(
                    "select count(e) from Employees e where lower(e.email) = lower(:email)",
                    Long.class
            ).setParameter("email", email).uniqueResult();
            return count != null && count > 0;
        }
    }

    public boolean existsByEmailExceptId(String email, Long employeeId) {
        try (Session session = sessionFactory.openSession()) {
            Long count = session.createQuery(
                    "select count(e) from Employees e " +
                    "where lower(e.email) = lower(:email) and e.employeeId <> :employeeId",
                    Long.class
            )
            .setParameter("email", email)
            .setParameter("employeeId", employeeId)
            .uniqueResult();
            return count != null && count > 0;
        }
    }

    public List<Employees> searchByEmployeeNumber(String employeeNumber) {
        if (employeeNumber == null || employeeNumber.isBlank()) {
            return findAll();
        }
        try (Session session = sessionFactory.openSession()) {
            return session.createQuery(
                    "from Employees e where lower(e.employeeCode) like :employeeNumber order by e.employeeId desc",
                    Employees.class
            )
                    .setParameter("employeeNumber", "%" + employeeNumber.trim().toLowerCase() + "%")
                    .getResultList();
        }
    }

    public List<Employees> searchByNameKeyword(String keyword) {
        if (keyword == null || keyword.isBlank()) {
            return findAll();
        }
        try (Session session = sessionFactory.openSession()) {
            return session.createQuery(
                    "from Employees e where " +
                    "lower(e.employeeCode) like :keyword or lower(e.firstName) like :keyword or lower(e.lastName) like :keyword " +
                    "order by e.employeeId desc",
                    Employees.class
            )
                    .setParameter("keyword", "%" + keyword.trim().toLowerCase() + "%")
                    .getResultList();
        }
    }

    public List<Employees> search(String employeeNumber, String nameKeyword) {
        if ((employeeNumber == null || employeeNumber.isBlank())
                && (nameKeyword == null || nameKeyword.isBlank())) {
            return findAll();
        }

        try (Session session = sessionFactory.openSession()) {
            StringBuilder hql = new StringBuilder("from Employees e where 1=1");

            if (employeeNumber != null && !employeeNumber.isBlank()) {
                hql.append(" and lower(e.employeeCode) like :employeeNumber");
            }

            if (nameKeyword != null && !nameKeyword.isBlank()) {
                hql.append(" and (lower(e.employeeCode) like :nameKeyword or lower(e.firstName) like :nameKeyword or lower(e.lastName) like :nameKeyword)");
            }

            hql.append(" order by e.employeeId desc");

            Query<Employees> query = session.createQuery(hql.toString(), Employees.class);

            if (employeeNumber != null && !employeeNumber.isBlank()) {
                query.setParameter("employeeNumber", "%" + employeeNumber.trim().toLowerCase() + "%");
            }

            if (nameKeyword != null && !nameKeyword.isBlank()) {
                query.setParameter("nameKeyword", "%" + nameKeyword.trim().toLowerCase() + "%");
            }

            return query.getResultList();
        }
    }

    public List<Employees> filterByEmploymentDates(LocalDate startDate, LocalDate endDate) {
        if (startDate == null && endDate == null) {
            return findAll();
        }

        StringBuilder hql = new StringBuilder("from Employees e where 1=1");

        if (startDate != null && endDate != null) {
            hql.append(" and e.hireDate between :startDate and :endDate");
        } else if (startDate != null) {
            hql.append(" and e.hireDate >= :startDate");
        } else if (endDate != null) {
            hql.append(" and e.hireDate <= :endDate");
        }

        hql.append(" order by e.employeeId desc");

        try (Session session = sessionFactory.openSession()) {
            Query<Employees> query = session.createQuery(hql.toString(), Employees.class);
            if (startDate != null) {
                query.setParameter("startDate", startDate);
            }
            if (endDate != null) {
                query.setParameter("endDate", endDate);
            }
            return query.getResultList();
        }
    }
}
