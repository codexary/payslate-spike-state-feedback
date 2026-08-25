package com.elsworne.payslate.spike;

// The baseline target: over a month of shifts, the two engines must agree on the accrued
// leave. The fuzzer breaks this only when the ordinary-minute total lands on one of the
// few HALF_EVEN boundaries where the double accumulation and the exact divide round apart.

import static org.assertj.core.api.Assertions.assertThat;

import com.code_intelligence.jazzer.junit.FuzzTest;
import java.util.List;

class DifferentialLeaveFuzzTest {
  @FuzzTest(maxDuration = "2m")
  void leaveAgrees(byte[] data) {
    List<Engines.Shift> shifts = Roster.ofBytes(data);
    if (shifts.isEmpty()) return;
    Engines.Ordinary o;
    try {
      o = Engines.ordinary(shifts);
    } catch (IllegalArgumentException rejected) {
      return;
    }
    assertThat(Engines.leaveExact(o.minutes())).isEqualByComparingTo(Engines.leaveDouble(o.accumulated()));
  }
}
