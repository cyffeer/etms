package org.fujitsu.codes.etms.test.dao;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import org.fujitsu.codes.etms.model.dao.TrngTypeDao;
import org.fujitsu.codes.etms.model.data.TrngType;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class TestTrngTypeDao {

    @Mock
    private SessionFactory sessionFactory;

    @Mock
    private Session session;

    @Mock
    private Query<TrngType> query;

    @InjectMocks
    private TrngTypeDao trngTypeDao;

    @Test
    void findByIdShouldReturnTrainingType() {
        TrngType type = org.mockito.Mockito.mock(TrngType.class);

        when(sessionFactory.openSession()).thenReturn(session);
        when(session.find(TrngType.class, 1L)).thenReturn(type);

        Optional<TrngType> result = trngTypeDao.findById(1L);

        assertTrue(result.isPresent());
        assertEquals(type, result.get());
    }

    @Test
    void findAllShouldReturnAllTrainingTypes() {
        TrngType type1 = org.mockito.Mockito.mock(TrngType.class);
        TrngType type2 = org.mockito.Mockito.mock(TrngType.class);

        when(sessionFactory.openSession()).thenReturn(session);
        when(session.createQuery(anyString(), eq(TrngType.class))).thenReturn(query);
        when(query.getResultList()).thenReturn(List.of(type1, type2));

        List<TrngType> result = trngTypeDao.findAll();

        assertEquals(2, result.size());
    }
}
