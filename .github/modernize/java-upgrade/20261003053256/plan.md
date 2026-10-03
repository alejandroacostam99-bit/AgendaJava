# Upgrade Plan: agenda-contactos (20261003053256)

- **Generated**: 2026-10-03 01:33:36 -04:00
- **HEAD Branch**: N/A
- **HEAD Commit ID**: N/A

## Available Tools

**JDKs**
- JDK 17: not available (baseline will be skipped)
- JDK 21: `C:\Program Files\Java\jdk-21.0.11\bin` (installed; not used for this target)
- JDK 25: **<TO_BE_INSTALLED>** (required by Steps 1 and 5)

**Build Tools**
- Maven: **<TO_BE_INSTALLED>** (no system Maven or Maven Wrapper found; required for Steps 2-5)

## Guidelines

> Note: You can add any specific guidelines or constraints for the upgrade process here if needed, bullet points are preferred.

## Options

- Working branch: N/A (not a Git repository)
- Run tests before and after the upgrade: true

## Upgrade Goals

- Upgrade Java runtime/compiler target from 17 to Java 25 LTS.

## Technology Stack

| Technology/Dependency | Current | Min Compatible Version | Why Incompatible |
| --------------------- | ------- | ---------------------- | ---------------- |
| Java | 17 | 25 | User requested Java 25 LTS |
| Maven | Not installed; no wrapper | 3.9.x | Required to build; install current supported Maven during setup |
| maven-compiler-plugin | Implicit Maven default | 3.14.1 | Explicit version needed to support Java 25 `release` compilation |
| maven-shade-plugin | 3.5.0 | 3.5.0 | No known Java 25 incompatibility; unchanged |
| org.xerial:sqlite-jdbc | 3.45.2.0 | 3.45.2.0 | No known Java 25 source/API incompatibility; scan for CVEs before deciding whether an upgrade is required |
| Swing/JDBC application sources | Java 17-compatible APIs | Java 25 | No detected removed JDK APIs or internal packages |

## Derived Upgrades

- Set the compiler release to 25 and explicitly use maven-compiler-plugin 3.14.1 so compilation is validated against the Java 25 API.
- Install JDK 25 and Maven because neither target JDK nor Maven is currently available; the existing JDK 21 does not satisfy the requested target.

## Impact Analysis

### Dependency Changes

| File | Dependency | Current | Action | Target | Reason |
|------|-----------|---------|--------|--------|--------|
| pom.xml | maven-compiler-plugin | Implicit Maven default | add | 3.14.1 | Explicit Java 25 release support |
| pom.xml | org.xerial:sqlite-jdbc | 3.45.2.0 | Conditional upgrade | Patched version if reported vulnerable | Only change if CVE scan finds an applicable issue with a patched release |

### Source Code Changes

No source changes identified. The application uses standard Swing and JDBC APIs; no JDK internals, removed Java EE modules, reflective access to JDK internals, or language constructs requiring migration were found.

### Configuration Changes

| File | Property/Setting | Current | Required Change | Reason |
|------|-----------------|---------|----------------|--------|
| pom.xml | maven.compiler.source / maven.compiler.target | 17 / 17 | Replace with maven.compiler.release=25 | Compile against Java 25 language and API level |
| pom.xml | maven-compiler-plugin | Not explicitly configured | Add version 3.14.1 | Reliable Java 25 compiler support |

### CI/CD Changes

No CI/CD files or hardcoded Java runtime declarations were found.

### Risks & Warnings

- **SQLite JDBC runtime compatibility**: The existing SQLite JDBC dependency predates Java 25. Compilation alone does not prove native SQLite loading on this runtime. **Mitigation**: Run the full Maven test lifecycle and package the shaded application; if startup is practical in the environment, confirm the driver loads without changing database data. Upgrade only if scan or runtime evidence requires it.
- **No GUI smoke test**: The app is a Swing desktop application and may not be launchable in a headless environment. **Mitigation**: Validate clean compile, tests, and shaded package; record the limitation if GUI startup cannot be exercised.
- **No Git repository**: Changes cannot be branched or committed here; leave files in the working directory and note this in progress/summary.

## Upgrade Steps

- Step 1: Setup Environment
  - **Rationale**: Java 25 and Maven are required to apply and validate the requested target; neither is currently available.
  - **Changes to Make**: Install JDK 25 and the current supported Maven release. No project files change in this setup step.
  - **Verification**: List installed JDKs and confirm JDK 25; run Maven version check with JDK 25.

- Step 2: Setup Baseline
  - **Rationale**: Establish pre-upgrade compilation and test results where the current JDK is available.
  - **Changes to Make**: No project file changes. Skip because JDK 17, the project's current compiler target, is not installed.
  - **Verification**: Skipped; record unavailable base JDK and proceed.

- Step 3: Configure Java 25 Compilation
  - **Rationale**: Set the requested Java API/language level and ensure the compiler plugin recognizes Java 25.
  - **Changes to Make**: Apply the Configuration Changes and maven-compiler-plugin dependency change above.
  - **Verification**: With JDK 25, run `mvn clean test-compile -q`; main and test sources must compile.

- Step 4: CVE Validation and Fix
  - **Rationale**: Check direct dependencies after the project target is updated and patch any applicable vulnerable dependency found.
  - **Changes to Make**: Scan direct dependencies; change a dependency version only when the scan reports an applicable CVE with an available fix.
  - **Verification**: Recompile after any dependency update and rescan to confirm resolution.

- Step 5: Final Validation
  - **Rationale**: Verify the Java 25 target, test suite, and executable shaded artifact after all changes.
  - **Changes to Make**: Resolve any local compile/test/package failures; do not leave temporary workarounds or TODOs.
  - **Verification**: With JDK 25, run `mvn clean test-compile -q`, `mvn clean test -q`, and `mvn clean package -q`; all must succeed. Confirm the produced jar is present and Java reports version 25.
