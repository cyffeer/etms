package org.fujitsu.codes.etms.controller;

import java.net.URI;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.fujitsu.codes.etms.model.dao.EmployeesDao;
import org.fujitsu.codes.etms.model.dao.NpLvlInfoDao;
import org.fujitsu.codes.etms.model.dao.NpTestEmpHistDao;
import org.fujitsu.codes.etms.model.dao.NpTestHistDao;
import org.fujitsu.codes.etms.model.dao.NpTypeDao;
import org.fujitsu.codes.etms.model.data.NpLvlInfo;
import org.fujitsu.codes.etms.model.data.NpTestEmpHist;
import org.fujitsu.codes.etms.model.data.NpTestHist;
import org.fujitsu.codes.etms.model.data.NpType;
import org.fujitsu.codes.etms.model.dto.NpTestEmpHistRequest;
import org.fujitsu.codes.etms.model.dto.NpTestEmpHistResponse;
import org.fujitsu.codes.etms.util.NihongoAllowanceUtil;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/np-test-emp-hist")
public class NpTestEmpHistRestController {

    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(NpTestEmpHistRestController.class);

    private final NpTestEmpHistDao npTestEmpHistDao;
    private final EmployeesDao employeesDao;
    private final NpTestHistDao npTestHistDao;
    private final NpLvlInfoDao npLvlInfoDao;
    private final NpTypeDao npTypeDao;

    public NpTestEmpHistRestController(
            NpTestEmpHistDao npTestEmpHistDao,
            EmployeesDao employeesDao,
            NpTestHistDao npTestHistDao,
            NpLvlInfoDao npLvlInfoDao,
            NpTypeDao npTypeDao) {
        this.npTestEmpHistDao = npTestEmpHistDao;
        this.employeesDao = employeesDao;
        this.npTestHistDao = npTestHistDao;
        this.npLvlInfoDao = npLvlInfoDao;
        this.npTypeDao = npTypeDao;
    }

    @GetMapping("/{npTestEmpHistId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> getById(Authentication authentication, @PathVariable Long npTestEmpHistId) {
        log.info("Nihongo history detail requested for id={}", npTestEmpHistId);
        NpTestEmpHist item = npTestEmpHistDao.findById(npTestEmpHistId).orElse(null);
        if (item == null) {
            return ResponseEntity.status(404).body(Map.of("message", "NP test employee history not found"));
        }
        if (!canAccess(authentication, item.getEmployeeNumber())) {
            return ResponseEntity.status(403).body(Map.of("message", "Access denied"));
        }
        return ResponseEntity.ok(toResponse(item, buildPolicyContext(), buildHistoryIndex()));
    }

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<NpTestEmpHistResponse>> getAll(Authentication authentication) {
        log.info("Nihongo history list requested");
        PolicyContext policyContext = buildPolicyContext();
        EmployeeHistoryIndex historyIndex = buildHistoryIndex();
        List<NpTestEmpHistResponse> data = npTestEmpHistDao.findAll().stream()
                .map(item -> toResponse(item, policyContext, historyIndex))
                .filter(item -> canAccess(authentication, item.getEmployeeNumber()))
                .toList();
        return ResponseEntity.ok(data);
    }

    @GetMapping("/by-employee/{employeeNumber}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<NpTestEmpHistResponse>> getByEmployeeNumber(Authentication authentication, @PathVariable String employeeNumber) {
        log.info("Nihongo history-by-employee requested for employeeNumber={}", employeeNumber);
        if (!canAccess(authentication, employeeNumber)) {
            return ResponseEntity.status(403).body(List.of());
        }
        PolicyContext policyContext = buildPolicyContext();
        EmployeeHistoryIndex historyIndex = buildHistoryIndex();
        List<NpTestEmpHistResponse> data = npTestEmpHistDao.findByEmployeeNumber(employeeNumber).stream()
                .map(item -> toResponse(item, policyContext, historyIndex))
                .filter(item -> canAccess(authentication, item.getEmployeeNumber()))
                .toList();
        return ResponseEntity.ok(data);
    }

    @GetMapping("/by-test/{npTestHistId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<NpTestEmpHistResponse>> getByTestId(Authentication authentication, @PathVariable Long npTestHistId) {
        log.info("Nihongo history-by-test requested for testId={}", npTestHistId);
        PolicyContext policyContext = buildPolicyContext();
        EmployeeHistoryIndex historyIndex = buildHistoryIndex();
        List<NpTestEmpHistResponse> data = npTestEmpHistDao.findByTestId(npTestHistId).stream()
                .map(item -> toResponse(item, policyContext, historyIndex))
                .filter(item -> canAccess(authentication, item.getEmployeeNumber()))
                .toList();
        return ResponseEntity.ok(data);
    }

