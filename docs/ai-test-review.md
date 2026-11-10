# AI Test Review Log

Tool used: Claude
Files shared: project-requirements.md, sql/schema.sql, sql/seed.sql, docs/test-cases.md
Date: 2026-11-09

| # | Suggestion                                                                                            | Requirement  | Decision | Reason                                                                                                                   | Resulting case |
| - | ----------------------------------------------------------------------------------------------------- | ------------ | -------- | ------------------------------------------------------------------------------------------------------------------------ | -------------- |
| 1 | Test that empty username/password submissions re-prompt and do not increment the failed-login counter | FR-A1, FR-A3 | Rejected | Duplicate of PTC-04                                                                                                      | N/A            |
| 2 | Test that a successful submit, edit, and delete each write a log entry with time, level, and message  | FR-S3        | Kept     | FR-S3 requires logging of submits/edits/deletes; PTC-21 only covers login                                                | PTC-44         |
| 3 | Test that passwords are never written to the log file or printed during login                         | FR-S3        | Kept     | FR-S3 specifies "Passwords are never logged or printed"                                                                  | PTC-45         |
| 4 | Test that a wrong-role login (`mgr_john`) counts as a failed attempt toward the 3-strike limit      | FR-A2, FR-A3 | Rejected | Not observable. FR-A2 says "the app exits" after the role message, so no second attempt exists for the counter to affect | N/A            |

## Summary

- Total suggestions: 4
- Kept: 2
- Rejected: 2
