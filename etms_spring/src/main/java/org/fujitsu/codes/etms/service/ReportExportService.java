package org.fujitsu.codes.etms.service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.font.Standard14Fonts;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.fujitsu.codes.etms.exception.InvalidInputException;
import org.fujitsu.codes.etms.model.dao.AttendanceDao;
import org.fujitsu.codes.etms.model.dao.EmployeesDao;
import org.fujitsu.codes.etms.model.dao.LeaveDao;
import org.fujitsu.codes.etms.model.dao.LeaveTypeDao;
import org.fujitsu.codes.etms.model.dao.TrngHistDao;
import org.fujitsu.codes.etms.model.dao.TrngInfoDao;
import org.fujitsu.codes.etms.model.data.AttendanceRecord;
import org.fujitsu.codes.etms.model.data.Employees;
import org.fujitsu.codes.etms.model.data.LeaveRecord;
import org.fujitsu.codes.etms.model.data.LeaveType;
import org.fujitsu.codes.etms.model.data.TrngHist;
import org.fujitsu.codes.etms.model.data.TrngInfo;
import org.springframework.stereotype.Service;

@Service
public class ReportExportService {

    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
    private static final PDType1Font PDF_TITLE_FONT = new PDType1Font(Standard14Fonts.FontName.HELVETICA_BOLD);
    private static final PDType1Font PDF_BODY_FONT = new PDType1Font(Standard14Fonts.FontName.HELVETICA);

    private final EmployeesDao employeesDao;
    private final TrngHistDao trngHistDao;
    private final TrngInfoDao trngInfoDao;
    private final AttendanceDao attendanceDao;
    private final LeaveDao leaveDao;
    private final LeaveTypeDao leaveTypeDao;

    public ReportExportService(EmployeesDao employeesDao, TrngHistDao trngHistDao, TrngInfoDao trngInfoDao,
            AttendanceDao attendanceDao, LeaveDao leaveDao, LeaveTypeDao leaveTypeDao) {
        this.employeesDao = employeesDao;
        this.trngHistDao = trngHistDao;
        this.trngInfoDao = trngInfoDao;
        this.attendanceDao = attendanceDao;
        this.leaveDao = leaveDao;
        this.leaveTypeDao = leaveTypeDao;
    }

    public byte[] exportEmployees(String format) {
        List<Employees> employees = employeesDao.findAll();
        return switch (normalizeFormat(format)) {
            case "xlsx" -> exportEmployeesWorkbook(employees);
            case "pdf" -> exportEmployeesPdf(employees);
            default -> throw unsupportedFormat();
        };
    }

    public byte[] exportTrainingHistory(String format, String employeeNumber) {
        Integer employeeId = employeesDao.resolveEmployeeIdentifier(employeeNumber);
        List<TrngHist> history = employeeNumber == null || employeeNumber.isBlank()
                ? trngHistDao.findAll(0, 10_000)
                : employeeId == null ? java.util.List.of() : trngHistDao.findByEmployeeNumber(String.valueOf(employeeId));

        Map<Integer, Employees> employeesByCode = indexEmployees();
        Map<Long, TrngInfo> trainingById = trngInfoDao.findAll().stream()
                .collect(Collectors.toMap(TrngInfo::getTrngInfoId, training -> training, (left, right) -> left));

        return switch (normalizeFormat(format)) {
            case "xlsx" -> exportTrainingHistoryWorkbook(history, employeesByCode, trainingById);
            case "pdf" -> exportTrainingHistoryPdf(history, employeesByCode, trainingById);
                default -> throw unsupportedFormat();
        };
    }

    public byte[] exportAttendance(String format, String employeeNumber, Integer year, Integer month) {
        Integer employeeId = employeesDao.resolveEmployeeIdentifier(employeeNumber);
        List<AttendanceRecord> rows = attendanceDao.search(employeeId, year, month);
        Map<Integer, Employees> employeesByCode = indexEmployees();

        return switch (normalizeFormat(format)) {
            case "xlsx" -> exportAttendanceWorkbook(rows, employeesByCode);
            case "pdf" -> exportAttendancePdf(rows, employeesByCode);
            default -> throw unsupportedFormat();
        };
    }

