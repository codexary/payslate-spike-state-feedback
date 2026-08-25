package com.elsworne.payslate.spike;

// The same differential leave check as DifferentialLeaveFuzzTest, with Jazzer's state
// feedback added: the whole number of ordinary hours is reported as domain state, so an
// input that reaches a new ordinary-hour total counts as progress even when it covers no
// new code. This does not hand the fuzzer the answer -- it does not know which totals
// diverge -- it only rewards exploring the total that the divergence depends on.

import static org.assertj.core.api.Assertions.assertThat;

import com.code_intelligence.jazzer.api.Jazzer;
import com.code_intelligence.jazzer.junit.FuzzTest;
import java.util.List;

class StateFeedbackLeaveFuzzTest {
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
    Jazzer.exploreState((byte) ((o.minutes() / 60) & 0xff), 0);
    assertThat(Engines.leaveExact(o.minutes())).isEqualByComparingTo(Engines.leaveDouble(o.accumulated()));
  }
}
