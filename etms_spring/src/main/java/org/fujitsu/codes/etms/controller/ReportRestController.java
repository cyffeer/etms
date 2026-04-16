package org.fujitsu.codes.etms.controller;

import org.fujitsu.codes.etms.service.ReportExportService;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/reports")
public class ReportRestController {

    private final ReportExportService reportExportService;

    public ReportRestController(ReportExportService reportExportService) {
        this.reportExportService = reportExportService;
    }

    @GetMapping("/employees")
    public ResponseEntity<byte[]> exportEmployees(@RequestParam(defaultValue = "xlsx") String format) {
        return buildResponse(
                reportExportService.exportEmployees(format),
                format,
                "employees-report");
    }

    @GetMapping("/training-history")
    public ResponseEntity<byte[]> exportTrainingHistory(
            @RequestParam(defaultValue = "xlsx") String format,
            @RequestParam(required = false) String employeeNumber) {
        return buildResponse(
                reportExportService.exportTrainingHistory(format, employeeNumber),
                format,
                "training-history-report");
    }

    @GetMapping("/attendance")
    public ResponseEntity<byte[]> exportAttendance(
            @RequestParam(defaultValue = "xlsx") String format,
            @RequestParam(required = false) String employeeNumber,
            @RequestParam(required = false) Integer year,
            @RequestParam(required = false) Integer month) {
        return buildResponse(
                reportExportService.exportAttendance(format, employeeNumber, year, month),
                format,
                "attendance-report");
    }

    @GetMapping("/leaves")
    public ResponseEntity<byte[]> exportLeaves(
            @RequestParam(defaultValue = "xlsx") String format,
            @RequestParam(required = false) String employeeNumber,
            @RequestParam(required = false) String leaveType,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) java.time.LocalDate startDate,
            @RequestParam(required = false) java.time.LocalDate endDate) {
        return buildResponse(
                reportExportService.exportLeaves(format, employeeNumber, leaveType, status, startDate, endDate),
                format,
                "leave-report");
    }

    private ResponseEntity<byte[]> buildResponse(byte[] payload, String format, String filenamePrefix) {
        String normalized = format == null ? "" : format.trim().toLowerCase();
        MediaType mediaType = "pdf".equals(normalized)
                ? MediaType.APPLICATION_PDF
                : MediaType.parseMediaType(
                        "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        String extension = "pdf".equals(normalized) ? "pdf" : "xlsx";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(mediaType);
        headers.setContentDisposition(ContentDisposition.attachment()
                .filename(filenamePrefix + "." + extension)
                .build());

        return ResponseEntity.ok()
                .headers(headers)
                .body(payload);
    }
}
