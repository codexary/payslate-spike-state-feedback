package com.elsworne.payslate.spike;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Duration;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

/**
 * The permanent-employee leave algorithms of both pay engines, copied unchanged from
 * rebuilt payslate at 8829abc (see PROVENANCE.md). The ordinary/overtime split is shared;
 * the two engines differ only in how they carry the ordinary total that feeds leave:
 * the frozen engine accumulates a double (1.0/60.0 per ordinary minute), the refactored
 * engine divides the exact integer minute count. Leave accrues on ordinary hours at
 * 0.076923, rounded HALF_EVEN to four places.
 */
public final class Engines {
  public record Shift(LocalDateTime start, LocalDateTime end, boolean publicHoliday) {}

  /** Carries both ordinary totals the two engines produce for the same roster. */
  public record Ordinary(int minutes, double accumulated) {}

  private static final BigDecimal RATE = new BigDecimal("0.076923");
  private static final BigDecimal SIXTY = new BigDecimal("60");

  /** The shared split loop (permanent employee), copied from LegacyPayEngine.calculate. */
  public static Ordinary ordinary(List<Shift> shifts) {
    for (Shift shift : shifts) {
      if (shift == null || shift.start() == null || shift.end() == null || !shift.end().isAfter(shift.start()))
        throw new IllegalArgumentException("Invalid shift");
    }
    ArrayList<Shift> ordered = new ArrayList<>(shifts);
    Collections.sort(ordered, Comparator.comparing(Shift::start));
    double ordinary = 0;
    int ordinaryMinutes = 0;
    HashMap<LocalDate, Integer> weeklyMinutes = new HashMap<>();
    HashMap<LocalDate, Integer> dailyMinutes = new HashMap<>();
    for (Shift s : ordered) {
      long minutes = Duration.between(s.start(), s.end()).toMinutes();
      if (minutes > 24 * 60 || minutes < 1) throw new IllegalArgumentException("Shift must be 1 minute to 24 hours");
      LocalDateTime clock = s.start();
      for (int m = 0; m < (int) minutes; m++) {
        LocalDate date = clock.toLocalDate();
        int daily = dailyMinutes.getOrDefault(date, 0);
        dailyMinutes.put(date, daily + 1);
        LocalDate week = date.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
        int weekMinutes = weeklyMinutes.getOrDefault(week, 0) + 1;
        weeklyMinutes.put(week, weekMinutes);
        boolean extra = daily >= 8 * 60 || weekMinutes > 38 * 60;
        if (!extra) {
          ordinary += 1.0 / 60.0;
          ordinaryMinutes++;
        }
        clock = clock.plusMinutes(1);
      }
    }
    return new Ordinary(ordinaryMinutes, ordinary);
  }

  /** Refactored engine: leave from the exact integer minute count. */
  public static BigDecimal leaveExact(int ordinaryMinutes) {
    return BigDecimal.valueOf(ordinaryMinutes).divide(SIXTY, 10, RoundingMode.HALF_EVEN)
        .multiply(RATE).setScale(4, RoundingMode.HALF_EVEN);
  }

  /** Frozen engine: leave from the double that accumulated 1.0/60.0 per ordinary minute. */
  public static BigDecimal leaveDouble(double accumulated) {
    return BigDecimal.valueOf(accumulated).multiply(RATE).setScale(4, RoundingMode.HALF_EVEN);
  }

  private Engines() {}
}
