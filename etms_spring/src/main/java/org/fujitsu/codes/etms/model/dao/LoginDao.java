package org.fujitsu.codes.etms.model.dao;

import java.util.Optional;

import org.fujitsu.codes.etms.exception.InvalidInputException;
import org.fujitsu.codes.etms.model.data.Login;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.springframework.stereotype.Repository;

@Repository
public class LoginDao {

    private final SessionFactory sessionFactory;

    public LoginDao(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    public Login save(Login entity) {
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

    public Optional<Login> findByUsername(String username) {
        try (Session session = sessionFactory.openSession()) {
            return session.createQuery(
                    "from Login l where lower(l.username) = lower(:username)",
                    Login.class
            )
            .setParameter("username", username)
            .uniqueResultOptional();
        }
    }

    public Login authenticate(String username, String password) {
        if (username == null || username.trim().isEmpty()) {
            throw new InvalidInputException("Username is required");
        }
        if (password == null || password.isEmpty()) {
            throw new InvalidInputException("Password is required");
        }

        return findByUsername(username.trim())
                .filter(u -> u.getPassword() != null && u.getPassword().equals(password))
                .orElseThrow(() -> new InvalidInputException("Invalid username or password"));
    }

    private void rollback(Transaction tx) {
        if (tx != null) {
            tx.rollback();
        }
    }
}