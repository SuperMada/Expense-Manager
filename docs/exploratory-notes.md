# Exploratory Testing

## Session 1 - Employee App

**Charter:** Explore the submit flow with values near VR-01 to VR-03 boundaries to find off-by-one or rounding issues.
**Tester:** Md Tanjeem Haider
**Date:** 2026-11-09
**Duration:** 30 min
**Environment:** Employee App on windows 11, DB seeded from sql/schema.sql + sql/seed.sql

### What I tried on Team 2's employee app

#### VR-01 Amount

| Probe                     | Input    | Observed | Assessment |
| ------------------------- | -------- | -------- | ---------- |
| Minimum valid             | 0.01     | Allowed  | OK         |
| Trailing zero on decimals | 0.010    | Denied   | OK         |
| Maximum valid             | 10000.00 | Allowed  | OK         |
| Trailing dot              | 45.      | Denied   | OK         |
| Leading dot               | .5       | Denied   | OK         |
| Leading zeros             | 00.01    | Allowed  | OK         |
| Number with string        | +45      | Denied   | OK         |
| Scientific notation       | 1e2      | Denied   | OK         |
| Three decimals            | 12.345   | Denied   | OK         |

#### VR-02 Description

| Probe                         | Input                            | Observed | Assessment            |
| ----------------------------- | -------------------------------- | -------- | --------------------- |
| Exactly 200 chars             | "a" 200 times                   | Allowed  | OK                    |
| Exactly 201 chars             | "a" 201 times                   | Denied   | OK                    |
| 200 chars + 5 trailing spaces | "a" 200 times then " " 5 times | Allowed  | Should not be allowed |

#### VR-03 Category

| Probe              | Input | Observed | Assessment       |
| ------------------ | ----- | -------- | ---------------- |
| Leading zero       | 01    | Allowed  | should be denied |
| Positive sign      | +3    | Denied   | OK               |
| Float format       | 3.0   | Denied   | OK               |
| Padded with spaces | " 3 " | allowed  | OK               |

### What I found on Team 2's employee app

**Bugs:**

- BUG-001: Description does not count spaces towards character limit.
  - Severity: Med
- BUG-002: Category accepts values with leading zeros like "01" but it should reject it.
  - Severity: Low

### What Team 2 tested on ours

**Charter:** Explore the login and submit flow with values near VR-01 to VR-04 boundaries to find off-by-one or rounding issues.
**Tester:** Divyan Varatharaj
**Date:** 2026-11-09
**Duration:** 45 min
**Environment:** Employee App on windows 11, DB seeded from sql/schema.sql + sql/seed.sql

#### Login

| Probe                               | Input                                   | Observed | Assessment |
| ----------------------------------- | --------------------------------------- | -------- | ---------- |
| Enter incorrect input >3 times      | "a" as username and password            | Denied   | OK         |
| Enter empty username/password       | Pressed enter                           | Allowed  | OK         |
| Enter space(s) as username/password | "    "                                  | Allowed  | OK         |
| Enter correct details               | Username: emp_peter Password: Passw0rd! | Allowed  | OK         |

#### Amount

| Probe                | Input    | Observed | Assessment |
| -------------------- | -------- | -------- | ---------- |
| Minimum valid        | 0.01     | Allowed  | OK         |
| More than 2 decimals | 0.111    | Denied   | OK         |
| Maximum valid        | 10000.00 | Allowed  | OK         |
| Trailing dot         | 32.      | Allowed  | OK         |
| Leading dot          | .32      | Allowed  | OK         |
| Leading zeros        | 00.01    | Allowed  | OK         |
| Number with string   | +72      | Denied   | OK         |
| Scientific notation  | 1e3      | Denied   | OK         |

#### Description

| Probe                                           | Input                                        | Observed | Assessment |
| ----------------------------------------------- | -------------------------------------------- | -------- | ---------- |
| Exactly 200 chars                               | "a" 200 times                               | Allowed  | OK         |
| Exactly 201 chars                               | "a" 201 times                               | Denied   | OK         |
| 200+ chars with trailing spaces must be trimmed | "a" 200 times then " " 5 times             | Allowed  | OK         |
| 200+ chars with spaces in between               | "a" 100 times + " " 3 times + "a" 100 times | Denied   | OK         |

#### Category

| Probe              | Input | Observed | Assessment |
| ------------------ | ----- | -------- | ---------- |
| Leading zero       | 03    | Denied   | OK         |
| Positive sign      | +1    | Denied   | OK         |
| Float format       | 1.0   | Denied   | OK         |
| Padded with spaces | " 1 " | Allowed  | OK         |

#### Date

| Probe                  | Input      | Observed | Assessment |
| ---------------------- | ---------- | -------- | ---------- |
| Not in date format     | ytr        | Denied   | OK         |
| Future date            | 2027-01-01 | Denied   | OK         |
| 1 day in the future    | 2026-11-10 | Denied   | OK         |
| Same day               | 2026-11-09 | Allowed  | OK         |
| Date from 90+ days ago | 2026-04-01 | Denied   | OK         |
| Date from 91+ days ago | 2026-04-01 | Denied   | OK         |

### What team 2 found on ours

**Bugs:**

- NO BUGS
