#!/bin/sh
# Compare the two targets: each one N times under the same time budget, recording whether
# and when each run first hit a leave divergence. Every run starts from an empty corpus and
# lets Jazzer pick its own random seed, which the log records. Needs an otherwise idle
# machine; not part of CI. Usage: sh compare.sh [N] [DURATION]
set -eu
cd "$(dirname "$0")"
N="${1:-10}"
DURATION="${2:-2m}"
OUT="compare-out"
rm -rf "$OUT"; mkdir -p "$OUT"
echo "machine: $(uname -sm), $(getconf _NPROCESSORS_ONLN) CPUs; $(java -version 2>&1 | head -1)"
echo "N=$N runs per target, $DURATION each"
for target in DifferentialLeaveFuzzTest StateFeedbackLeaveFuzzTest; do
  for i in $(seq 1 "$N"); do
    # no working corpus and no finding left over from the previous run
    rm -rf .cifuzz-corpus src/test/resources crash-*
    JAZZER_FUZZ=1 ./mvnw -B -ntp test -Dtest="$target" -Djazzer.max_duration="$DURATION" \
      > "$OUT/$target-run$i.log" 2>&1 || true
  done
done
rm -rf .cifuzz-corpus src/test/resources crash-*
python3 summarise.py "$OUT" | tee "$OUT/summary.txt"
