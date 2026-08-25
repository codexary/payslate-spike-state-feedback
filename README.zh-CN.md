[English](README.md) | 简体中文

# payslate-spike-state-feedback

一个小实验：告诉模糊器“普通工时总数变了就算进展”，能不能比只靠覆盖率反馈更快撞上年假舍入分歧？

两套薪资引擎从精确分钟算出同样的钱，却用不同方式存普通工时总数——冻结引擎用 `double`，重构引擎用精确整数分钟——所以它们显示的年假会在几个普通分钟总数上舍入分开。朴素的差分目标只能靠运气撞上这些总数之一。第二个目标加上 Jazzer 的状态反馈，按普通工时整小时数分桶，没见过的桶就算一次进展。

- `Engines` —— 两套引擎的长期雇员年假算法，按原样复制（见 `PROVENANCE.md`）。
- `Roster` —— 和主仓库差分目标一样的“字节到一个月”映射。
- `DifferentialLeaveFuzzTest` —— 基线：两套引擎的年假必须一致。
- `StateFeedbackLeaveFuzzTest` —— 同样的检查，对普通工时整小时数用 `Jazzer.exploreState`。
- `compare.sh` —— 在同样的预算下把两个目标各跑多次，每次都从空语料开始、随机种子由 Jazzer 自选并记在日志里，由 `summarise.py` 报出每次第一次撞上分歧的时刻。

Java 21、Maven 包装器（`./mvnw`）、Jazzer 0.30.0。`./mvnw -B -ntp test` 在回归模式下跑两个目标；`sh compare.sh [N] [DURATION]` 跑计时比较，需要一台没有别的负载的机器。数据全为合成。

## 关于本仓库

Elsworne、Payslate 和团队成员均为虚构。

本仓库的代码与文档借助 AI 工具编写；记录的输出都来自实际运行。
