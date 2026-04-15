package org.fujitsu.codes.etms.test.dao;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import org.fujitsu.codes.etms.model.dao.SkillsInventoryDao;
import org.fujitsu.codes.etms.model.data.SkillsInventory;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class TestSkillsInventoryDao {

    @Mock
    private SessionFactory sessionFactory;

    @Mock
    private Session session;

    @Mock
    private Query<SkillsInventory> query;

    @InjectMocks
    private SkillsInventoryDao skillsInventoryDao;

    @Test
    void findByIdShouldReturnSkillsInventory() {
        SkillsInventory skill = org.mockito.Mockito.mock(SkillsInventory.class);

        when(sessionFactory.openSession()).thenReturn(session);
        when(session.createQuery(anyString(), eq(SkillsInventory.class))).thenReturn(query);
        when(query.setParameter(anyString(), eq(1L))).thenReturn(query);
        when(query.setMaxResults(1)).thenReturn(query);
        when(query.uniqueResultOptional()).thenReturn(Optional.of(skill));

        Optional<SkillsInventory> result = skillsInventoryDao.findById(1L);

        assertTrue(result.isPresent());
        assertEquals(skill, result.get());
    }

    @Test
    void findByEmployeeNumberShouldReturnSkills() {
        SkillsInventory skill = org.mockito.Mockito.mock(SkillsInventory.class);

        when(sessionFactory.openSession()).thenReturn(session);
        when(session.createQuery(anyString(), eq(SkillsInventory.class))).thenReturn(query);
        when(query.setParameter(anyString(), anyString())).thenReturn(query);
        when(query.getResultList()).thenReturn(List.of(skill));

        List<SkillsInventory> result = skillsInventoryDao.findByEmployeeNumber("E1001");

        assertEquals(1, result.size());
    }

    @Test
    void findAllShouldReturnAllSkills() {
        SkillsInventory skill1 = org.mockito.Mockito.mock(SkillsInventory.class);
        SkillsInventory skill2 = org.mockito.Mockito.mock(SkillsInventory.class);

        when(sessionFactory.openSession()).thenReturn(session);
        when(session.createQuery(anyString(), eq(SkillsInventory.class))).thenReturn(query);
        when(query.list()).thenReturn(List.of(skill1, skill2));

        List<SkillsInventory> result = skillsInventoryDao.findAll();

        assertEquals(2, result.size());
    }
}
