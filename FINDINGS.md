English | [简体中文](FINDINGS.zh-CN.md)

# Findings: did state feedback help?

## What was compared

`DifferentialLeaveFuzzTest` and `StateFeedbackLeaveFuzzTest` use the same byte-to-month
mapping and the same check (the two engines' leave must agree); the second also reports
the whole number of ordinary hours to `Jazzer.exploreState`. `compare.sh` ran each target
10 times with a 2-minute budget per run, every run from an empty corpus, the two targets
back to back in one session. Jazzer chose each run's random seed; the logs record it.

The machine had other workloads running throughout (load average about 4.5 to 6), so the
absolute times say little about any other machine. Only the two configurations, measured
in the same session under the same load, are compared with each other.

## Results

| | Baseline | State feedback |
| --- | --- | --- |
| Runs that found a divergence | 10 of 10 | 10 of 10 |
| Executions to the first finding, median | 15733 | 10293.5 |
| Executions to the first finding, range | 4561–39929 | 6482–18838 |
| Seconds to the first finding, median (whole seconds) | 2.5 | 1.0 |
| Seconds to the first finding, range | 0–7 | 0–2 |
| Ordinary-minute total of the divergence found | 3000 ×3, 7000 ×5, 9000 ×2 | 3000 ×9, 7000 ×1 |

## What this shows

Both targets found a leave divergence in every run, within seconds. With this mapping the
divergence is not hard to reach, so state feedback was not needed to find it.

State feedback reached it with fewer executions at the median and with a much narrower
spread: its slowest run took fewer executions than the baseline's four slowest. It also
nearly always stopped at the smallest diverging total, 3000 minutes (50 hours), while the
baseline stopped at whichever total it happened to cross. That matches what the feedback
rewards: each new whole-hour total counts as progress, so the fuzzer works its way up the
totals and meets 50 hours first.

Ten runs each, with overlapping ranges, is a small sample; the difference is consistent
in direction but modest in size. The comparison did not measure the cost of the feedback
itself separately.

## Why this is not handing the fuzzer the answer

The state is the whole number of ordinary hours, not the leave value, and the fuzzer is
never told which totals diverge. The feedback only rewards exploring the quantity the
divergence depends on. Knowing which quantity that is came from reading the two engines
and working the found input through by hand.

## Decision

Keep the main repository's differential target without state feedback: it reaches these
totals in seconds already, and its oracle no longer treats the known leave difference as a
finding. Keep the technique in mind for targets where the state that matters does not show
up as new coverage.
