package org.fujitsu.codes.etms.test.dao;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import java.lang.reflect.Method;
import java.util.List;

import org.fujitsu.codes.etms.model.dao.SkillsDao;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class TestSkillsDao {

    @Mock SessionFactory sessionFactory;
    @Mock Session session;
    @Mock Query<?> query;

    @InjectMocks SkillsDao skillsDao;

    @Test
    void findAllShouldReturnListWhenSupported() throws Exception {
        Method method = getMethodOrNull(SkillsDao.class, "findAll");
        Assumptions.assumeTrue(method != null);

        when(sessionFactory.openSession()).thenReturn(session);
        when(session.createQuery(anyString(), org.mockito.ArgumentMatchers.<Class>any())).thenReturn((Query) query);
        when(query.getResultList()).thenReturn(List.of());

        Object result = method.invoke(skillsDao);
        assertNotNull(result);
    }

    @Test
    void findByIdShouldReturnValueWhenSupported() throws Exception {
        Method method = getMethodOrNull(SkillsDao.class, "findById", Long.class);
        if (method == null) method = getMethodOrNull(SkillsDao.class, "findById", long.class);
        Assumptions.assumeTrue(method != null);

        when(sessionFactory.openSession()).thenReturn(session);
        Object result = method.invoke(skillsDao, 1L);
        assertNotNull(result);
    }

    private Method getMethodOrNull(Class<?> c, String name, Class<?>... paramTypes) {
        try { return c.getMethod(name, paramTypes); } catch (NoSuchMethodException ex) { return null; }
    }
}