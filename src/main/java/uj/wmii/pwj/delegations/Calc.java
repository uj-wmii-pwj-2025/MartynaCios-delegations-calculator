package uj.wmii.pwj.delegations;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Duration;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

public class Calc {

    private static final int HOURS_IN_DAY = 24;
    private static final int MINUTES_IN_HOUR = 60;
    private static final int LIMIT_ONE_THIRD = 8;
    private static final int LIMIT_HALF = 12;

    BigDecimal calculate(String name, String start, String end, BigDecimal dailyRate) throws IllegalArgumentException {

        DateTimeFormatter datePattern = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm z");

        ZonedDateTime startTime = ZonedDateTime.parse(start, datePattern);
        ZonedDateTime endTime = ZonedDateTime.parse(end, datePattern);

        Duration duration = Duration.between(startTime, endTime);
        long totalMinutes = duration.toMinutes();

        if (totalMinutes <= 0) {
            return BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);
        }

        long minutesPerDay = HOURS_IN_DAY * MINUTES_IN_HOUR;
        long fullDays = totalMinutes / minutesPerDay;
        long remainingMinutes = totalMinutes % minutesPerDay;

        BigDecimal amount = dailyRate.multiply(BigDecimal.valueOf(fullDays));

        if (remainingMinutes > 0) {
            double hoursRemaining = (double) remainingMinutes / MINUTES_IN_HOUR;
            amount = amount.add(calculatePartial(hoursRemaining, dailyRate));
        }

        return amount.setScale(2, RoundingMode.HALF_UP);
    }

    private BigDecimal calculatePartial(double hours, BigDecimal rate) {

        if (hours > LIMIT_HALF) {
            return rate;
        }

        if (hours > LIMIT_ONE_THIRD) {
            return rate.multiply(BigDecimal.valueOf(0.5));
        }

        return rate.divide(BigDecimal.valueOf(3), 2, RoundingMode.HALF_UP);
    }
}