    public byte[] exportLeaves(String format, String employeeNumber, String leaveType, String status,
            java.time.LocalDate startDate, java.time.LocalDate endDate) {
        List<LeaveRecord> rows = leaveDao.search(employeeNumber, leaveType, status, startDate, endDate);
        Map<Integer, Employees> employeesByKey = indexEmployees();
        Map<String, LeaveType> leaveTypesByKey = indexLeaveTypes();

        return switch (normalizeFormat(format)) {
            case "xlsx" -> exportLeavesWorkbook(rows, employeesByKey, leaveTypesByKey);
            case "pdf" -> exportLeavesPdf(rows, employeesByKey, leaveTypesByKey);
            default -> throw unsupportedFormat();
        };
    }

    private byte[] exportEmployeesWorkbook(List<Employees> employees) {
        try (XSSFWorkbook workbook = new XSSFWorkbook(); ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            Sheet sheet = workbook.createSheet("Employees");
            writeRow(sheet.createRow(0), "Employee ID", "Employee Code", "First Name", "Last Name", "Email",
                    "Hire Date", "Status", "Created At", "Updated At");

            int rowIndex = 1;
            for (Employees employee : employees) {
                writeRow(sheet.createRow(rowIndex++),
                        stringify(employee.getEmployeeId()),
                        employee.getEmployeeCode(),
                        employee.getFirstName(),
                        employee.getLastName(),
                        employee.getEmail(),
                        stringify(employee.getHireDate()),
                        Boolean.TRUE.equals(employee.getActive()) ? "Active" : "Inactive",
                        formatDateTime(employee.getCreatedAt()),
                        formatDateTime(employee.getUpdatedAt()));
            }

            autosize(sheet, 9);
            workbook.write(outputStream);
            return outputStream.toByteArray();
        } catch (IOException ex) {
            throw new InvalidInputException("Failed to generate employee Excel report");
        }
    }

    private byte[] exportTrainingHistoryWorkbook(List<TrngHist> history, Map<Integer, Employees> employeesByCode,
            Map<Long, TrngInfo> trainingById) {
        try (XSSFWorkbook workbook = new XSSFWorkbook(); ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            Sheet sheet = workbook.createSheet("Training History");
            writeRow(sheet.createRow(0), "Employee Number", "Employee Name", "Training ID", "Training Code",
                    "Training Name", "Assigned At", "Updated At");

            int rowIndex = 1;
            for (TrngHist item : history) {
                Employees employee = employeesByCode.get(employeesDao.resolveEmployeeIdentifier(item.getEmployeeNumber()));
                TrngInfo training = trainingById.get(item.getTrngHistId());

                writeRow(sheet.createRow(rowIndex++),
                        stringify(employeesDao.resolveEmployeeIdentifier(item.getEmployeeNumber())),
                        employee == null ? "" : employee.getFirstName() + " " + employee.getLastName(),
                        stringify(item.getTrngHistId()),
                        training == null ? "" : training.getTrngCode(),
                        training == null ? "" : training.getTrngName(),
                        formatDateTime(item.getCreatedAt()),
                        formatDateTime(item.getUpdatedAt()));
            }

            autosize(sheet, 7);
            workbook.write(outputStream);
            return outputStream.toByteArray();
        } catch (IOException ex) {
            throw new InvalidInputException("Failed to generate training history Excel report");
        }
    }

    private byte[] exportEmployeesPdf(List<Employees> employees) {
        List<String> lines = employees.stream()
                .map(employee -> String.join(" | ",
                        stringify(employee.getEmployeeId()),
                        safe(employee.getEmployeeCode()),
                        safe(employee.getFirstName() + " " + employee.getLastName()),
                        safe(employee.getEmail()),
                        stringify(employee.getHireDate()),
                        Boolean.TRUE.equals(employee.getActive()) ? "Active" : "Inactive"))
                .toList();
        return exportPdf("Employee List Report", lines);
    }

