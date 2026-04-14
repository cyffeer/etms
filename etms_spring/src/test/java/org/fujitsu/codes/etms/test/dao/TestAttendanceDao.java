package org.fujitsu.codes.etms.test.dao;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;

import org.fujitsu.codes.etms.model.dao.AttendanceDao;
import org.fujitsu.codes.etms.model.data.AttendanceRecord;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class TestAttendanceDao {

    @Mock
    private SessionFactory sessionFactory;

    @Mock
    private Session session;

    @Mock
    private Query<AttendanceRecord> query;

    @InjectMocks
    private AttendanceDao attendanceDao;

    @Test
    void searchShouldReturnMatchingAttendanceRecords() {
        AttendanceRecord record = org.mockito.Mockito.mock(AttendanceRecord.class);

        when(sessionFactory.openSession()).thenReturn(session);
        when(session.createQuery(anyString(), eq(AttendanceRecord.class))).thenReturn(query);
        when(query.setParameter(anyString(), org.mockito.ArgumentMatchers.any())).thenReturn(query);
        when(query.getResultList()).thenReturn(List.of(record));

        List<AttendanceRecord> result = attendanceDao.search("E1001", 2026, 4);

        assertEquals(1, result.size());
        verify(query).setParameter("employeeNumber", "%e1001%");
        verify(query).setParameter("year", 2026);
        verify(query).setParameter("month", 4);
    }

    @Test
    void findByEmployeeNumberShouldReturnRecords() {
        AttendanceRecord record = org.mockito.Mockito.mock(AttendanceRecord.class);

        when(sessionFactory.openSession()).thenReturn(session);
        when(session.createQuery(anyString(), eq(AttendanceRecord.class))).thenReturn(query);
        when(query.setParameter(anyString(), anyString())).thenReturn(query);
        when(query.getResultList()).thenReturn(List.of(record));

        List<AttendanceRecord> result = attendanceDao.findByEmployeeNumber("E1001");

        assertEquals(1, result.size());
    }
}