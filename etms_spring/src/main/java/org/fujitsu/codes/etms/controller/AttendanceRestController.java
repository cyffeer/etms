package org.fujitsu.codes.etms.controller;

import java.net.URI;
import java.time.LocalDate;
import java.time.Month;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.fujitsu.codes.etms.model.dao.AttendanceDao;
import org.fujitsu.codes.etms.model.dao.DepartmentDao;
import org.fujitsu.codes.etms.model.dao.DeptMembersDao;
import org.fujitsu.codes.etms.model.dao.EmployeesDao;
import org.fujitsu.codes.etms.model.dao.MemberTypeDao;
import org.fujitsu.codes.etms.model.data.AttendanceRecord;
import org.fujitsu.codes.etms.model.data.Department;
import org.fujitsu.codes.etms.model.data.DeptMembers;
import org.fujitsu.codes.etms.model.dto.AttendanceRequest;
import org.fujitsu.codes.etms.model.dto.AttendanceResponse;
import org.fujitsu.codes.etms.model.data.Employees;
import org.fujitsu.codes.etms.model.data.MemberType;
import org.fujitsu.codes.etms.validator.AttendanceValidator;
import org.fujitsu.codes.etms.model.dto.ApiResponse;
import org.springframework.http.ResponseEntity;
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
import org.springframework.security.access.prepost.PreAuthorize;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/attendance")
public class AttendanceRestController {

    private final AttendanceDao attendanceDao;
    private final EmployeesDao employeesDao;
    private final DeptMembersDao deptMembersDao;
    private final DepartmentDao departmentDao;
    private final MemberTypeDao memberTypeDao;
    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(AttendanceRestController.class);

    public AttendanceRestController(
            AttendanceDao attendanceDao,
            EmployeesDao employeesDao,
            DeptMembersDao deptMembersDao,
            DepartmentDao departmentDao,
            MemberTypeDao memberTypeDao) {
        this.attendanceDao = attendanceDao;
        this.employeesDao = employeesDao;
        this.deptMembersDao = deptMembersDao;
        this.departmentDao = departmentDao;
        this.memberTypeDao = memberTypeDao;
    }

