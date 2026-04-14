package org.fujitsu.codes.etms.test.validator;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.fujitsu.codes.etms.model.dao.NpLvlInfoDao;
import org.fujitsu.codes.etms.model.dao.NpTestEmpHistDao;
import org.fujitsu.codes.etms.model.dao.NpTestHistDao;
import org.fujitsu.codes.etms.model.dao.NpTypeDao;
import org.fujitsu.codes.etms.validator.NpTestEmpHistValidator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class TestNpTestEmpHistValidator {

    @Mock
    private NpTestEmpHistDao npTestEmpHistDao;

    @Mock
    private NpTestHistDao npTestHistDao;

    @Mock
    private NpLvlInfoDao npLvlInfoDao;

    @Mock
    private NpTypeDao npTypeDao;

    @InjectMocks
    private NpTestEmpHistValidator validator;

    @Test
    void validateBusinessRulesShouldReturnErrorWhenRequestIsNull() {
        List<String> errors = validator.validateBusinessRules(null);

        assertFalse(errors.isEmpty());
        assertTrue(errors.contains("Request is required"));
    }

    @Test
    void checkFirstTimePassConditionShouldReturnFalseForNullRequest() {
        assertFalse(validator.checkFirstTimePassCondition(null));
    }

    @Test
    void isKnownNpTypeCodeShouldReturnFalseForBlankCode() {
        assertFalse(validator.isKnownNpTypeCode(" "));
    }
}