    @GetMapping("/search")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<NpTestEmpHistResponse>> search(
            Authentication authentication,
            @RequestParam(required = false) String employeeNumber,
            @RequestParam(required = false, defaultValue = "false") Boolean passedOnly,
            @RequestParam(required = false, defaultValue = "false") Boolean mostRecentOnly) {

        log.info("Nihongo history search requested");
        if (employeeNumber != null && !employeeNumber.isBlank() && !canAccess(authentication, employeeNumber)) {
            return ResponseEntity.status(403).body(List.of());
        }
        PolicyContext policyContext = buildPolicyContext();
        EmployeeHistoryIndex historyIndex = buildHistoryIndex();
        List<NpTestEmpHistResponse> data = npTestEmpHistDao.search(employeeNumber, passedOnly, mostRecentOnly).stream()
                .map(item -> toResponse(item, policyContext, historyIndex))
                .filter(item -> canAccess(authentication, item.getEmployeeNumber()))
                .toList();
        return ResponseEntity.ok(data);
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN','HR','MANAGER')")
    public ResponseEntity<?> create(@Valid @RequestBody NpTestEmpHistRequest request) {
        log.info("Nihongo history create requested for employeeNumber={}", request.getEmployeeNumber());
        normalize(request);

        PolicyContext policyContext = buildPolicyContext();
        EmployeeHistoryIndex historyIndex = buildHistoryIndex();
        List<String> errors = validate(request, null, policyContext, historyIndex);
        if (!errors.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("errors", errors));
        }

        NpTestEmpHist entity = toEntity(request);
        LocalDateTime now = LocalDateTime.now();
        entity.setCreatedAt(now);
        entity.setUpdatedAt(now);

        NpTestEmpHist saved = npTestEmpHistDao.save(entity);
        return ResponseEntity.created(URI.create("/api/np-test-emp-hist/" + saved.getNpTestEmpHistId()))
                .body(toResponse(saved, policyContext, historyIndex));
    }

    @PutMapping("/{npTestEmpHistId}")
    @PreAuthorize("hasAnyRole('ADMIN','HR','MANAGER')")
    public ResponseEntity<?> update(@PathVariable Long npTestEmpHistId, @Valid @RequestBody NpTestEmpHistRequest request) {
        log.info("Nihongo history update requested for id={}", npTestEmpHistId);
        normalize(request);

        if (npTestEmpHistDao.findById(npTestEmpHistId).isEmpty()) {
            return ResponseEntity.status(404).body(Map.of("message", "NP test employee history not found"));
        }

        PolicyContext policyContext = buildPolicyContext();
        EmployeeHistoryIndex historyIndex = buildHistoryIndex();
        List<String> errors = validate(request, npTestEmpHistId, policyContext, historyIndex);
        if (!errors.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("errors", errors));
        }

        NpTestEmpHist entity = toEntity(request);
        entity.setUpdatedAt(LocalDateTime.now());

