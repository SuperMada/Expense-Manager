# AI Test Review Log

Tool used: Claude

Prompt: Given the project description markdown and the data from the database can you write test cases we may have missed in test-cases.md. Try to focus on any obvious smoke tests we may have missed to test the default functionality as well as any edge cases we may have missed.

Files shared: project-requirements.md, sql/schema.sql, sql/seed.sql, docs/test-cases.md

Date: 2026-11-09

| # | Suggestion                                                                                            | Requirement  | Decision | Reason                                                                                                                   | Resulting case |
| - | ----------------------------------------------------------------------------------------------------- | ------------ | -------- | ------------------------------------------------------------------------------------------------------------------------ | -------------- |
| 1 | Test that empty username/password submissions re-prompt and do not increment the failed-login counter | FR-A1, FR-A3 | Rejected | Duplicate of PTC-04                                                                                                      | N/A            |
| 2 | Test that a successful submit, edit, and delete each write a log entry with time, level, and message  | FR-S3        | Kept     | FR-S3 requires logging of submits/edits/deletes; PTC-21 only covers login                                                | PTC-44         |
| 3 | Test that passwords are never written to the log file or printed during login                         | FR-S3        | Rejected | Duplicate of PTC-21                                                                                                      | N/A            |
| 4 | Test that a wrong-role login (`mgr_john`) counts as a failed attempt toward the 3-strike limit      | FR-A2, FR-A3 | Rejected | Not observable. FR-A2 says "the app exits" after the role message, so no second attempt exists for the counter to affect | N/A            |
| 5 | Test string with only spaces on comment to ensure input is stripped                                        | VR-05, FR-M2 | Kept   | Good edge case test for denial which needs a comment with actuall input | JTC-06 |
| 6 | Test invalid login after blank inputs | FR-A1, FR-A3 | Rejected | Retests functionality that is already seperatly tests | N/A
| 7 | Test closed connection throws SQLException which gets caught and throws DatabaseException | FR-S4 | Rejected | Tests already existed to check SQLException catching, adding a real unmocked one just retests the same functionality | N/A |
| 8 | Test foreign key enforcement | FR-S5 | Rejected | SQL functionality not Java and code never allows a write attempt that would violate the rule | N/A |
| 9 | Test menu option 2 with a non positive integer | VR-08, FR-M2 | Rejected | Duplicate of JTC-46 | N/A |
| 10 | Test user service throws authorization exception | FR-M3 | Rejected | The exception functionality is tested in other services & the return is tested in the Dao unit test | N/A |

## Summary

- Total suggestions: 10
- Kept: 2
- Rejected: 8

## Findings

A majority of the test cases returned from AI were already implemented as we prompted after finishing writing the tests ourselves. The AI was able to find a couple missed test cases but mostly gave us tests that we already wrote exactly or tested the same functionality as other tests.
