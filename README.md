English | [简体中文](README.zh-CN.md)

# payslate-spike-state-feedback

A small experiment: does telling the fuzzer that a new total of ordinary hours is
progress help it reach the leave-rounding divergence sooner than coverage feedback alone?

The two pay engines compute the same money from exact minutes but carry the ordinary
total differently — the frozen engine in a `double`, the refactored engine as exact
integer minutes — so their displayed leave rounds apart on a few ordinary-minute totals.
A plain differential target has to stumble onto one of those totals. A second target adds
Jazzer's state feedback, bucketing by whole ordinary hours, so a never-seen bucket counts as
progress.

- `Engines` — the permanent-employee leave algorithms of both engines, copied unchanged
  (see `PROVENANCE.md`).
- `Roster` — the same byte-to-month mapping as the main repository's differential target.
- `DifferentialLeaveFuzzTest` — the baseline: the two engines must agree on the leave.
- `StateFeedbackLeaveFuzzTest` — the same check, with `Jazzer.exploreState` on the whole
  number of ordinary hours.
- `compare.sh` — runs each target several times under the same budget, each run from an
  empty corpus with a random seed that the log records, and `summarise.py` reports when
  each run first hit a divergence.

Java 21, the Maven wrapper (`./mvnw`), Jazzer 0.30.0. `./mvnw -B -ntp test` runs both
targets in regression mode; `sh compare.sh [N] [DURATION]` runs the timing comparison and needs
an otherwise idle machine. All data is synthetic.

## About this repository

Elsworne, Payslate and the team are fictional.

The code and documents in this repository were written with the help of AI tools; every recorded output comes from actually running them.
