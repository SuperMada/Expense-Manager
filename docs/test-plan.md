# **Test Plan: Employee and Manager Expense Report Apps (Release 1.0)**

## **1\. Introduction**

This plan covers testing of the whole Revature Expense Manager project: the Python Employee App, the Java Manager App and the shared SQLite database.

* Systems under test: Python Employee App (console), Java Manager App (console), shared SQLite database (path set by `EM_DB_PATH`), and the SQL scripts that build it  
* The plan follows the project requirements document (FR, VR and XW IDs)

## **2\. Objective**

Testing must show, with evidence, that both apps meet every Must requirement and work together through the shared database.

* Verify FR-A1 to FR-A4, FR-E1 to FR-E5, FR-M1 to FR-M4, FR-S1 to FR-S5 and validation rules VR-01 to VR-09  
* Prove cross-app behaviour with test cases XW-01 to XW-06  
* Meet the automation minimums in PyTest and in JUnit 5 with Mockito

## **3\. Scope**

The entire project is in scope: both apps, the shared database, and the test assets themselves.

**In scope**

* Employee App (Python): login, submit, view, edit, delete and review history (FR-E1 to FR-E5)  
* Manager App (Java): login, pending list, approve or deny, reports, CSV export (FR-M1 to FR-M4)  
* Authentication and access control in both apps (FR-A1 to FR-A4)  
* Database and system rules: schema and constraints, seed data, expense lifecycle, logging, friendly errors, shared data contract (FR-S1 to FR-S5)  
* All validation rules, VR-01 to VR-09  
* Cross-app test cases XW-01 to XW-06  
* Test assets: SQL validation queries, `validation_cases.csv`, the PyTest and JUnit suites

**Out of scope**

* GUI or web front end, browser automation, API testing, CI/CD, containers  
* Java frameworks, ORMs, async code, code-coverage tools  
* Password hashing or real security; many users at the same time  
* Expense reversal after a decision, receipts, currencies, notifications, bulk import  
* Stretch goals SG-1 to SG-3

## **4\. Test Approach by Level**

Testing is automated wherever possible at unit and integration level, with manual runs for cross-app behaviour and exploration.

| Level | Employee App (PyTest) | Manager App (JUnit 5 and Mockito) |
| ----- | ----- | ----- |
| Unit | 20 tests: validators, services, custom exceptions, mocks for `input`, fixed `today` and forced database errors | 20 tests: validators, services, custom exceptions, mocked DAOs, fixed `Clock` |
| Integration | 5 tests on a real temporary database | 4 tests on a real temporary database |
| SQL validation | 5 queries in `sql/validation/`, each must return zero rows, run from PyTest | Not applicable |
| Menu flow | 1 test with mocked input | 1 test with scripted input |
| Cross-app (manual) | XW-01 to XW-06 with both apps open, in both cycles | Same cases |
| Exploratory | 1 session of about 30 minutes with a written charter | 1 session of about 30 minutes with a written charter |

* Test design uses EP, BVA, decision tables, state transition (pending to approved or denied) and error guessing  
* At least 30 designed test cases, at least 40% negative or boundary, each marked automated or manual  
* Tests are tagged `smoke`, `negative`, `boundary` and `integration`; shared cases run from `validation_cases.csv` in both suites  
* Cycle 1 runs all manual and cross-app cases; Cycle 2 reruns failed cases, the cross-app cases and both automated suites  
* AI-suggested test ideas are reviewed against the requirements and logged (at least 10 entries, at least 3 changed or rejected)  
* Defects go in Jira (at least 6, at least 2 from the peer bug bash); an RTM links requirement, test case, automated test and defect  
* Test effectiveness check: two deliberate bugs, one per app, in a scratch branch

## **6\. Entry Criteria**

Cycle 1 starts only when all of the following are true:

* Every requirement ID is listed in the RTM  
* At least 30 designed test cases are written and reviewed by the partner  
* Both apps build and start, and the database builds from `schema.sql` and `seed.sql` on an empty file  
* `EM_DB_PATH` is set and the README steps work on a clean clone  

## **7\. Exit Criteria**

Testing is complete when every item below is met.

* Every Must requirement has at least one designed test case and one automated test, and is in the RTM  
* All automated tests pass on a clean clone (a test failing because of an open defect may be skipped, with the Jira ID as the reason)  
* No open Critical or High defects  
* Two test cycles recorded  
* Minimums met: 30 designed cases, 20 unit tests per app, 5 and 4 integration tests, 5 SQL queries, 1 menu-flow test per app  
* At least 6 defects logged, at least 2 from the peer bug bash, one taken through new, fixed, retested, closed  
* Exploratory notes, AI review log, effectiveness check and test summary report (with go/no-go) completed

**Suspension:** smoke test fails, the database cannot be built from the scripts, or a Critical defect blocks login in either app.

**Resumption:** a new build passes smoke, the database is rebuilt, and the Critical defect is fixed and retested.

## **8\. Main Risks**

A mismatch between the two apps' data handling is the biggest threat (score 9); the rest score 4 to 6\. Score \= likelihood × impact, each rated 1 to 3\.

| Risk | Likelihood | Impact | Score | Mitigation |
| :---: | :---: | :---: | :---: | :---: |
| Apps disagree on shared data (rounding, date format, status case) | 3 | 3 | 9 | Cross-app cases XW-05 and XW-06, SQL validation queries, shared contract checks in both cycles |
| Learning JUnit and Mockito while building gives weak tests | 3 | 2 | 6 | Start with simple tests (Appendix B); partner reviews every pull request |
| Tests left until the end of the project | 3 | 2 | 6 | Write tests with each feature; follow the five milestones |
| AI-suggested tests have wrong expected results | 2 | 3 | 6 | Check each against the requirement and exact message; keep the review log |
| Date-based tests unstable (VR-04, 90 days) | 2 | 2 | 4 | Fixed `today` in Python, injected `Clock` in Java |
| Dirty or locked database between tests | 2 | 2 | 4 | Temporary database per test; busy timeout set |
| Work unbalanced between team members | 2 | 2 | 4 | Both members code and test in both languages |

## **9\. Smoke Test Subset**

Seven checks run on every new build, and the build is accepted or rejected within 15 to 30 minutes.

1. The database builds from `schema.sql` and `seed.sql` on an empty file (FR-S1)  
2. The Employee App starts and a valid employee sees `Welcome, <full name>` (FR-A1)  
3. The Manager App starts and a valid manager sees `Welcome, <full name>` (FR-A1)  
4. A wrong password shows `Invalid username or password.` (FR-A1)  
5. An employee submits a valid expense and gets the new ID (FR-E1)  
6. The manager lists pending expenses and sees that expense (FR-M1)  
7. The manager approves it and the employee sees status `approved` (XW-01, short path)

The `smoke`\-tagged PyTest and JUnit tests run alongside these checks. Any failure means the build is rejected and deeper testing stops.