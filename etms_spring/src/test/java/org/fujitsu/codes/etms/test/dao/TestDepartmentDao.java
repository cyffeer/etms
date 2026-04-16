package org.fujitsu.codes.etms.test.dao;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;

import org.fujitsu.codes.etms.model.dao.DepartmentDao;
import org.fujitsu.codes.etms.model.data.Department;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class TestDepartmentDao {

    @Mock
    private SessionFactory sessionFactory;

    @Mock
    private Session session;

    @Mock
    private Query<Department> query;

    @InjectMocks
    private DepartmentDao departmentDao;

    @Test
    void findAllShouldReturnPagedDepartments() {
        Department department = org.mockito.Mockito.mock(Department.class);

        when(sessionFactory.openSession()).thenReturn(session);
        when(session.createQuery(anyString(), eq(Department.class))).thenReturn(query);
        when(query.setFirstResult(anyInt())).thenReturn(query);
        when(query.setMaxResults(anyInt())).thenReturn(query);
        when(query.getResultList()).thenReturn(List.of(department));

        List<Department> result = departmentDao.findAll(0, 10);

        assertEquals(1, result.size());
        verify(query).setFirstResult(0);
        verify(query).setMaxResults(10);
    }

    @Test
    void countAllShouldReturnTotalCount() {
        when(sessionFactory.openSession()).thenReturn(session);
        @SuppressWarnings("unchecked")
        Query<Long> countQuery = (Query<Long>) org.mockito.Mockito.mock(Query.class);
        when(session.createQuery(anyString(), eq(Long.class))).thenReturn(countQuery);
        when(countQuery.uniqueResult()).thenReturn(3L);

        long result = departmentDao.countAll();

        assertEquals(3L, result);
    }

    @Test
    void searchShouldNormalizeDepartmentCodeFormats() {
        Department department = org.mockito.Mockito.mock(Department.class);

        when(sessionFactory.openSession()).thenReturn(session);
        when(session.createQuery(anyString(), eq(Department.class))).thenReturn(query);
        when(query.setParameter(eq("keyword"), anyString())).thenReturn(query);
        when(query.setParameter(eq("departmentCode"), eq("d001"))).thenReturn(query);
        when(query.getResultList()).thenReturn(List.of(department));

        List<Department> result = departmentDao.search("D001");

        assertEquals(1, result.size());
        verify(query).setParameter("departmentCode", "d001");
    }
}