        return npTestEmpHistDao.update(npTestEmpHistId, entity)
                .<ResponseEntity<?>>map(item -> ResponseEntity.ok(toResponse(item, policyContext, historyIndex)))
                .orElseGet(() -> ResponseEntity.status(404).body(Map.of("message", "NP test employee history not found")));
    }

    @DeleteMapping("/{npTestEmpHistId}")
    @PreAuthorize("hasAnyRole('ADMIN','HR','MANAGER')")
    public ResponseEntity<?> delete(@PathVariable Long npTestEmpHistId) {
        log.info("Nihongo history delete requested for id={}", npTestEmpHistId);
        boolean deleted = npTestEmpHistDao.deleteById(npTestEmpHistId);
        if (!deleted) {
            return ResponseEntity.status(404).body(Map.of("message", "NP test employee history not found"));
        }
        return ResponseEntity.noContent().build();
    }

    private List<String> validate(
            NpTestEmpHistRequest request,
            Long currentRecordId,
            PolicyContext policyContext,
            EmployeeHistoryIndex historyIndex) {
        List<String> errors = new ArrayList<>();

        if (!employeesDao.existsByEmployeeCode(request.getEmployeeNumber())) {
            errors.add("Employee number does not exist");
        }

        NpTestHist requestedTest = npTestHistDao.findById(request.getNpTestHistId()).orElse(null);
        if (requestedTest == null) {
            errors.add("Test id does not exist");
            return errors;
        }

        NpLvlInfo requestedLevel = policyContext.levelsByCode.get(requestedTest.getNpLvlInfoCode());
        NpType requestedType = requestedLevel == null ? null : policyContext.typesByCode.get(requestedLevel.getNpTypeCode());
        Integer requestedRank = NihongoAllowanceUtil.extractPolicyGroupRank(
                requestedLevel == null ? null : requestedLevel.getNpLvlInfoCode(),
                requestedLevel == null ? null : requestedLevel.getNpLvlInfoName(),
                requestedType == null ? null : requestedType.getNpTypeCode());

        Integer highestPreferredRank = historyIndex.findHighestPassedPreferredRank(
                request.getEmployeeNumber(),
                currentRecordId,
                requestedTest.getTestDate());
        if (NihongoAllowanceUtil.shouldDisallowLowerRankExamAfterHigherPass(highestPreferredRank, requestedRank)) {
            errors.add("Downgrading to a lower Nihongo level is not allowed after passing a preferred level");
        }

        return errors;
    }

    private void normalize(NpTestEmpHistRequest request) {
        if (request.getEmployeeNumber() != null) {
            request.setEmployeeNumber(request.getEmployeeNumber().trim());
        }
    }

    private boolean canAccess(Authentication authentication, String employeeNumber) {
        if (authentication == null || employeeNumber == null || employeeNumber.isBlank()) {
            return false;
        }
        if (hasAnyRole(authentication, "ADMIN", "HR", "MANAGER")) {
            return true;
        }
        if (!hasAnyRole(authentication, "EMPLOYEE")) {
            return false;
        }
        String current = authentication.getName() == null ? "" : authentication.getName().trim().toLowerCase();
        String candidate = employeeNumber.trim().toLowerCase();
        if (current.equals(candidate)) {
            return true;
        }
        return current.replaceAll("\\D+", "").equals(candidate.replaceAll("\\D+", ""));
    }

    private boolean hasAnyRole(Authentication authentication, String... roles) {
        return java.util.Arrays.stream(roles)
                .anyMatch(role -> authentication.getAuthorities().stream()
                        .anyMatch(authority -> authority.getAuthority().equals("ROLE_" + role)));
    }

    private NpTestEmpHist toEntity(NpTestEmpHistRequest request) {
        NpTestEmpHist entity = new NpTestEmpHist();
        entity.setEmployeeNumber(request.getEmployeeNumber());
        entity.setNpTestHistId(request.getNpTestHistId());
        entity.setPassFlag(request.getPassFlag());
        entity.setTakeFlag(request.getTakeFlag());
        entity.setPoints(request.getPoints());
        return entity;
    }

    private NpTestEmpHistResponse toResponse(NpTestEmpHist entity, PolicyContext policyContext, EmployeeHistoryIndex historyIndex) {
        NpTestHist test = policyContext.testsById.get(entity.getNpTestHistId());
        NpLvlInfo level = test == null ? null : policyContext.levelsByCode.get(test.getNpLvlInfoCode());
        NpType type = level == null ? null : policyContext.typesByCode.get(level.getNpTypeCode());

        NpTestEmpHistResponse response = new NpTestEmpHistResponse();
        response.setNpTestEmpHistId(entity.getNpTestEmpHistId());
        response.setEmployeeNumber(entity.getEmployeeNumber());
        response.setNpTestHistId(entity.getNpTestHistId());
        response.setNpLvlInfoCode(level == null ? null : level.getNpLvlInfoCode());
        response.setNpLvlInfoName(level == null ? null : level.getNpLvlInfoName());
        response.setNpTypeCode(type == null ? null : type.getNpTypeCode());
        response.setNpTypeName(type == null ? null : type.getNpTypeName());
        Integer policyRank = NihongoAllowanceUtil.extractPolicyGroupRank(
                level == null ? null : level.getNpLvlInfoCode(),
                level == null ? null : level.getNpLvlInfoName(),
                type == null ? null : type.getNpTypeCode());
        response.setPolicyRank(policyRank);

        LocalDate allowanceStartDate = null;
        LocalDate allowanceEndDate = null;
        LocalDate effectiveAllowanceEndDate = null;
        boolean expired = false;
        boolean firstTimePass = false;

        if (Boolean.TRUE.equals(entity.getPassFlag()) && test != null) {
            Integer waitingMonths = NihongoAllowanceUtil.resolveAllowanceWaitingMonths(type == null ? null : type.getNpTypeCode());
            Integer validityMonths = NihongoAllowanceUtil.resolveValidityMonths(
                    type == null ? null : type.getNpTypeCode(),
                    level == null ? null : level.getNpLvlInfoCode(),
                    level == null ? null : level.getNpLvlInfoName());
            allowanceStartDate = NihongoAllowanceUtil.calculateAllowanceStartDate(test.getTestDate(), waitingMonths);
            allowanceEndDate = NihongoAllowanceUtil.calculateAllowanceEndDate(allowanceStartDate, validityMonths);
            effectiveAllowanceEndDate = applyRollingValidity(
                    entity.getEmployeeNumber(),
                    entity.getNpTestEmpHistId(),
                    policyRank,
                    allowanceStartDate,
                    allowanceEndDate,
                    historyIndex);
            expired = effectiveAllowanceEndDate != null && effectiveAllowanceEndDate.isBefore(LocalDate.now());
            firstTimePass = isPreferredType(type == null ? null : type.getNpTypeCode())
                    && historyIndex.isFirstPreferredPass(
                            entity.getEmployeeNumber(),
                            entity.getNpTestEmpHistId(),
                            test.getTestDate());
        }

        response.setAllowanceStartDate(allowanceStartDate);
        response.setAllowanceEndDate(allowanceEndDate);
        response.setEffectiveAllowanceEndDate(effectiveAllowanceEndDate);
        response.setExpired(expired);
        response.setFirstTimePass(firstTimePass);
        response.setPassFlag(entity.getPassFlag());
        response.setTakeFlag(entity.getTakeFlag());
        response.setPoints(entity.getPoints());
        response.setCreatedAt(entity.getCreatedAt());
        response.setUpdatedAt(entity.getUpdatedAt());
        return response;
    }

    private LocalDate applyRollingValidity(
            String employeeNumber,
            Long currentRecordId,
            Integer currentRank,
            LocalDate allowanceStartDate,
            LocalDate allowanceEndDate,
            EmployeeHistoryIndex historyIndex) {

        if (employeeNumber == null || currentRank == null || allowanceStartDate == null || allowanceEndDate == null) {
            return allowanceEndDate;
        }

        LocalDate rollingEndDate = allowanceEndDate;
        for (NpTestEmpHist history : historyIndex.historiesByEmployee.getOrDefault(employeeNumber, List.of())) {
            if (history.getNpTestEmpHistId() == null || history.getNpTestEmpHistId().equals(currentRecordId)) {
                continue;
            }
            if (!Boolean.TRUE.equals(history.getPassFlag())) {
                continue;
            }

            NpTestHist futureTest = historyIndex.testsById.get(history.getNpTestHistId());
            NpLvlInfo futureLevel = futureTest == null ? null : historyIndex.levelsByCode.get(futureTest.getNpLvlInfoCode());
            NpType futureType = futureLevel == null ? null : historyIndex.typesByCode.get(futureLevel.getNpTypeCode());
            Integer futureRank = NihongoAllowanceUtil.extractPolicyGroupRank(
                    futureLevel == null ? null : futureLevel.getNpLvlInfoCode(),
                    futureLevel == null ? null : futureLevel.getNpLvlInfoName(),
                    futureType == null ? null : futureType.getNpTypeCode());

            if (futureRank == null || futureRank > currentRank) {
                continue;
            }

            LocalDate futureStart = NihongoAllowanceUtil.calculateAllowanceStartDate(
                    futureTest.getTestDate(),
                    NihongoAllowanceUtil.resolveAllowanceWaitingMonths(futureType == null ? null : futureType.getNpTypeCode()));
            if (futureStart != null && futureStart.isAfter(allowanceStartDate)) {
                LocalDate effectiveEnd = futureStart.minusDays(1);
                if (effectiveEnd.isBefore(rollingEndDate)) {
                    rollingEndDate = effectiveEnd;
                }
            }
        }
        return rollingEndDate;
    }

    private boolean isPreferredType(String typeCode) {
        return typeCode != null && typeCode.trim().toUpperCase().contains("JLPT");
    }

    private PolicyContext buildPolicyContext() {
        Map<Long, NpTestHist> testsById = npTestHistDao.findAll().stream()
                .collect(Collectors.toMap(NpTestHist::getNpTestHistId, item -> item, (left, right) -> left));
        Map<String, NpLvlInfo> levelsByCode = npLvlInfoDao.findAll().stream()
                .collect(Collectors.toMap(NpLvlInfo::getNpLvlInfoCode, item -> item, (left, right) -> left));
        Map<String, NpType> typesByCode = npTypeDao.findAll().stream()
                .collect(Collectors.toMap(NpType::getNpTypeCode, item -> item, (left, right) -> left));
        return new PolicyContext(testsById, levelsByCode, typesByCode);
    }

    private EmployeeHistoryIndex buildHistoryIndex() {
        Map<String, List<NpTestEmpHist>> historiesByEmployee = npTestEmpHistDao.findAll().stream()
                .collect(Collectors.groupingBy(NpTestEmpHist::getEmployeeNumber, HashMap::new, Collectors.toList()));
        PolicyContext policyContext = buildPolicyContext();
        return new EmployeeHistoryIndex(historiesByEmployee, policyContext.testsById, policyContext.levelsByCode, policyContext.typesByCode);
    }

    private static class PolicyContext {
        protected final Map<Long, NpTestHist> testsById;
        protected final Map<String, NpLvlInfo> levelsByCode;
        protected final Map<String, NpType> typesByCode;

        private PolicyContext(Map<Long, NpTestHist> testsById, Map<String, NpLvlInfo> levelsByCode, Map<String, NpType> typesByCode) {
            this.testsById = testsById;
            this.levelsByCode = levelsByCode;
            this.typesByCode = typesByCode;
        }
    }

    private static final class EmployeeHistoryIndex extends PolicyContext {
        private final Map<String, List<NpTestEmpHist>> historiesByEmployee;

        private EmployeeHistoryIndex(
                Map<String, List<NpTestEmpHist>> historiesByEmployee,
                Map<Long, NpTestHist> testsById,
                Map<String, NpLvlInfo> levelsByCode,
                Map<String, NpType> typesByCode) {
            super(testsById, levelsByCode, typesByCode);
            this.historiesByEmployee = historiesByEmployee;
        }

        private boolean isFirstPreferredPass(String employeeNumber, Long currentRecordId, LocalDate currentDate) {
            if (currentDate == null) {
                return false;
            }
            List<NpTestEmpHist> employeeHistory = historiesByEmployee.getOrDefault(employeeNumber, List.of());
            Optional<NpTestEmpHist> currentItem = employeeHistory.stream()
                    .filter(item -> currentRecordId != null && currentRecordId.equals(item.getNpTestEmpHistId()))
                    .findFirst();
            if (currentItem.isEmpty()) {
                return false;
            }

            return employeeHistory.stream()
                    .filter(item -> !currentRecordId.equals(item.getNpTestEmpHistId()))
                    .filter(item -> Boolean.TRUE.equals(item.getPassFlag()))
                    .map(this::toPolicyEntry)
                    .filter(java.util.Objects::nonNull)
                    .filter(entry -> entry.preferredType)
                    .map(entry -> entry.testDate)
                    .filter(java.util.Objects::nonNull)
                    .noneMatch(date -> date.isBefore(currentDate) || date.isEqual(currentDate));
        }

        private Integer findHighestPassedPreferredRank(String employeeNumber, Long excludedRecordId, LocalDate asOfDate) {
            return historiesByEmployee.getOrDefault(employeeNumber, List.of()).stream()
                    .filter(item -> excludedRecordId == null || !excludedRecordId.equals(item.getNpTestEmpHistId()))
                    .filter(item -> Boolean.TRUE.equals(item.getPassFlag()))
                    .map(this::toPolicyEntry)
                    .filter(java.util.Objects::nonNull)
                    .filter(entry -> entry.preferredType)
                    .filter(entry -> asOfDate == null || entry.testDate == null
                            || entry.testDate.isBefore(asOfDate) || entry.testDate.isEqual(asOfDate))
                    .map(entry -> entry.rank)
                    .filter(java.util.Objects::nonNull)
                    .min(Comparator.naturalOrder())
                    .orElse(null);
        }

        private PolicyEntry toPolicyEntry(NpTestEmpHist history) {
            NpTestHist test = testsById.get(history.getNpTestHistId());
            NpLvlInfo level = test == null ? null : levelsByCode.get(test.getNpLvlInfoCode());
            NpType type = level == null ? null : typesByCode.get(level.getNpTypeCode());
            if (test == null || level == null || type == null) {
                return null;
            }

            Integer rank = NihongoAllowanceUtil.extractPolicyGroupRank(
                    level.getNpLvlInfoCode(),
                    level.getNpLvlInfoName(),
                    type.getNpTypeCode());
            boolean preferredType = type.getNpTypeCode() != null
                    && type.getNpTypeCode().trim().toUpperCase().contains("JLPT");
            return new PolicyEntry(rank, test.getTestDate(), preferredType);
        }

        private record PolicyEntry(Integer rank, LocalDate testDate, boolean preferredType) {
        }
    }
}
