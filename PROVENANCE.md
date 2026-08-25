# Provenance

The leave algorithms in `Engines.java` are copied unchanged from the Payslate repository
at commit `8829abc` (the state before the differential-fuzzing work):

- The ordinary/overtime split loop and the frozen-engine leave (`double` accumulation of
  `1.0/60.0` per ordinary minute, times `0.076923`, HALF_EVEN to four places) come from
  `pay-engine/src/main/java/com/elsworne/payslate/engine/LegacyPayEngine.java` (permanent
  employee; the casual minimum-call and money, tax and retirement paths are omitted as this
  spike compares leave only).
- The refactored-engine leave (exact integer minutes divided to ten places, times
  `0.076923`, HALF_EVEN to four places) comes from
  `pay-engine/src/main/java/com/elsworne/payslate/engine/PayEngine.java`.

The byte-to-month mapping in `Roster.java` matches that repository's
`DifferentialEngineFuzzTest`. All data is synthetic.
