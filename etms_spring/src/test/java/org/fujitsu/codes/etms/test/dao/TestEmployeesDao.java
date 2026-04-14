package org.fujitsu.codes.etms.test.dao;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;

import org.fujitsu.codes.etms.model.dao.EmployeesDao;
import org.fujitsu.codes.etms.model.data.Employees;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class TestEmployeesDao {

    @Mock
    private SessionFactory sessionFactory;

    @Mock
    private Session session;

    @Mock
    private Query<Employees> query;

    @InjectMocks
    private EmployeesDao employeesDao;

    @Test
    void findAllShouldReturnPagedEmployees() {
        Employees employee = org.mockito.Mockito.mock(Employees.class);

        when(sessionFactory.openSession()).thenReturn(session);
        when(session.createQuery(anyString(), eq(Employees.class))).thenReturn(query);
        when(query.setFirstResult(anyInt())).thenReturn(query);
        when(query.setMaxResults(anyInt())).thenReturn(query);
        when(query.getResultList()).thenReturn(List.of(employee));

        List<Employees> result = employeesDao.findAll(0, 10);

        assertEquals(1, result.size());
        verify(query).setFirstResult(0);
        verify(query).setMaxResults(10);
    }

    @Test
    void countAllShouldReturnTotalCount() {
        when(sessionFactory.openSession()).thenReturn(session);
        when(session.createQuery(anyString(), eq(Long.class))).thenReturn(org.mockito.Mockito.mock(Query.class));
        @SuppressWarnings("unchecked")
        Query<Long> countQuery = (Query<Long>) session.createQuery("select count(e) from Employees e", Long.class);
        when(countQuery.uniqueResult()).thenReturn(2L);

        long result = employeesDao.countAll();

        assertEquals(2L, result);
    }
}