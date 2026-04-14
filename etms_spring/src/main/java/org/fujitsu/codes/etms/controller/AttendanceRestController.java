package org.fujitsu.codes.etms.controller;

import java.net.URI;
import java.time.LocalDate;
import java.time.Month;
import java.util.List;
import java.util.Map;

import org.fujitsu.codes.etms.model.dao.AttendanceDao;
import org.fujitsu.codes.etms.model.dao.EmployeesDao;
import org.fujitsu.codes.etms.model.data.AttendanceRecord;
import org.fujitsu.codes.etms.model.dto.AttendanceRequest;
import org.fujitsu.codes.etms.model.dto.AttendanceResponse;
import org.fujitsu.codes.etms.validator.AttendanceValidator;
import org.springframework.http.ResponseEntity;
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
@RequestMapping("/api/attendance")
public class AttendanceRestController {

    private final AttendanceDao attendanceDao;
    private final EmployeesDao employeesDao;

    public AttendanceRestController(AttendanceDao attendanceDao, EmployeesDao employeesDao) {
        this.attendanceDao = attendanceDao;
        this.employeesDao = employeesDao;
    }

    @GetMapping("/{attendanceRecordId}")
    public ResponseEntity<?> getById(@PathVariable Long attendanceRecordId) {
        return attendanceDao.findById(attendanceRecordId)
                .<ResponseEntity<?>>map(a -> ResponseEntity.ok(toResponse(a)))
                .orElseGet(() -> ResponseEntity.status(404).body(Map.of("message", "Attendance record not found")));
    }

    @GetMapping
    public ResponseEntity<List<AttendanceResponse>> getAll() {
        List<AttendanceResponse> data = attendanceDao.findAll().stream()
                .map(this::toResponse)
                .toList();
        return ResponseEntity.ok(data);
    }

    @GetMapping("/employee/{employeeNumber}")
    public ResponseEntity<List<AttendanceResponse>> getByEmployee(@PathVariable String employeeNumber) {
        List<AttendanceResponse> data = attendanceDao.findByEmployeeNumber(employeeNumber).stream()
                .map(this::toResponse)
                .toList();
        return ResponseEntity.ok(data);
    }

    @GetMapping("/date/{year}/{month}")
    public ResponseEntity<List<AttendanceResponse>> getByYearAndMonth(@PathVariable int year, @PathVariable int month) {
        LocalDate date = LocalDate.of(year, Month.of(month), 1);
        List<AttendanceResponse> data = attendanceDao.findByYearAndMonth(date).stream()
                .map(this::toResponse)
                .toList();
        return ResponseEntity.ok(data);
    }

    @GetMapping("/search")
    public ResponseEntity<?> search(
            @RequestParam(required = false) String employeeNumber,
            @RequestParam(required = false) Integer year,
            @RequestParam(required = false) Integer month) {

        if (month != null && (month < 1 || month > 12)) {
            return ResponseEntity.badRequest().body(Map.of("errors", List.of("month must be between 1 and 12")));
        }

        List<AttendanceResponse> data = attendanceDao.search(employeeNumber, year, month).stream()
                .map(this::toResponse)
                .toList();
        return ResponseEntity.ok(data);
    }

    @PostMapping
    public ResponseEntity<?> create(@Valid @RequestBody AttendanceRequest request) {
        List<String> errors = AttendanceValidator.validate(request);
        if (!errors.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("errors", errors));
        }

        if (!employeesDao.existsByEmployeeCode(request.getEmployeeNumber())) {
            return ResponseEntity.badRequest().body(Map.of("errors", List.of("Employee number does not exist")));
        }

        AttendanceRecord entity = toEntity(request);
        AttendanceRecord saved = attendanceDao.save(entity);
        return ResponseEntity.created(URI.create("/api/attendance/" + saved.getAttendanceRecordId()))
                .body(toResponse(saved));
    }

    @PutMapping("/{attendanceRecordId}")
    public ResponseEntity<?> update(
            @PathVariable Long attendanceRecordId,
            @Valid @RequestBody AttendanceRequest request) {

        if (attendanceDao.findById(attendanceRecordId).isEmpty()) {
            return ResponseEntity.status(404).body(Map.of("message", "Attendance record not found"));
        }

        List<String> errors = AttendanceValidator.validate(request);
        if (!errors.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("errors", errors));
        }

        AttendanceRecord entity = toEntity(request);
        return attendanceDao.update(attendanceRecordId, entity)
                .<ResponseEntity<?>>map(a -> ResponseEntity.ok(toResponse(a)))
                .orElseGet(() -> ResponseEntity.status(404).body(Map.of("message", "Attendance record not found")));
    }

    @DeleteMapping("/{attendanceRecordId}")
    public ResponseEntity<?> delete(@PathVariable Long attendanceRecordId) {
        boolean deleted = attendanceDao.deleteById(attendanceRecordId);
        if (!deleted) {
            return ResponseEntity.status(404).body(Map.of("message", "Attendance record not found"));
        }
        return ResponseEntity.noContent().build();
    }

    private AttendanceRecord toEntity(AttendanceRequest request) {
        AttendanceRecord attendance = new AttendanceRecord();
        attendance.setEmployeeNumber(request.getEmployeeNumber());
        attendance.setAttendanceDate(request.getAttendanceDate());
        attendance.setTimeIn(request.getTimeIn());
        attendance.setTimeOut(request.getTimeOut());
        attendance.setStatus(request.getStatus());
        return attendance;
    }

    private AttendanceResponse toResponse(AttendanceRecord attendance) {
        AttendanceResponse response = new AttendanceResponse();
        response.setAttendanceRecordId(attendance.getAttendanceRecordId());
        response.setEmployeeNumber(attendance.getEmployeeNumber());
        response.setAttendanceDate(attendance.getAttendanceDate());
        response.setTimeIn(attendance.getTimeIn());
        response.setTimeOut(attendance.getTimeOut());
        response.setStatus(attendance.getStatus());
        return response;
    }
}