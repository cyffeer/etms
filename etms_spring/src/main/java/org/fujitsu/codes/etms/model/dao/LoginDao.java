package org.fujitsu.codes.etms.model.dao;

import java.util.Optional;

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

    private void rollback(Transaction tx) {
        if (tx != null) {
            tx.rollback();
        }
    }
}
