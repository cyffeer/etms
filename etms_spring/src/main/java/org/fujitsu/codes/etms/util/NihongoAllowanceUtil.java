package org.fujitsu.codes.etms.util;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.fujitsu.codes.etms.model.data.NpTestEmpHist;

public final class NihongoAllowanceUtil {

    private static final Pattern JLPT_PATTERN = Pattern.compile("\\bN([1-5])\\b", Pattern.CASE_INSENSITIVE);
    private static final Pattern DIGIT_ONLY_PATTERN = Pattern.compile("\\bN([1-5])\\b", Pattern.CASE_INSENSITIVE);

    private NihongoAllowanceUtil() {
    }

    public static LocalDate calculateValidityEndDate(LocalDate passDate, Integer validityMonths) {
        if (passDate == null || validityMonths == null || validityMonths <= 0) {
            return null;
        }
        return passDate.plusMonths(validityMonths).minusDays(1);
    }

    public static LocalDate calculateAllowanceStartDate(LocalDate passDate, Integer waitingMonths) {
        if (passDate == null) {
            return null;
        }
        int months = (waitingMonths == null || waitingMonths < 0) ? 0 : waitingMonths;
        return YearMonth.from(passDate).plusMonths(months).atDay(1);
    }

    public static LocalDate calculateAllowanceEndDate(LocalDate allowanceStartDate, Integer validityMonths) {
        if (allowanceStartDate == null || validityMonths == null || validityMonths <= 0) {
            return null;
        }
        return allowanceStartDate.plusMonths(validityMonths).minusDays(1);
    }

    public static boolean shouldDisallowLowerRankExamAfterHigherPass(
            Integer highestPassedGroupRank,
            Integer requestedGroupRank) {

        if (highestPassedGroupRank == null || requestedGroupRank == null) {
            return false;
        }

        return requestedGroupRank > highestPassedGroupRank;
    }

    public static boolean isFirstTimePass(
            boolean currentAttemptPassed,
            List<NpTestEmpHist> employeeHistory) {

        if (!currentAttemptPassed) {
            return false;
        }

        if (employeeHistory == null || employeeHistory.isEmpty()) {
            return true;
        }

        return employeeHistory.stream().noneMatch(h -> Boolean.TRUE.equals(h.getPassFlag()));
    }

    public static Integer extractPolicyGroupRank(String levelCode, String levelText, String typeCode) {
        String normalizedCode = normalize(levelCode);
        String normalizedText = normalize(levelText);
        String normalizedType = normalize(typeCode);

        if (normalizedText.contains("N1") || normalizedCode.contains("N1")) {
            return 1;
        }
        if (normalizedText.contains("N2") || normalizedCode.contains("N2")) {
            return 2;
        }
        if (normalizedText.contains("N3") || normalizedCode.contains("N3")) {
            return 3;
        }
        if (normalizedText.contains("N4") || normalizedCode.contains("N4")) {
            return 4;
        }
        if (normalizedText.contains("N5") || normalizedCode.contains("N5")) {
            return 5;
        }

        if (normalizedType.contains("JTEST") || normalizedText.contains("JTEST") || normalizedCode.contains("JTEST")) {
            if (normalizedText.contains("SPECIALA") || normalizedText.contains("PREA") || normalizedText.equals("A")) {
                return 1;
            }
            if (normalizedText.contains("PREB") || normalizedText.contains("B")) {
                return 2;
            }
            if (normalizedText.contains("C")) {
                return 2;
            }
            if (normalizedText.contains("D")) {
                return 3;
            }
            if (normalizedText.contains("E")) {
                return 4;
            }
            if (normalizedText.contains("F")) {
                return 5;
            }
            if (normalizedText.contains("G")) {
                return 6;
            }
        }

        return null;
    }

    public static Integer extractJlptRank(String levelText) {
        if (levelText == null || levelText.isBlank()) {
            return null;
        }
        Matcher matcher = JLPT_PATTERN.matcher(levelText.trim());
        if (!matcher.find()) {
            return null;
        }
        return Integer.valueOf(matcher.group(1));
    }

    public static Integer resolveValidityMonths(String typeCode, String levelCode, String levelText) {
        Integer groupRank = extractPolicyGroupRank(levelCode, levelText, typeCode);
        if (groupRank == null) {
            return null;
        }

        String normalizedType = normalize(typeCode);
        if (normalizedType.contains("JTEST")) {
            return switch (groupRank) {
                case 1 -> 72;
                case 2 -> 48;
                case 3 -> 24;
                case 4 -> 12;
                case 5 -> 6;
                default -> null;
            };
        }

        if (normalizedType.contains("JLPT")) {
            return switch (groupRank) {
                case 1 -> 72;
                case 2 -> 48;
                case 3 -> 24;
                case 4 -> 12;
                case 5 -> 6;
                default -> null;
            };
        }

        return null;
    }

    public static Integer resolveAllowanceWaitingMonths(String typeCode) {
        String normalizedType = normalize(typeCode);
        if (normalizedType.contains("JLPT")) {
            return 3;
        }
        if (normalizedType.contains("JTEST")) {
            return 2;
        }
        return null;
    }

    private static String normalize(String value) {
        if (value == null) {
            return "";
        }
        return value.trim().toUpperCase().replaceAll("[^A-Z0-9]", "");
    }
}