    private byte[] exportTrainingHistoryPdf(List<TrngHist> history, Map<Integer, Employees> employeesByCode,
            Map<Long, TrngInfo> trainingById) {
        List<String> lines = history.stream()
                .map(item -> {
                    Employees employee = employeesByCode.get(employeesDao.resolveEmployeeIdentifier(item.getEmployeeNumber()));
                    TrngInfo training = trainingById.get(item.getTrngHistId());
                    String employeeName = employee == null ? "" : employee.getFirstName() + " " + employee.getLastName();
                    return String.join(" | ",
                            safe(stringify(employeesDao.resolveEmployeeIdentifier(item.getEmployeeNumber()))),
                            safe(employeeName),
                            stringify(item.getTrngHistId()),
                            training == null ? "" : safe(training.getTrngCode()),
                            training == null ? "" : safe(training.getTrngName()),
                            formatDateTime(item.getCreatedAt()));
                })
                .toList();
        return exportPdf("Training History Report", lines);
    }

    private byte[] exportAttendanceWorkbook(List<AttendanceRecord> attendanceRows, Map<Integer, Employees> employeesByCode) {
        try (XSSFWorkbook workbook = new XSSFWorkbook(); ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            Sheet sheet = workbook.createSheet("Attendance");
            writeRow(sheet.createRow(0), "Attendance ID", "Employee Number", "Employee Name", "Date",
                    "Time In", "Time Out", "Status");

            int rowIndex = 1;
            for (AttendanceRecord item : attendanceRows) {
                Employees employee = employeesByCode.get(item.getEmployeeNumber());
                writeRow(sheet.createRow(rowIndex++),
                        stringify(item.getAttendanceRecordId()),
                        stringify(item.getEmployeeNumber()),
                        employee == null ? "" : employee.getFirstName() + " " + employee.getLastName(),
                        stringify(item.getAttendanceDate()),
                        stringify(item.getTimeIn()),
                        stringify(item.getTimeOut()),
                        item.getStatus());
            }

            autosize(sheet, 6);
            workbook.write(outputStream);
            return outputStream.toByteArray();
        } catch (IOException ex) {
            throw new InvalidInputException("Failed to generate attendance Excel report");
        }
    }

    private byte[] exportAttendancePdf(List<AttendanceRecord> attendanceRows, Map<Integer, Employees> employeesByCode) {
        List<String> lines = attendanceRows.stream()
                .map(item -> {
                    Employees employee = employeesByCode.get(item.getEmployeeNumber());
                    String employeeName = employee == null ? "" : employee.getFirstName() + " " + employee.getLastName();
                    return String.join(" | ",
                            stringify(item.getAttendanceRecordId()),
                            safe(stringify(item.getEmployeeNumber())),
                            safe(employeeName),
                            stringify(item.getAttendanceDate()),
                            safe(stringify(item.getTimeIn())),
                            safe(stringify(item.getTimeOut())),
                            safe(item.getStatus()));
                })
                .toList();
        return exportPdf("Attendance Report", lines);
    }

    private byte[] exportLeavesWorkbook(List<LeaveRecord> rows, Map<Integer, Employees> employeesByKey,
            Map<String, LeaveType> leaveTypesByKey) {
        try (XSSFWorkbook workbook = new XSSFWorkbook(); ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            Sheet sheet = workbook.createSheet("Leaves");
            writeRow(sheet.createRow(0), "Leave Record ID", "Employee Number", "Employee Name", "Leave Type",
                    "Leave Type Name", "Start Date", "End Date", "Status", "Remarks");

            int rowIndex = 1;
            for (LeaveRecord item : rows) {
                Employees employee = employeesByKey.get(employeesDao.resolveEmployeeIdentifier(item.getEmployeeNumber()));
                LeaveType leaveType = leaveTypesByKey.get(item.getLeaveType());
                writeRow(sheet.createRow(rowIndex++),
                        stringify(item.getLeaveRecordId()),
                        item.getEmployeeNumber(),
                        employee == null ? "" : employee.getFirstName() + " " + employee.getLastName(),
                        item.getLeaveType(),
                        leaveType == null ? "" : leaveType.getLeaveTypeName(),
                        stringify(item.getStartDate()),
                        stringify(item.getEndDate()),
                        item.getStatus(),
                        item.getRemarks());
            }

            autosize(sheet, 8);
            workbook.write(outputStream);
            return outputStream.toByteArray();
        } catch (IOException ex) {
            throw new InvalidInputException("Failed to generate leave Excel report");
        }
    }

