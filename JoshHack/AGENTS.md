# JoshHack Agent Instructions

- Build/test with Java 17, Maven Wrapper (`mvnw`/`mvnw.cmd`), and JUnit 5.
- Before work is complete, run:
  - `.\mvnw.cmd clean test`
  - `.\mvnw.cmd org.pitest:pitest-maven:mutationCoverage`
  - `.\mvnw.cmd spotbugs:spotbugs`
- Preserve existing behavior unless a change is explicitly requested.
- Never weaken, remove, or disable tests just to make the build pass.
- Clearly identify any production-code changes for human review.
- Never push or merge automatically.
