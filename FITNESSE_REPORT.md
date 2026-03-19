# FitNesse Usage Report for Cora

## Table of Contents

1. [Overview](#1-overview)
2. [How FitNesse Is Used](#2-how-fitnesse-is-used)
3. [Architecture and Design](#3-architecture-and-design)
4. [Test Organization and Patterns](#4-test-organization-and-patterns)
5. [Fixture Integration](#5-fixture-integration)
6. [What Can Be Improved](#6-what-can-be-improved)
7. [Suggestions for Better and Faster Testing](#7-suggestions-for-better-and-faster-testing)

---

## 1. Overview

The `cora-fitnesse` repository is a **pure test definition and living documentation** repository for the Cora platform. It contains **299 FitNesse wiki test pages** organized into executable acceptance tests that exercise the Cora REST API and verify rule-based acceptance criteria.

**Key characteristics:**
- No Java source code — all fixture implementations live in the separate `cora-fitnesseintegration` repository
- Uses the **SLIM** test system (not FIT)
- Tests run against a live Cora system via REST API calls
- Serves dual purpose: **executable tests** and **readable documentation**

---

## 2. How FitNesse Is Used

### 2.1 Test Execution Model

FitNesse is configured to use the **SLIM protocol** (`!define TEST_SYSTEM {slim}`), which provides a cleaner separation between test definitions and fixture code. Tests are executed through:

1. **Decision Tables** — Used for CRUD operations via `RecordEndpointFixture` and `ChildComparerFixture`
2. **Script Tables** — Used for multi-step procedural flows (login, configuration, regex extraction)
3. **RestFixture Tables** — Used for direct HTTP/REST API calls via `smartrics.rest.fitnesse.fixture.RestFixture`

### 2.2 Global Setup and Authentication

The `SuiteSetUp.wiki` at the root level handles:
- **Fixture imports** from 12 packages in `se.uu.ub.cora.fitnesseintegration`
- **System configuration** (URLs, dependency providers)
- **Authentication** for 3 users via IDP login:
  - `$adminAuthToken` — Full administrative access
  - `$userAuthToken` — Regular user access
  - `$guestAuthToken` — Guest/anonymous access

### 2.3 Test Data Lifecycle

The `CoraTests/SuiteSetUp.wiki` manages test data:
- Uses `RememberRecordState` fixture to snapshot records before tests
- Creates a pool of test data via `ExamplesPool.wiki` (texts, metadata, record types, roles, rules)
- `SuiteTearDown.wiki` restores all modified records and deletes 81+ test records

### 2.4 Variable-Driven Helper Pages

The `HelperPages/` directory provides **37 reusable wiki pages** that accept variables and perform common operations:

| Helper Page | Purpose |
|---|---|
| `createRecord.wiki` | Creates a record using `ChildComparerFixture` |
| `readRecord.wiki` | Reads a record and stores it |
| `updateRecord.wiki` | Updates a record with provided data |
| `deleteRecord.wiki` | Deletes a record by type and ID |
| `searchRecord.wiki` | Searches records by query |
| `compareRecord.wiki` | Compares stored record against expected data |
| `checkActionsForRecord.wiki` | Verifies available actions on a record |
| `uploadResource.wiki` | Uploads files to binary records |

These helpers are included in test pages using `!include -seamless .HelperPages.createRecord`, with variables defined beforehand:
```wiki
!define recordType {text}
!define createData {<JSON payload>}
!include -seamless .HelperPages.createRecord
```

---

## 3. Architecture and Design

### 3.1 Repository Separation

```
cora-fitnesse              (Test definitions — .wiki pages only)
    └── depends on ──►
cora-fitnesseintegration   (Java fixture implementations)
    └── depends on ──►
Cora platform              (System under test)
```

This separation means:
- Test **intent** (wiki pages) is kept separate from test **mechanism** (Java fixtures)
- Test pages are readable by non-developers
- Fixture code can be versioned and tested independently

### 3.2 Key Fixtures (from cora-fitnesseintegration)

| Fixture Class | Purpose |
|---|---|
| `RecordEndpointFixture` | CRUD operations on Cora REST API |
| `ChildComparerFixture` | Create/read/update/search + compare operations |
| `AuthenticationFixture` | IDP login and auth token management |
| `RememberRecordState` | Snapshot/restore record state for test isolation |
| `ActionComparerFixture` | Verify available actions on records |
| `PermissionComparerFixture` | Compare permission structures |
| `ExtractSubstringUsingRegex` | Extract values from responses using regex |
| `AuthTokenHolder` | Store and retrieve authentication tokens |
| `SystemUrl` | Configure system URLs |
| `DependencyProvider` | Configure HTTP handlers and comparers |
| `LoginToken` | Manage login credentials |
| `RestFixture` (smartrics) | Direct HTTP REST calls with response validation |

### 3.3 Test Categories

The test suite covers 8 major areas:

1. **Access Control** — Authentication (AppToken, AuthToken, IDP, Password) and Authorization (Roles, Rules)
2. **Built-in Metadata** — Metadata definitions, consistency checks, duplicate ID detection
3. **Call Through Java Code** — Direct Java fixture execution via Maven
4. **Data Operations** — Record CRUD, search, binary file handling (Image, PDF, TIFF, IIIF)
5. **REST API** — JSON and XML response validation, deployment info
6. **Storage** — Archive create/read/update/delete operations

---

## 4. Test Organization and Patterns

### 4.1 Directory Hierarchy

```
FitNesseRoot/
├── SuiteSetUp.wiki              # Global authentication and imports
├── content.txt                   # SLIM config, global variables
├── CoraTests/
│   ├── SuiteSetUp.wiki          # Test data pool creation, state snapshots
│   ├── SuiteTearDown.wiki       # Cleanup: delete 81+ records, restore state
│   ├── AccessControl/
│   │   ├── Authentication/      # 12+ subdirectories
│   │   └── Authorization/       # Roles, Rules, RecordTypeSettings
│   ├── BuiltInMetadata/
│   ├── Data/
│   │   ├── Record/              # CRUD + search tests
│   │   └── Binary/              # File upload/download tests
│   ├── REST/
│   │   ├── JSON/                # JSON API tests
│   │   └── XML/                 # XML API tests
│   └── Storage/
│       └── Archive/             # Archive CRUD
├── HelperPages/                  # 37 reusable test components
├── DocumentationPage/            # Legacy docs (Data, Metadata, Presentation)
└── FrontPage/                    # Landing page
```

### 4.2 Common Test Patterns

**Pattern 1: Create-Read-Verify**
```wiki
!define recordType {text}
!define createData {<JSON>}
!include -seamless .HelperPages.createRecord
!include -seamless .HelperPages.readRecord
!include -seamless .HelperPages.compareRecord
```

**Pattern 2: Variable-driven test cases with explicit cases**
```wiki
!***> Case 1: Description of scenario
!define currentAuthToken {$adminAuthToken}
!define recordId {$someId}
!define updateData {<JSON payload>}
!define expectedupdateResponseRegex {expected pattern}
!include -seamless <User.ActionHelpers.UpdateRecordAndEnsureResponse
*!
```

**Pattern 3: RestFixture for direct HTTP testing**
```wiki
| Table:smartrics.rest.fitnesse.fixture.RestFixture | ${systemUnderTestUrl} |
| POST | /rest/record/metadata | 201 | Content-Type : application/vnd.cora.record\+json | |
| DELETE | /rest/record/metadata/someId | 200 | | |
```

**Pattern 4: State management with RememberRecordState**
```wiki
!| RememberRecordState |
| type | id | version | remember? |
| user | 121212 | beforeTest | OK |
```

### 4.3 Test Isolation Strategy

- **Before each suite**: Snapshots of key records are taken using `RememberRecordState`
- **Test data pool**: A dedicated set of test records (text, metadata, recordTypes, roles) is created
- **After each suite**: All test records are deleted, original records are restored
- **Overwrite protection**: Tests use `ignoreOverwriteProtection` to bypass concurrency controls

---

## 5. Fixture Integration

### 5.1 Dependencies (from pom.xml)

| Dependency | Version | Purpose |
|---|---|---|
| `fitnesseintegration` | 6.11.0 | Core Cora FitNesse fixtures |
| `smartrics-RestFixture` | 4.4 | REST API testing |
| `basicdata` | 7.2.0 | Cora data utilities |
| `xmlconverter` | 3.8.0 | XML conversion |
| `coralog4j` | 2.16.0 | Logging |
| `testng` | — | Test framework |

### 5.2 Maven Shade Plugin

The build uses `maven-shade-plugin` to package all dependencies into a single uber-JAR, allowing FitNesse to load all required classes from one artifact.

---

## 6. What Can Be Improved

### 6.1 Test Readability

**Issue**: JSON payloads are embedded inline in wiki pages, making them extremely long and hard to read. For example, in `GlobalFitnesseVariables.wiki`, single variable definitions span hundreds of characters of minified JSON.

**Recommendation**: Extract large JSON payloads into separate files under `FitNesseRoot/files/testResources/` and reference them from test pages. This improves readability and makes payload changes easier to review in version control diffs.

### 6.2 Legacy Documentation Migration

**Issue**: The `DocumentationPage/` directory contains legacy documentation that duplicates content being moved into `CoraTests/`. The FrontPage already states this migration should happen.

**Recommendation**: Complete the migration of legacy documentation into `CoraTests/` and remove `DocumentationPage/` to avoid confusion about the authoritative source.

### 6.3 Hardcoded Values and Magic Numbers

**Issue**: Test pages contain hardcoded user IDs (`121212`, `131313`, `141414`, `12345`), timestamps, and system-specific values that reduce portability and clarity.

**Recommendation**: Define these as named variables in `GlobalFitnesseVariables.wiki` with descriptive names (e.g., `${fitnesseUserUserId}`, `${fitnesseAdminUserId}`) to improve self-documentation and maintainability.

### 6.4 Tear-Down Complexity

**Issue**: `SuiteTearDown.wiki` deletes 81+ records individually. This is fragile — if a test adds new records but the tear-down is not updated, orphaned test data may remain.

**Recommendation**: Consider implementing a tag-based or prefix-based cleanup strategy where all test-created records share a naming convention (e.g., `fitnesse*`) and can be cleaned up programmatically.

### 6.5 Test Dependencies and Ordering

**Issue**: Some tests depend on state created by previous tests (e.g., using `$createCase1RecordId` from a create test in an update test), creating implicit ordering dependencies.

**Recommendation**: Document these dependencies explicitly or restructure tests to be more self-contained. Each test page should ideally set up its own prerequisites within its local SetUp section.

### 6.6 Error Messages and Diagnostics

**Issue**: When tests fail, the feedback from FitNesse tables can be difficult to interpret, especially for JSON comparison failures on large payloads.

**Recommendation**: Add more descriptive test case labels and consider using the `ComparerFixture` with more granular assertions rather than full-payload regex matching.

### 6.7 TODO Items in Configuration

**Issue**: There is an unresolved TODO in `content.txt`:
```
# TODO: Move archiveFileSystemPath more near to Archive Tests
```

**Recommendation**: Address existing TODOs to keep the codebase clean.

### 6.8 Typos in Documentation

**Issue**: The FrontPage contains the typo "Interessting links" (should be "Interesting links").

**Recommendation**: Fix documentation typos to maintain professionalism.

---

## 7. Suggestions for Better and Faster Testing

### 7.1 Parallel Test Execution

**Status**: ✅ Implemented

All six test suites under `CoraTests` have been tagged with `independent` in their `Suites` property, enabling them to be run concurrently via FitNesse's suite filter feature:

| Suite | Tag | Can Run in Parallel |
|---|---|---|
| AccessControl | `independent` | ✅ Yes |
| BuiltInMetadata | `independent` | ✅ Yes |
| CallThroughJavaCode | `independent` | ✅ Yes |
| Data | `independent` | ✅ Yes |
| REST | `independent` | ✅ Yes |
| Storage | `independent` | ✅ Yes |

**How to use:**
- Run all independent suites: `?suite&suiteFilter=independent`
- Run individual suites in parallel via separate processes or CI/CD jobs
- See the `CoraTests.ParallelExecution` wiki page for full documentation

**Constraint**: The global `SuiteSetUp.wiki` must complete before parallel execution begins, and `SuiteTearDown.wiki` should run after all suites complete.

### 7.2 Selective Test Execution with Tags

**Suggestion**: Leverage FitNesse's tagging system to categorize tests by:
- **Speed**: `fast`, `slow` (e.g., binary upload tests are slower)
- **Feature**: `auth`, `crud`, `search`, `binary`, `rest`
- **Risk**: `smoke`, `regression`, `edge-case`

This enables running quick smoke tests during development and full regression suites before releases.

### 7.3 Reduce Test Data Setup Overhead

**Current state**: `ExamplesPool.wiki` creates a large pool of test data for every suite run (texts, metadata variables, collections, record types, presentations, roles, rules).

**Suggestion**: 
- **Pre-seed approach**: Maintain a known test data state in the system and only create/modify what each test specifically needs.
- **Lazy initialization**: Only create test data when the specific test that needs it runs.
- **Shared fixtures**: Use FitNesse's `SuiteSetUp` at different levels to set up only the data needed for that sub-suite.

### 7.4 API Contract Testing

**Suggestion**: Complement FitNesse acceptance tests with lightweight API contract tests (e.g., using OpenAPI/Swagger schemas). These can:
- Run faster than full FitNesse suites
- Catch structural API changes early
- Be integrated into CI/CD pipelines as a fast feedback loop

### 7.5 Response Validation Improvements

**Current state**: Many tests use regex-based validation (`=~/pattern/`) or string matching on JSON responses, which is brittle and hard to maintain.

**Suggestion**: 
- Use the `ChildComparerFixture` more extensively for structured JSON comparison instead of regex matching.
- Consider implementing a JSON path-based assertion fixture that allows targeted field validation (e.g., `$.record.data.recordInfo.id == "expectedId"`) rather than full-payload comparisons.

### 7.6 Test Documentation Template

**Suggestion**: Standardize test page structure with a template:

```wiki
!1 Feature Name
Brief description of the feature being tested.

!2 Acceptance Criteria
 * Criterion 1
 * Criterion 2

!2 Prerequisites
 * Required data/state

!2 Test Cases
!***> Case 1: Description
[test implementation]
*!
```

This ensures consistency across the 299 test pages and makes it easier for new team members to contribute.

### 7.7 CI/CD Integration Optimization

**Suggestion**: Structure test execution in CI/CD pipelines as:

1. **Fast feedback** (< 2 min): Smoke tests — basic auth, simple CRUD, API health
2. **Medium feedback** (< 10 min): Feature-level tests — one representative test per feature area
3. **Full regression** (complete suite): All 299 tests — run nightly or before releases

### 7.8 Test Impact Analysis

**Suggestion**: Maintain a mapping between Cora system components and their corresponding FitNesse test pages. When a specific component changes, only the relevant tests need to run, significantly reducing feedback time during development.

### 7.9 Improved Binary Test Efficiency

**Current state**: Binary tests upload real files (TIFF, PDF) which are inherently slow due to file I/O and processing.

**Suggestion**: Use minimal-size test files (e.g., 1x1 pixel images, single-page PDFs) for functional tests and reserve full-size file tests for dedicated performance/integration suites.

### 7.10 Better State Management

**Suggestion**: Consider implementing a transactional test pattern where:
- Each test suite starts a "transaction" (marks its starting state)
- Tests run and make changes
- The suite automatically "rolls back" to the starting state

This would be more reliable than the current manual record-by-record cleanup in `SuiteTearDown.wiki`.

---

## Summary

The `cora-fitnesse` repository is a well-structured acceptance test suite that effectively uses FitNesse as both a testing framework and living documentation platform. The separation of test definitions (wiki pages) from fixture implementations (`cora-fitnesseintegration`) is a sound architectural choice that promotes maintainability.

The main areas for improvement center around:
- **Readability**: Extracting inline JSON, standardizing templates, fixing hardcoded values
- **Speed**: Parallel execution, selective running with tags, optimized test data setup
- **Reliability**: Better state management, reduced test interdependencies, more robust cleanup
- **Maintainability**: Completing legacy doc migration, addressing TODOs, better error diagnostics
