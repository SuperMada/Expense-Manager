# Defect Log - Revature Expense Manager

## Defect Overview

| Defect ID | App               | Summary                                                                                    | Severity / Priority | Date Found | Status   | Fix Commit Reference                                                                                   |
| --------- | ----------------- | ------------------------------------------------------------------------------------------ | ------------------- | ---------- | -------- | ------------------------------------------------------------------------------------------------------ |
| BUG-01    | Employee (Python) | Welcome message prints `Welcome, <name>!` but FR-A1 specifies `Welcome, <full name>.` | Low / Low           | 2026-11-10 | Resolved | [e3b04e2](https://github.com/SuperMada/Expense-Manager/commit/e3b04e23e5e8431f6aa781fd4fb482ffcb31d0bf) |

## Defect details

### BUG-01: Welcome message trailing punctuation not in spec

- **Symptom:** On successful login, the Employee App prints `Welcome, Jane Doe!` (with a trailing exclamation mark). FR-A1 specifies `Welcome, <full name>` with no trailing punctuation of any kind.
- **Root cause:** `login_menu` hardcoded the punctuation: `print(f"Welcome, {user['full_name']}!")`. The `!` was added for style and never checked against FR-A1's exact string.
- **Source:** `employee_app/menu.py`
- **Fix:** commit [e3b04e2](https://github.com/SuperMada/Expense-Manager/commit/e3b04e23e5e8431f6aa781fd4fb482ffcb31d0bf):

  - `employee_app/menu.py` removed the trailing `!` so the line now prints `Welcome, Jane Doe` (no punctuation, per FR-A1).
