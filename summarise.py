#!/usr/bin/env python3
"""Summarise compare.sh: for each target and run, the seed Jazzer used, whether the run hit
a leave divergence, and how many executions and whole seconds it took to stop (at the first
finding, or at the time budget). Prints medians and ranges over the runs that found it."""
import pathlib
import re
import statistics
import sys


def parse(text):
    seed = re.search(r'INFO: Seed: (\d+)', text)
    done = re.search(r'Done (\d+) runs in (\d+) second', text)
    return (int(seed.group(1)) if seed else None,
            int(done.group(1)) if done else None,
            int(done.group(2)) if done else None,
            'Test unit written to' in text)


out = pathlib.Path(sys.argv[1] if len(sys.argv) > 1 else 'compare-out')
for target in ('DifferentialLeaveFuzzTest', 'StateFeedbackLeaveFuzzTest'):
    logs = sorted(out.glob(f'{target}-run*.log'), key=lambda p: int(re.search(r'run(\d+)', p.name).group(1)))
    runs = [(log.name,) + parse(log.read_text(errors='replace')) for log in logs]
    hits = [r for r in runs if r[4] and r[2] is not None]
    print(f'{target}: {len(hits)}/{len(runs)} runs found the divergence')
    for name, seed, execs, secs, found in runs:
        print(f'  {name:42} seed {seed}  {"found" if found else "none "}  {execs} execs  {secs} s')
    if hits:
        e = sorted(r[2] for r in hits)
        s = sorted(r[3] for r in hits)
        print(f'  executions to first finding: median {statistics.median(e)}, range {e[0]}-{e[-1]}')
        print(f'  seconds to first finding:    median {statistics.median(s)}, range {s[0]}-{s[-1]}')
