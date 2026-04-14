package org.fujitsu.codes.etms.util;

import java.time.LocalDate;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.fujitsu.codes.etms.model.data.NpTestEmpHist;

public final class NihongoAllowanceUtil {

    private static final Pattern JLPT_PATTERN = Pattern.compile("\\bN([1-5])\\b", Pattern.CASE_INSENSITIVE);

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
        return passDate.plusMonths(months);
    }

    public static boolean shouldDisallowLowerRankExamAfterHigherPass(
            String highestPassedPreferredLevel,
            String requestedExamLevel) {

        Integer passedRank = extractJlptRank(highestPassedPreferredLevel);
        Integer requestedRank = extractJlptRank(requestedExamLevel);

        if (passedRank == null || requestedRank == null) {
            return false;
        }

        // JLPT: N1 is highest, N5 is lowest.
        // Disallow when user already passed a higher level and requests lower level.
        return requestedRank > passedRank;
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
}