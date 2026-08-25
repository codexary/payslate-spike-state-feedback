package com.elsworne.payslate.spike;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/** Turns fuzzer bytes into a month of shifts, the same mapping as the main repository's
 * DifferentialEngineFuzzTest: two bytes per day (1..1440 minutes, high bit a public-holiday
 * flag), consecutive days from a fixed Monday, up to 31 days. */
public final class Roster {
  private static final LocalDate START = LocalDate.of(2026, 3, 2);
  public static final int MAX_DAYS = 31;

  public static List<Engines.Shift> ofBytes(byte[] data) {
    int days = Math.min(data.length / 2, MAX_DAYS);
    List<Engines.Shift> shifts = new ArrayList<>();
    for (int i = 0; i < days; i++) {
      int hi = data[2 * i] & 0xff;
      int lo = data[2 * i + 1] & 0xff;
      int minutes = 1 + (((hi << 8) | lo) % (24 * 60));
      boolean holiday = (hi & 0x80) != 0;
      var start = START.plusDays(i).atTime(8, 0);
      shifts.add(new Engines.Shift(start, start.plusMinutes(minutes), holiday));
    }
    return shifts;
  }

  private Roster() {}
}
