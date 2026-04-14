package org.fujitsu.codes.etms.test.dao;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.fujitsu.codes.etms.exception.InvalidInputException;
import org.fujitsu.codes.etms.model.dao.LoginDao;
import org.fujitsu.codes.etms.model.data.Login;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class TestLoginDao {

    @Mock
    private SessionFactory sessionFactory;

    @Mock
    private Session session;

    @Mock
    private Query<Login> query;

    @InjectMocks
    private LoginDao loginDao;

    @Test
    void findByUsernameShouldReturnLoginWhenFound() {
        Login login = org.mockito.Mockito.mock(Login.class);

        when(sessionFactory.openSession()).thenReturn(session);
        when(session.createQuery(anyString(), eq(Login.class))).thenReturn(query);
        when(query.setParameter(anyString(), anyString())).thenReturn(query);
        when(query.uniqueResultOptional()).thenReturn(Optional.of(login));

        Optional<Login> result = loginDao.findByUsername("admin");

        assertTrue(result.isPresent());
    }

    @Test
    void authenticateShouldThrowWhenCredentialsAreInvalid() {
        when(sessionFactory.openSession()).thenReturn(session);
        when(session.createQuery(anyString(), eq(Login.class))).thenReturn(query);
        when(query.setParameter(anyString(), anyString())).thenReturn(query);
        when(query.uniqueResultOptional()).thenReturn(Optional.empty());

        try {
            loginDao.authenticate("admin", "badpass");
        } catch (InvalidInputException ex) {
            assertEquals("Invalid username or password", ex.getMessage());
        }
    }
}