    @GetMapping("/{attendanceRecordId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> getById(Authentication authentication, @PathVariable Long attendanceRecordId) {
        log.info("Attendance detail requested for id={}", attendanceRecordId);
        Optional<AttendanceRecord> attendance = attendanceDao.findById(attendanceRecordId);
        if (attendance.isEmpty()) {
            return ResponseEntity.status(404).body(Map.of("message", "Attendance record not found"));
        }
        AttendanceRecord record = attendance.get();
        if (!canAccess(authentication, record.getEmployeeNumber() == null ? null : String.valueOf(record.getEmployeeNumber()))) {
            return ResponseEntity.status(403).body(Map.of("message", "Access denied"));
        }
        return ResponseEntity.ok(toResponse(record, buildContext()));
    }

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<?>> getAll(
            Authentication authentication,
            @RequestParam(name = "page", required = false) Integer page,
            @RequestParam(name = "size", required = false) Integer size) {
        log.info("Attendance list requested page={} size={}", page, size);
        AttendanceContext context = buildContext();
        List<AttendanceResponse> allItems = attendanceDao.findAll().stream()
                .map(item -> toResponse(item, context))
                .filter(item -> canAccess(authentication, item.getEmployeeNumber()))
                .toList();

        if (page == null && size == null) {
            return ResponseEntity.ok(ApiResponse.success("Attendance fetched successfully", allItems));
        }
        if (page == null || size == null || page < 0 || size <= 0) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("Invalid pagination values", List.of("page must be >= 0 and size must be > 0")));
        }

        int fromIndex = Math.min(page * size, allItems.size());
        int toIndex = Math.min(fromIndex + size, allItems.size());
        List<AttendanceResponse> data = allItems.subList(fromIndex, toIndex);
        long totalElements = allItems.size();
        long totalPages = (long) Math.ceil((double) totalElements / size);

        return ResponseEntity.ok(ApiResponse.success("Attendance fetched successfully",
                Map.of(
                        "data", data,
                        "page", page,
                        "size", size,
                        "totalElements", totalElements,
                        "totalPages", totalPages
                )));
    }

    @GetMapping("/employee/{employeeNumber}")
    @PreAuthorize("@etmsAuthorizationService.canAccessEmployeeInput(authentication, #employeeNumber)")
    public ResponseEntity<List<AttendanceResponse>> getByEmployee(Authentication authentication, @PathVariable String employeeNumber) {
        log.info("Attendance-by-employee requested for employeeNumber={}", employeeNumber);
        AttendanceContext context = buildContext();
        Integer employeeId = employeesDao.resolveEmployeeIdentifier(employeeNumber);
        if (employeeId == null) {
            return ResponseEntity.ok(List.of());
        }
        if (!canAccess(authentication, String.valueOf(employeeId))) {
            return ResponseEntity.status(403).<List<AttendanceResponse>>body(List.of());
        }
        List<AttendanceResponse> data = attendanceDao.findByEmployeeNumber(employeeId).stream()
                .map(item -> toResponse(item, context))
                .toList();
        return ResponseEntity.ok(data);
    }

    @GetMapping("/date/{year}/{month}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<AttendanceResponse>> getByYearAndMonth(Authentication authentication, @PathVariable int year, @PathVariable int month) {
        LocalDate date = LocalDate.of(year, Month.of(month), 1);
        AttendanceContext context = buildContext();
        List<AttendanceResponse> data = attendanceDao.findByYearAndMonth(date).stream()
                .map(item -> toResponse(item, context))
                .filter(item -> canAccess(authentication, item.getEmployeeNumber()))
                .toList();
        return ResponseEntity.ok(data);
    }

    @GetMapping("/search")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> search(
            Authentication authentication,
            @RequestParam(required = false) String employeeNumber,
            @RequestParam(required = false) String employeeName,
            @RequestParam(required = false) String designation,
            @RequestParam(required = false) String officeName,
            @RequestParam(required = false) Integer year,
            @RequestParam(required = false) Integer month,
            @RequestParam(name = "page", required = false) Integer page,
            @RequestParam(name = "size", required = false) Integer size) {

        if (employeeNumber != null && !employeeNumber.isBlank() && !canAccess(authentication, employeeNumber)) {
            return ResponseEntity.status(403).body(Map.of("message", "Access denied"));
        }

        if (month != null && (month < 1 || month > 12)) {
            return ResponseEntity.badRequest().body(Map.of("errors", List.of("month must be between 1 and 12")));
        }

        AttendanceContext context = buildContext();
        Integer employeeId = employeesDao.resolveEmployeeIdentifier(employeeNumber);
        List<AttendanceResponse> allItems = attendanceDao.search(employeeId, year, month).stream()
                .map(item -> toResponse(item, context))
                .filter(item -> canAccess(authentication, item.getEmployeeNumber()))
                .filter(item -> matches(item, employeeName, designation, officeName))
                .toList();

        if (page == null && size == null) {
            return ResponseEntity.ok(ApiResponse.success("Attendance fetched successfully", allItems));
        }
        if (page == null || size == null || page < 0 || size <= 0) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("Invalid pagination values", List.of("page must be >= 0 and size must be > 0")));
        }

        int fromIndex = Math.min(page * size, allItems.size());
        int toIndex = Math.min(fromIndex + size, allItems.size());
        List<AttendanceResponse> data = allItems.subList(fromIndex, toIndex);
        long totalElements = allItems.size();
        long totalPages = (long) Math.ceil((double) totalElements / size);

        return ResponseEntity.ok(ApiResponse.success("Attendance fetched successfully",
                Map.of(
                        "data", data,
                        "page", page,
                        "size", size,
                        "totalElements", totalElements,
                        "totalPages", totalPages
                )));
    }

    @PostMapping
    @PreAuthorize("@etmsAuthorizationService.canAccessEmployeeInput(authentication, #request.employeeNumber)")
    public ResponseEntity<?> create(Authentication authentication, @Valid @RequestBody AttendanceRequest request) {
        log.info("Attendance create requested for employee={}", request.getEmployeeNumber());
        if (!canAccess(authentication, request.getEmployeeNumber())) {
            return ResponseEntity.status(403).body(Map.of("message", "Access denied"));
        }
        List<String> errors = AttendanceValidator.validate(request);
        Integer employeeId = employeesDao.resolveEmployeeIdentifier(request.getEmployeeNumber());
        if (employeeId == null) {
            errors.add("Employee number does not exist");
        }
        if (!errors.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("errors", errors));
        }

        AttendanceRecord entity = toEntity(request);
        AttendanceRecord saved = attendanceDao.save(entity);
        return ResponseEntity.created(URI.create("/api/attendance/" + saved.getAttendanceRecordId()))
                .body(toResponse(saved));
    }

    @PutMapping("/{attendanceRecordId}")
    @PreAuthorize("hasAnyRole('ADMIN','HR','MANAGER')")
    public ResponseEntity<?> update(
            Authentication authentication,
            @PathVariable Long attendanceRecordId,
            @Valid @RequestBody AttendanceRequest request) {
        log.info("Attendance update requested for id={}", attendanceRecordId);
        if (!canAccess(authentication, request.getEmployeeNumber())) {
            return ResponseEntity.status(403).body(Map.of("message", "Access denied"));
        }

        if (attendanceDao.findById(attendanceRecordId).isEmpty()) {
            return ResponseEntity.status(404).body(Map.of("message", "Attendance record not found"));
        }

        List<String> errors = AttendanceValidator.validate(request);
        Integer employeeId = employeesDao.resolveEmployeeIdentifier(request.getEmployeeNumber());
        if (employeeId == null) {
            errors.add("Employee number does not exist");
        }
        if (!errors.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("errors", errors));
        }

        AttendanceRecord entity = toEntity(request);
        return attendanceDao.update(attendanceRecordId, entity)
                .<ResponseEntity<?>>map(a -> ResponseEntity.ok(toResponse(a)))
                .orElseGet(() -> ResponseEntity.status(404).body(Map.of("message", "Attendance record not found")));
    }

    @DeleteMapping("/{attendanceRecordId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> delete(@PathVariable Long attendanceRecordId) {
        log.info("Attendance delete requested for id={}", attendanceRecordId);
        boolean deleted = attendanceDao.deleteById(attendanceRecordId);
        if (!deleted) {
            return ResponseEntity.status(404).body(Map.of("message", "Attendance record not found"));
        }
        return ResponseEntity.noContent().build();
    }

    private AttendanceRecord toEntity(AttendanceRequest request) {
        AttendanceRecord attendance = new AttendanceRecord();
        attendance.setEmployeeNumber(employeesDao.resolveEmployeeIdentifier(request.getEmployeeNumber()));
        attendance.setAttendanceDate(request.getAttendanceDate());
        attendance.setTimeIn(request.getTimeIn());
        attendance.setTimeOut(request.getTimeOut());
        attendance.setStatus(request.getStatus());
        return attendance;
    }

    private AttendanceResponse toResponse(AttendanceRecord attendance) {
        return toResponse(attendance, buildContext());
    }

    private AttendanceResponse toResponse(AttendanceRecord attendance, AttendanceContext context) {
        AttendanceResponse response = new AttendanceResponse();
        response.setAttendanceRecordId(attendance.getAttendanceRecordId());
        response.setEmployeeNumber(attendance.getEmployeeNumber() == null ? null : String.valueOf(attendance.getEmployeeNumber()));
        response.setAttendanceDate(attendance.getAttendanceDate());
        response.setTimeIn(attendance.getTimeIn());
        response.setTimeOut(attendance.getTimeOut());
        response.setStatus(attendance.getStatus());

        EmployeeSummary summary = context.employeeSummaries.get(attendance.getEmployeeNumber());
        if (summary != null) {
            response.setEmployeeName(summary.employeeName);
            response.setDesignation(summary.designation);
            response.setOfficeName(summary.officeName);
        }
        return response;
    }

    private boolean matches(AttendanceResponse item, String employeeName, String designation, String officeName) {
        if (employeeName != null && !employeeName.isBlank()) {
            String candidate = item.getEmployeeName() == null ? "" : item.getEmployeeName().toLowerCase();
            if (!candidate.contains(employeeName.trim().toLowerCase())) {
                return false;
            }
        }
        if (designation != null && !designation.isBlank()) {
            String candidate = item.getDesignation() == null ? "" : item.getDesignation().toLowerCase();
            if (!candidate.contains(designation.trim().toLowerCase())) {
                return false;
            }
        }
        if (officeName != null && !officeName.isBlank()) {
            String candidate = item.getOfficeName() == null ? "" : item.getOfficeName().toLowerCase();
            if (!candidate.contains(officeName.trim().toLowerCase())) {
                return false;
            }
        }
        return true;
    }

    private AttendanceContext buildContext() {
        Map<Integer, Employees> employeesByKey = new LinkedHashMap<>();
        employeesDao.findAll().forEach(employee -> {
            if (employee.getEmployeeId() != null) {
                employeesByKey.put(employee.getEmployeeId().intValue(), employee);
            }
            if (employee.getEmployeeCode() != null && !employee.getEmployeeCode().isBlank()) {
                try {
                    employeesByKey.putIfAbsent(Integer.parseInt(employee.getEmployeeCode().replaceAll("\\D+", "")), employee);
                } catch (NumberFormatException ex) {
                    // ignore non-numeric codes
                }
            }
        });
        Map<String, Department> departmentsByCode = departmentDao.findAll().stream()
                .collect(Collectors.toMap(Department::getDepartmentCode, Function.identity(), (left, right) -> left, LinkedHashMap::new));
        Map<Long, MemberType> memberTypesById = memberTypeDao.findAll().stream()
                .collect(Collectors.toMap(MemberType::getMemberTypeId, Function.identity(), (left, right) -> left, LinkedHashMap::new));
        Map<Integer, DeptMembers> membershipByEmployee = deptMembersDao.findAll().stream()
                .filter(item -> item.getEmployeeNumber() != null)
                .collect(Collectors.toMap(
                        DeptMembers::getEmployeeNumber,
                        Function.identity(),
                        (left, right) -> {
                            if (left.getMemberStart() == null) {
                                return right;
                            }
                            if (right.getMemberStart() == null) {
                                return left;
                            }
                            return right.getMemberStart().isAfter(left.getMemberStart()) ? right : left;
                        },
                        LinkedHashMap::new));

        Map<Integer, EmployeeSummary> summaries = new LinkedHashMap<>();
        employeesByKey.forEach((employeeNumber, employee) -> {
            String fullName = ((employee.getFirstName() == null ? "" : employee.getFirstName()) + " " +
                    (employee.getLastName() == null ? "" : employee.getLastName())).trim();
            DeptMembers membership = membershipByEmployee.get(employeeNumber);
            String designation = null;
            String officeName = null;
            if (membership != null) {
                if (membership.getMemberTypeId() != null) {
                    MemberType memberType = memberTypesById.get(membership.getMemberTypeId());
                    if (memberType != null) {
                        designation = memberType.getMemberTypeName();
                    }
                }
                if (membership.getDepartmentCode() != null) {
                    Department department = departmentsByCode.get(membership.getDepartmentCode());
                    if (department != null) {
                        officeName = department.getDepartmentName();
                    }
                }
            }
            summaries.put(employeeNumber, new EmployeeSummary(fullName, designation, officeName));
        });
        return new AttendanceContext(summaries);
    }

    private static final class AttendanceContext {
        private final Map<Integer, EmployeeSummary> employeeSummaries;

        private AttendanceContext(Map<Integer, EmployeeSummary> employeeSummaries) {
            this.employeeSummaries = employeeSummaries;
        }
    }

    private static final class EmployeeSummary {
        private final String employeeName;
        private final String designation;
        private final String officeName;

        private EmployeeSummary(String employeeName, String designation, String officeName) {
            this.employeeName = employeeName;
            this.designation = designation;
            this.officeName = officeName;
        }
    }

    private boolean canAccess(Authentication authentication, String employeeNumber) {
        if (authentication == null || employeeNumber == null) {
            return false;
        }
        if (hasAnyRole(authentication, "ADMIN", "HR", "MANAGER")) {
            return true;
        }
        if (authentication.getName() == null) {
            return false;
        }
        String current = authentication.getName().trim().toLowerCase();
        String candidate = employeeNumber.trim().toLowerCase();
        if (current.equals(candidate)) {
            return true;
        }
        return current.replaceAll("\\D+", "").equals(candidate.replaceAll("\\D+", ""));
    }

    private boolean hasAnyRole(Authentication authentication, String... roles) {
        if (authentication == null) {
            return false;
        }
        return java.util.Arrays.stream(roles)
                .anyMatch(role -> authentication.getAuthorities().stream()
                        .anyMatch(authority -> authority.getAuthority().equals("ROLE_" + role)));
    }
}
