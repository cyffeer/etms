package org.fujitsu.codes.etms.test.dao;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import org.fujitsu.codes.etms.model.dao.DeptMembersDao;
import org.fujitsu.codes.etms.model.data.DeptMembers;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class TestDepartmentMembersDao {

    @Mock
    private SessionFactory sessionFactory;

    @Mock
    private Session session;

    @Mock
    private Query<DeptMembers> query;

    @InjectMocks
    private DeptMembersDao deptMembersDao;

    @Test
    void findByEmployeeNumberShouldReturnMemberships() {
        DeptMembers member = org.mockito.Mockito.mock(DeptMembers.class);

        when(sessionFactory.openSession()).thenReturn(session);
        when(session.createQuery(anyString(), eq(DeptMembers.class))).thenReturn(query);
        when(query.setParameter(anyString(), anyString())).thenReturn(query);
        when(query.getResultList()).thenReturn(List.of(member));

        List<DeptMembers> result = deptMembersDao.findByEmployeeNumber("E1001");

        assertEquals(1, result.size());
    }

    @Test
    void findByDepartmentCodeShouldReturnMembers() {
        DeptMembers member = org.mockito.Mockito.mock(DeptMembers.class);

        when(sessionFactory.openSession()).thenReturn(session);
        when(session.createQuery(anyString(), eq(DeptMembers.class))).thenReturn(query);
        when(query.setParameter(anyString(), anyString())).thenReturn(query);
        when(query.getResultList()).thenReturn(List.of(member));

        List<DeptMembers> result = deptMembersDao.findByDepartmentCode("D001");

        assertEquals(1, result.size());
    }

    @Test
    void findByIdShouldReturnOptional() {
        DeptMembers member = org.mockito.Mockito.mock(DeptMembers.class);

        when(sessionFactory.openSession()).thenReturn(session);
        when(session.createQuery(anyString(), eq(DeptMembers.class))).thenReturn(query);
        when(query.setParameter(anyString(), eq(1L))).thenReturn(query);
        when(query.setMaxResults(1)).thenReturn(query);
        when(query.uniqueResultOptional()).thenReturn(Optional.of(member));

        Optional<DeptMembers> result = deptMembersDao.findById(1L);

        assertEquals(Optional.of(member), result);
    }
}
