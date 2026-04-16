package org.fujitsu.codes.etms.validator;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.fujitsu.codes.etms.model.dao.NpLvlInfoDao;
import org.fujitsu.codes.etms.model.dao.NpTestEmpHistDao;
import org.fujitsu.codes.etms.model.dao.NpTestHistDao;
import org.fujitsu.codes.etms.model.dao.NpTypeDao;
import org.fujitsu.codes.etms.model.data.NpLvlInfo;
import org.fujitsu.codes.etms.model.data.NpTestEmpHist;
import org.fujitsu.codes.etms.model.data.NpTestHist;
import org.fujitsu.codes.etms.model.dto.NpTestEmpHistRequest;
import org.fujitsu.codes.etms.util.NihongoAllowanceUtil;
import org.springframework.stereotype.Component;

@Component
public class NpTestEmpHistValidator {

    private final NpTestEmpHistDao npTestEmpHistDao;
    private final NpTestHistDao npTestHistDao;
    private final NpLvlInfoDao npLvlInfoDao;
    private final NpTypeDao npTypeDao;

    public NpTestEmpHistValidator(
            NpTestEmpHistDao npTestEmpHistDao,
            NpTestHistDao npTestHistDao,
            NpLvlInfoDao npLvlInfoDao,
            NpTypeDao npTypeDao) {
        this.npTestEmpHistDao = npTestEmpHistDao;
        this.npTestHistDao = npTestHistDao;
        this.npLvlInfoDao = npLvlInfoDao;
        this.npTypeDao = npTypeDao;
    }

    public List<String> validateBusinessRules(NpTestEmpHistRequest request) {
        List<String> errors = new ArrayList<>();
        if (request == null) {
            errors.add("Request is required");
            return errors;
        }

        Optional<NpTestHist> currentTestOpt = npTestHistDao.findById(request.getNpTestHistId());
        if (currentTestOpt.isEmpty()) {
            errors.add("Test id does not exist");
            return errors;
        }

        NpTestHist currentTest = currentTestOpt.get();
        Optional<NpLvlInfo> currentLevelOpt = npLvlInfoDao.findByCode(currentTest.getNpLvlInfoCode());

        // NpLvlInfo dependency check
        if (currentLevelOpt.isEmpty()) {
            errors.add("NP level info for test does not exist");
        }

        // Basic flag consistency
        if (Boolean.FALSE.equals(request.getTakeFlag()) && Boolean.TRUE.equals(request.getPassFlag())) {
            errors.add("Pass flag cannot be true when take flag is false");
        }

        List<NpTestEmpHist> employeeHistory = npTestEmpHistDao.findByEmployeeNumber(request.getEmployeeNumber());

        Integer highestPassedRank = resolveHighestPassedRank(employeeHistory);
        Integer requestedRank = currentLevelOpt
                .map(level -> NihongoAllowanceUtil.extractPolicyGroupRank(
                        level.getNpLvlInfoCode(),
                        level.getNpLvlInfoName(),
                        level.getNpTypeCode()))
                .orElse(null);

        if (NihongoAllowanceUtil.shouldDisallowLowerRankExamAfterHigherPass(highestPassedRank, requestedRank)) {
            errors.add("Lower-rank exam is not allowed after passing a higher preferred level");
        }

        return errors;
    }

    public boolean checkFirstTimePassCondition(NpTestEmpHistRequest request) {
        if (request == null || request.getEmployeeNumber() == null) {
            return false;
        }
        List<NpTestEmpHist> employeeHistory = npTestEmpHistDao.findByEmployeeNumber(request.getEmployeeNumber());
        return NihongoAllowanceUtil.isFirstTimePass(Boolean.TRUE.equals(request.getPassFlag()), employeeHistory);
    }

    public boolean isKnownNpTypeCode(String npTypeCode) {
        if (npTypeCode == null || npTypeCode.isBlank()) {
            return false;
        }
        return npTypeDao.existsByCode(npTypeCode.trim());
    }

    private Integer resolveHighestPassedRank(List<NpTestEmpHist> employeeHistory) {
        if (employeeHistory == null || employeeHistory.isEmpty()) {
            return null;
        }

        return employeeHistory.stream()
                .filter(h -> Boolean.TRUE.equals(h.getPassFlag()))
                .map(h -> npTestHistDao.findById(h.getNpTestHistId()))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .map(test -> npLvlInfoDao.findByCode(test.getNpLvlInfoCode()))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .map(level -> NihongoAllowanceUtil.extractPolicyGroupRank(
                        level.getNpLvlInfoCode(),
                        level.getNpLvlInfoName(),
                        level.getNpTypeCode()))
                .filter(rank -> rank != null)
                .min(Integer::compareTo)
                .orElse(null);
    }
}
