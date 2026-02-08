# CLAUDE.md

Visualize Ruby code metrics in Jenkins: Rcov coverage, Rails stats, Rails notes, and Flog complexity.

## Stack
- Java 8
- Maven
- Jenkins plugin framework 2.19+
- Depends on rake-plugin 1.8.0+

## Build & Test
```bash
mvn clean verify
mvn hpi:run  # test plugin locally
```

## Notes
- Parses Ruby tool output and generates trend graphs
- HTML parsing via htmlparser 1.6
- Regex pattern matching with jregex
- Integrates with existing Ruby-based Jenkins workflows
