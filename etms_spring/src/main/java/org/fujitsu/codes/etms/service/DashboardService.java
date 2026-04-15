package org.fujitsu.codes.etms.service;

import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.fujitsu.codes.etms.model.dao.AttendanceDao;
import org.fujitsu.codes.etms.model.dao.EmployeesDao;
import org.fujitsu.codes.etms.model.dao.LeaveDao;
import org.fujitsu.codes.etms.model.dao.NpLvlInfoDao;
import org.fujitsu.codes.etms.model.dao.NpTestEmpHistDao;
import org.fujitsu.codes.etms.model.dao.NpTestHistDao;
import org.fujitsu.codes.etms.model.dao.TrngInfoDao;
import org.fujitsu.codes.etms.model.dto.DashboardCardDto;
import org.fujitsu.codes.etms.model.dto.DashboardNotificationDto;
import org.fujitsu.codes.etms.model.dto.DashboardSummaryResponse;
import org.fujitsu.codes.etms.model.dto.DashboardTrendPointDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class DashboardService {

    private static final Logger LOGGER = LoggerFactory.getLogger(DashboardService.class);
    private static final DateTimeFormatter MONTH_LABEL_FORMAT = DateTimeFormatter.ofPattern("MMM yyyy");
    private static final DateTimeFormatter DAY_LABEL_FORMAT = DateTimeFormatter.ofPattern("MMM dd");

    private final EmployeesDao employeesDao;
    private final TrngInfoDao trngInfoDao;
    private final LeaveDao leaveDao;
    private final NpTestEmpHistDao npTestEmpHistDao;
    private final NpTestHistDao npTestHistDao;
    private final NpLvlInfoDao npLvlInfoDao;
    private final AttendanceDao attendanceDao;

    public DashboardService(
            EmployeesDao employeesDao,
            TrngInfoDao trngInfoDao,
            LeaveDao leaveDao,
            NpTestEmpHistDao npTestEmpHistDao,
            NpTestHistDao npTestHistDao,
            NpLvlInfoDao npLvlInfoDao,
            AttendanceDao attendanceDao) {
        this.employeesDao = employeesDao;
        this.trngInfoDao = trngInfoDao;
        this.leaveDao = leaveDao;
        this.npTestEmpHistDao = npTestEmpHistDao;
        this.npTestHistDao = npTestHistDao;
        this.npLvlInfoDao = npLvlInfoDao;
        this.attendanceDao = attendanceDao;
    }

    public DashboardSummaryResponse buildSummary() {
        LOGGER.info("Building ETMS dashboard summary");

        long totalEmployees = employeesDao.findAll().size();
        long activeTrainings = trngInfoDao.findAll().stream()
                .filter(training -> Boolean.TRUE.equals(training.getActive()))
                .count();
        long pendingLeaves = leaveDao.findAll().stream()
                .filter(leave -> leave.getStatus() != null && "PENDING".equalsIgnoreCase(leave.getStatus()))
                .count();
        long expiredCertifications = countExpiredNihongoCertifications();

        DashboardSummaryResponse response = new DashboardSummaryResponse();
        response.setCards(List.of(
                new DashboardCardDto("Total Employees", totalEmployees, "/employees"),
                new DashboardCardDto("Active Trainings", activeTrainings, "/training/info"),
                new DashboardCardDto("Pending Leaves", pendingLeaves, "/leaves"),
                new DashboardCardDto("Expired Nihongo Certifications", expiredCertifications, "/nihongo/results")
        ));
        response.setTrainingsPerMonth(buildTrainingTrend());
        response.setAttendanceTrends(buildAttendanceTrend());
        response.setNotifications(buildNotifications());
        return response;
    }

    private long countExpiredNihongoCertifications() {
        LocalDate today = LocalDate.now();
        var testsById = npTestHistDao.findAll().stream()
                .collect(java.util.stream.Collectors.toMap(item -> item.getNpTestHistId(), item -> item, (left, right) -> left));
        var levelsByCode = npLvlInfoDao.findAll().stream()
                .collect(java.util.stream.Collectors.toMap(item -> item.getNpLvlInfoCode(), item -> item, (left, right) -> left));

        return npTestEmpHistDao.search(null, true, true).stream()
                .filter(item -> {
                    var test = testsById.get(item.getNpTestHistId());
                    if (test == null) {
                        return false;
                    }
                    var level = levelsByCode.get(test.getNpLvlInfoCode());
                    return level != null && level.getValidTo() != null && level.getValidTo().isBefore(today);
                })
                .count();
    }

    private List<DashboardTrendPointDto> buildTrainingTrend() {
        LocalDate today = LocalDate.now();
        LinkedHashMap<YearMonth, Long> buckets = new LinkedHashMap<>();
        for (int offset = 5; offset >= 0; offset--) {
            YearMonth month = YearMonth.from(today.minusMonths(offset));
            buckets.put(month, 0L);
        }

        trngInfoDao.findAll().forEach(training -> {
            if (training.getStartDate() == null) {
                return;
            }
            YearMonth month = YearMonth.from(training.getStartDate());
            if (buckets.containsKey(month)) {
                buckets.put(month, buckets.get(month) + 1);
            }
        });

        return buckets.entrySet().stream()
                .map(entry -> new DashboardTrendPointDto(entry.getKey().format(MONTH_LABEL_FORMAT), entry.getValue()))
                .toList();
    }

    private List<DashboardTrendPointDto> buildAttendanceTrend() {
        LocalDate today = LocalDate.now();
        LinkedHashMap<LocalDate, Long> buckets = new LinkedHashMap<>();
        for (int offset = 6; offset >= 0; offset--) {
            LocalDate day = today.minusDays(offset);
            buckets.put(day, 0L);
        }

        attendanceDao.findAll().forEach(record -> {
            LocalDate day = record.getAttendanceDate();
            if (day != null && buckets.containsKey(day)) {
                buckets.put(day, buckets.get(day) + 1);
            }
        });

        return buckets.entrySet().stream()
                .map(entry -> new DashboardTrendPointDto(entry.getKey().format(DAY_LABEL_FORMAT), entry.getValue()))
                .toList();
    }

    private List<DashboardNotificationDto> buildNotifications() {
        LocalDate today = LocalDate.now();
        LocalDate soon = today.plusDays(30);
        List<DashboardNotificationDto> notifications = new ArrayList<>();

        long pendingLeaves = leaveDao.findAll().stream()
                .filter(leave -> leave.getStatus() != null && "PENDING".equalsIgnoreCase(leave.getStatus()))
                .count();
        if (pendingLeaves > 0) {
            notifications.add(new DashboardNotificationDto(
                    "LEAVE_PENDING",
                    "Pending leave approvals",
                    pendingLeaves + " leave request(s) require review.",
                    "/leaves",
                    "warning"
            ));
        }

        long upcomingTrainings = trngInfoDao.findAll().stream()
                .filter(training -> training.getStartDate() != null)
                .filter(training -> !training.getStartDate().isBefore(today) && !training.getStartDate().isAfter(soon))
                .count();
        if (upcomingTrainings > 0) {
            notifications.add(new DashboardNotificationDto(
                    "TRAINING_UPCOMING",
                    "Upcoming trainings",
                    upcomingTrainings + " training schedule(s) will start within 30 days.",
                    "/training/info",
                    "info"
            ));
        }

        var testsById = npTestHistDao.findAll().stream()
                .collect(java.util.stream.Collectors.toMap(item -> item.getNpTestHistId(), item -> item, (left, right) -> left));
        var levelsByCode = npLvlInfoDao.findAll().stream()
                .collect(java.util.stream.Collectors.toMap(item -> item.getNpLvlInfoCode(), item -> item, (left, right) -> left));

        long expiringSoon = npTestEmpHistDao.search(null, true, true).stream()
                .filter(item -> {
                    var test = testsById.get(item.getNpTestHistId());
                    if (test == null) {
                        return false;
                    }
                    var level = levelsByCode.get(test.getNpLvlInfoCode());
                    return level != null
                            && level.getValidTo() != null
                            && !level.getValidTo().isBefore(today)
                            && !level.getValidTo().isAfter(soon);
                })
                .count();
        if (expiringSoon > 0) {
            notifications.add(new DashboardNotificationDto(
                    "NIHONGO_EXPIRING",
                    "Expiring Nihongo certifications",
                    expiringSoon + " certification(s) will expire within 30 days.",
                    "/nihongo/results",
                    "danger"
            ));
        }

        return notifications;
    }
}
