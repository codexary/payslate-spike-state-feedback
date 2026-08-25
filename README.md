English | [简体中文](README.zh-CN.md)

# payslate-spike-state-feedback

A small experiment: does telling the fuzzer that a new total of ordinary hours is
progress help it reach the leave-rounding divergence sooner than coverage feedback alone?

The two pay engines compute the same money from exact minutes but carry the ordinary
total differently — the frozen engine in a `double`, the refactored engine as exact
integer minutes — so their displayed leave rounds apart on a few ordinary-minute totals.
A plain differential target has to stumble onto one of those totals.

- `Engines` — the permanent-employee leave algorithms of both engines, copied unchanged
  (see `PROVENANCE.md`).
- `Roster` — the same byte-to-month mapping as the main repository's differential target.
- `DifferentialLeaveFuzzTest` — the baseline: the two engines must agree on the leave.

Java 21, the Maven wrapper (`./mvnw`), Jazzer 0.30.0. `./mvnw -B -ntp test` runs the
target in regression mode. All data is synthetic.

## About this repository

Elsworne, Payslate and the team are fictional.

The code and documents in this repository were written with the help of AI tools; every recorded output comes from actually running them.
