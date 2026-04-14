package org.fujitsu.codes.etms.test.dao;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;

import org.fujitsu.codes.etms.model.dao.TrngHistDao;
import org.fujitsu.codes.etms.model.data.TrngHist;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class TestTrainingHistoryDao {

    @Mock
    private SessionFactory sessionFactory;

    @Mock
    private Session session;

    @Mock
    private Query<TrngHist> query;

    @InjectMocks
    private TrngHistDao trngHistDao;

    @Test
    void findAllShouldReturnPagedTrainingHistory() {
        TrngHist record = org.mockito.Mockito.mock(TrngHist.class);

        when(sessionFactory.openSession()).thenReturn(session);
        when(session.createQuery(anyString(), eq(TrngHist.class))).thenReturn(query);
        when(query.setFirstResult(anyInt())).thenReturn(query);
        when(query.setMaxResults(anyInt())).thenReturn(query);
        when(query.getResultList()).thenReturn(List.of(record));

        List<TrngHist> result = trngHistDao.findAll(0, 10);

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
        when(countQuery.uniqueResult()).thenReturn(5L);

        long result = trngHistDao.countAll();

        assertEquals(5L, result);
    }

    @Test
    void findByEmployeeNumberShouldReturnTrainingRecords() {
        TrngHist record = org.mockito.Mockito.mock(TrngHist.class);

        when(sessionFactory.openSession()).thenReturn(session);
        when(session.createQuery(anyString(), eq(TrngHist.class))).thenReturn(query);
        when(query.setParameter(anyString(), anyString())).thenReturn(query);
        when(query.getResultList()).thenReturn(List.of(record));

        List<TrngHist> result = trngHistDao.findByEmployeeNumber("E1001");

        assertEquals(1, result.size());
    }
}