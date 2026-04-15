package org.fujitsu.codes.etms.test.dao;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import org.fujitsu.codes.etms.model.dao.VisaInfoDao;
import org.fujitsu.codes.etms.model.data.VisaInfo;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class TestVisaInfoDao {

    @Mock
    private SessionFactory sessionFactory;

    @Mock
    private Session session;

    @Mock
    private Query<VisaInfo> query;

    @InjectMocks
    private VisaInfoDao visaInfoDao;

    @Test
    void findByIdShouldReturnVisaInfo() {
        VisaInfo visa = org.mockito.Mockito.mock(VisaInfo.class);

        when(sessionFactory.openSession()).thenReturn(session);
        when(session.createQuery(anyString(), eq(VisaInfo.class))).thenReturn(query);
        when(query.setParameter(anyString(), eq(1L))).thenReturn(query);
        when(query.setMaxResults(1)).thenReturn(query);
        when(query.uniqueResultOptional()).thenReturn(Optional.of(visa));

        Optional<VisaInfo> result = visaInfoDao.findById(1L);

        assertTrue(result.isPresent());
        assertEquals(visa, result.get());
    }

    @Test
    void findByEmployeeNumberShouldReturnVisaRecords() {
        VisaInfo visa = org.mockito.Mockito.mock(VisaInfo.class);

        when(sessionFactory.openSession()).thenReturn(session);
        when(session.createQuery(anyString(), eq(VisaInfo.class))).thenReturn(query);
        when(query.setParameter(anyString(), anyString())).thenReturn(query);
        when(query.getResultList()).thenReturn(List.of(visa));

        List<VisaInfo> result = visaInfoDao.findByEmployeeNumber("E1001");

        assertEquals(1, result.size());
    }

    @Test
    void findAllShouldReturnAllVisaRecords() {
        VisaInfo visa1 = org.mockito.Mockito.mock(VisaInfo.class);
        VisaInfo visa2 = org.mockito.Mockito.mock(VisaInfo.class);

        when(sessionFactory.openSession()).thenReturn(session);
        when(session.createQuery(anyString(), eq(VisaInfo.class))).thenReturn(query);
        when(query.getResultList()).thenReturn(List.of(visa1, visa2));

        List<VisaInfo> result = visaInfoDao.findAll();

        assertEquals(2, result.size());
    }
}