    private byte[] exportLeavesPdf(List<LeaveRecord> rows, Map<Integer, Employees> employeesByKey,
            Map<String, LeaveType> leaveTypesByKey) {
        List<String> lines = rows.stream()
                .map(item -> {
                    Employees employee = employeesByKey.get(employeesDao.resolveEmployeeIdentifier(item.getEmployeeNumber()));
                    LeaveType leaveType = leaveTypesByKey.get(item.getLeaveType());
                    String employeeName = employee == null ? "" : employee.getFirstName() + " " + employee.getLastName();
                    return String.join(" | ",
                            stringify(item.getLeaveRecordId()),
                            safe(item.getEmployeeNumber()),
                            safe(employeeName),
                            safe(item.getLeaveType()),
                            leaveType == null ? "" : safe(leaveType.getLeaveTypeName()),
                            stringify(item.getStartDate()),
                            stringify(item.getEndDate()),
                            safe(item.getStatus()),
                            safe(item.getRemarks()));
                })
                .toList();
        return exportPdf("Leave Report", lines);
    }

    private byte[] exportPdf(String title, List<String> lines) {
        try (PDDocument document = new PDDocument(); ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            PDPage page = new PDPage(PDRectangle.LETTER);
            document.addPage(page);

            try (PDPageContentStream contentStream = new PDPageContentStream(document, page)) {
                contentStream.setFont(PDF_TITLE_FONT, 14);
                contentStream.beginText();
                contentStream.newLineAtOffset(50, 730);
                contentStream.showText(title);
                contentStream.endText();

                contentStream.setFont(PDF_BODY_FONT, 9);
                float y = 705;
                for (String line : lines) {
                    if (y < 50) {
                        break;
                    }
                    contentStream.beginText();
                    contentStream.newLineAtOffset(50, y);
                    contentStream.showText(trimForPdf(line));
                    contentStream.endText();
                    y -= 14;
                }
            }

            document.save(outputStream);
            return outputStream.toByteArray();
        } catch (IOException ex) {
            throw new InvalidInputException("Failed to generate PDF report");
        }
    }

    private String normalizeFormat(String format) {
        return format == null ? "" : format.trim().toLowerCase();
    }

    private InvalidInputException unsupportedFormat() {
        return new InvalidInputException("Report format must be either xlsx or pdf");
    }

    private void writeRow(Row row, String... values) {
        for (int index = 0; index < values.length; index++) {
            row.createCell(index).setCellValue(values[index] == null ? "" : values[index]);
        }
    }

    private void autosize(Sheet sheet, int lastColumnIndex) {
        for (int index = 0; index <= lastColumnIndex; index++) {
            sheet.autoSizeColumn(index);
        }
    }

    private String formatDateTime(java.time.LocalDateTime value) {
        return value == null ? "" : DATE_TIME_FORMATTER.format(value);
    }

    private String stringify(Object value) {
        return value == null ? "" : String.valueOf(value);
    }

    private String safe(String value) {
        return value == null ? "" : value;
    }

    private Map<Integer, Employees> indexEmployees() {
        Map<Integer, Employees> employeesByKey = new java.util.HashMap<>();
        for (Employees employee : employeesDao.findAll()) {
            if (employee.getEmployeeId() != null) {
                employeesByKey.put(employee.getEmployeeId().intValue(), employee);
            }
        }
        return employeesByKey;
    }

    private Map<String, LeaveType> indexLeaveTypes() {
        Map<String, LeaveType> leaveTypesByKey = new java.util.HashMap<>();
        for (LeaveType leaveType : leaveTypeDao.findAll()) {
            if (leaveType.getLeaveTypeCode() != null && !leaveType.getLeaveTypeCode().isBlank()) {
                leaveTypesByKey.put(leaveType.getLeaveTypeCode(), leaveType);
            }
            if (leaveType.getLeaveTypeName() != null && !leaveType.getLeaveTypeName().isBlank()) {
                leaveTypesByKey.put(leaveType.getLeaveTypeName(), leaveType);
            }
        }
        return leaveTypesByKey;
    }

    private String trimForPdf(String value) {
        String sanitized = safe(value).replace('\n', ' ').replace('\r', ' ');
        return sanitized.length() > 110 ? sanitized.substring(0, 107) + "..." : sanitized;
    }
}
