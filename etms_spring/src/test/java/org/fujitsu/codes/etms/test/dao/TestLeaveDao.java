package org.fujitsu.codes.etms.test.dao;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import java.util.List;

import org.fujitsu.codes.etms.model.dao.LeaveDao;
import org.fujitsu.codes.etms.model.data.LeaveRecord;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class TestLeaveDao {

    @Mock
    private SessionFactory sessionFactory;

    @Mock
    private Session session;

    @Mock
    private Query<LeaveRecord> query;

    @InjectMocks
    private LeaveDao leaveDao;

    @Test
    void searchShouldReturnMatchingLeaveRecords() {
        LeaveRecord record = org.mockito.Mockito.mock(LeaveRecord.class);

        when(sessionFactory.openSession()).thenReturn(session);
        when(session.createQuery(anyString(), eq(LeaveRecord.class))).thenReturn(query);
        when(query.setParameter(anyString(), org.mockito.ArgumentMatchers.any())).thenReturn(query);
        when(query.getResultList()).thenReturn(List.of(record));

        List<LeaveRecord> result = leaveDao.search("E1001", "Vacation", "APPROVED");

        assertEquals(1, result.size());
    }
}