# Test Restoration Plan - manager-app-java

## Executive Summary

Critical testing issues in manager-app-java require immediate restoration and improvement. The plan addresses:

- **IMMEDIATE RESTORATION**: Recreate deleted test_Services.java
- **API COMPATIBILITY FIXES**: Update ServicesTest.java to match current implementation
- **NEW TEST COVERAGE**: Create MenuTest.java from scratch
- **INFRASTRUCTURE IMPROVEMENTS**: Complete test setup and add missing functionality

## Phase 1: Immediate Restoration (Week 1)

### 1.1 Restore test_Services.java (High Priority - CRITICAL)

**Source Files**: Use git history to reconstruct lost tests
- Primary source: `git show 31962b2:manager-app-java/src/test/java/test_Services.java`
- Secondary source: `git show 79e5cef:manager-app-java/src/test/java/test_Services.java`
- Combine with current ServicesTest.java to create complete test suite

**Files to Create/Modify**:
- `src/test/java/test_Services.java` - Recreate from git history
- `src/test/java/ServicesTest.java` - Update to match current API
- `src/test/java/MenuTest.java` - Create from scratch

**Specific Tests to Restore**:

#### From commit 31962b2 (Full Test Suite):
```java
// Role requirement tests (LOST - MUST RESTORE)
@Test void test_role_required_matching_role_works()
@Test void test_get_role_works_manager() 
@Test void test_get_role_works_employee()

// Employee report tests (LOST - CRITICAL FUNCTIONALITY)
@Test void test_get_employee_report_works()

// Category report tests (LOST - CRITICAL FUNCTIONALITY)  
@Test void test_get_category_report_works()

// Additional validation tests (LOST)
```

#### From commit 79e5cef (Basic Tests):
```java
// Basic authentication tests (PRESERVED)
@Test void test_manager_login_works()
@Test void test_get_name_from_id_works()
@Test void test_get_expenses_option_1_orders_by_date_works()
@Test void test_get_expenses_option_2_orders_by_amount_works()
@Test void test_get_category_works()
@Test void test_is_pending_report_pending_works()
@Test void test_is_pending_report_already_approved_works()
@Test void test_is_pending_report_already_denied_works()
@Test void test_is_pending_report_not_found_works()
@Test void test_update_approval_with_comment_works()
@Test void test_update_approval_without_comment_works()
```

### 1.2 Update ServicesTest.java for API Compatibility (High Priority - CRITICAL)

**Current Issues in ServicesTest.java**:

```java
// ❌ INCORRECT: Current getEmployeeReport(conn, user_id), test calls with 1 param
List<List<Object>> report = Services.getEmployeeReport(conn, 1);

// ❌ INCORRECT: Current getCategoryReport(conn, category_id), test calls with 1 param  
List<List<Object>> report = Services.getCategoryReport(conn, 1);

// ✅ CORRECT: Already matches current API
List<List<Object>> results = Services.getExpensesFromID(conn, 1, 1);
```

**Required Changes**:

1. **Fix getEmployeeReport test calls**: Change `Services.getEmployeeReport(conn, 1)` to `Services.getEmployeeReport(conn, 1)`

2. **Fix getCategoryReport test calls**: Change `Services.getCategoryReport(conn, 1)` to `Services.getCategoryReport(conn, 1)`

3. **Update test imports**: Remove unnecessary `SQLException` imports

4. **Complete db_default_mock()**: Implement full mock setup for existing tests

### 1.3 Create MenuTest.java from Scratch (High Priority - CRITICAL)

**Test Coverage Strategy**: Based on Menu.java functionality

#### Test 1: Login Menu Tests
```java
@Test void test_valid_login() 
@Test void test_invalid_username_password()
@Test void test_too_many_failed_attempts()
@Test void test_empty_credentials_rejected()
```

#### Test 2: Input Validation Tests
```java
@Test void test_charInput_valid_options()
@Test void test_charInput_invalid_option_rejected()
@Test void test_integerInput_valid_integer()
@Test void test_integerInput_invalid_input_rejected()
```

#### Test 3: Menu Navigation Tests
```java
@Test void test_main_menu_options()
@Test void test_expenses_list_sorting()
@Test void test_reports_employee_selection()
@Test void test_reports_category_selection()
```

#### Test 4: Exception Handling Tests
```java
@Test void test_authentication_exception_handling()
@Test void test_authorization_exception_handling()
@Test void test_database_exception_handling()
```

## Phase 2: Infrastructure Improvements (Weeks 2-3)

### 2.1 Complete Test Infrastructure

**db_default_mock() Implementation**:
```java
void db_default_mock() throws SQLException {
    when(conn.prepareStatement(anyString())).thenReturn(ps);
    when(ps.executeQuery()).thenReturn(rs);
    when(rs.next()).thenReturn(true);
}
```

**Mock Setup Standardization**:
- Create common test setup in `@BeforeEach`
- Implement consistent mock configurations
- Add test data initialization

### 2.2 Add Negative Test Coverage (HIGH)

**Missing Negative Tests**:
- `test_manager_login_stops_invalid_username_password_input` (exists)
- `test_manager_login_on_db_error` (exists)
- `test_manager_get_name_on_db_error` (exists)
- `test_manager_get_expenses_on_db_error` (exists)

**Additional Negative Tests Needed**:
- `test_login_invalid_credentials_repeated()`
- `test_expenses_database_error_handling()`
- `test_reports_permission_denied()`
- `test_review_expenses_invalid_id()`

### 2.3 Implement Parameterized Tests

**Use @CsvSource and @ValueSource for parameterized testing**:
- Login credential variations
- Input validation edge cases
- Menu navigation scenarios

## Phase 3: Quality Assurance (Week 4)

### 3.1 Test Validation

**Test Execution Strategy**:
1. Compile all test sources
2. Run test suite with Maven
3. Identify and fix compilation errors
4. Verify all tests pass
5. Add test coverage reports

**Quality Gates**:
- ✅ All tests compile successfully
- ✅ All existing tests pass
- ✅ Restored tests pass
- ✅ New tests add value
- ✅ No test duplication

### 3.2 Documentation Updates

**Update Test Documentation**:
- Add comprehensive test class documentation
- Document test assumptions and dependencies
- Create test coverage reports
- Update project README with test status

## Phase 4: Validation (Ongoing)

### 4.1 Continuous Integration

**Test Execution Requirements**:
- [ ] All tests pass locally
- [ ] Tests run without external dependencies
- [ ] Test setup is reproducible
- [ ] Test isolation is maintained

### 4.2 Performance Considerations

**Test Optimization**:
- [ ] Mock setup is efficient
- [ ] Tests don't interfere with each other
- [ ] Test execution time is reasonable
- [ ] Memory usage is controlled

## Resource Requirements

### Human Resources
- **Developer**: 1-2 FTE for full restoration (Week 1-2)
- **Tester**: QA resources for validation (Week 4)

### Technical Resources
- **IDE**: IntelliJ IDEA or Eclipse with JUnit 5 support
- **Build Tool**: Maven 3.10+ with JUnit 5 plugin
- **Testing Framework**: JUnit 5, Mockito

### Time Allocation
- **Week 1**: Immediate restoration (80% effort)
- **Week 2**: API compatibility fixes (20% effort) + infrastructure setup
- **Week 3**: Negative test coverage + parameterized tests
- **Week 4**: Validation and quality assurance

## Risk Assessment

### High Risks (Mitigation Required):
1. **Test Restoral Complexity**: Risk of incorrect test reconstruction
   - **Mitigation**: Cross-reference with git history and current implementation

2. **API Incompatibility**: Tests may fail against current code
   - **Mitigation**: Verify each test against current Services.java

3. **Test Gap Analysis**: Incomplete restoration of lost functionality
   - **Mitigation**: Prioritize critical functionality restoration

### Medium Risks (Monitoring Required):
1. **Test Maintenance**: Ongoing test upkeep
   - **Mitigation**: Implement test documentation standards

2. **Test Coverage Gaps**: Areas without sufficient test coverage
   - **Mitigation**: Continuous coverage monitoring

### Low Risks (Acceptable):
1. **Test Performance**: Minor performance issues
   - **Mitigation**: Accept for functionality-critical fixes

## Success Metrics

### Immediate Success (Week 1):
- ✅ test_Services.java restored with >90% original functionality
- ✅ ServicesTest.java API compatibility fixed
- ✅ MenuTest.java created with basic test coverage
- ✅ All tests compile successfully

### Short-term Success (Week 2-3):
- ✅ Test infrastructure complete
- ✅ Negative test coverage added
- ✅ Parameterized tests implemented
- ✅ Test documentation updated

### Long-term Success (Week 4+):
- ✅ All tests pass consistently
- ✅ Test coverage meets project standards
- ✅ Test execution time is acceptable
- ✅ Test maintenance is sustainable

## Implementation Timeline

| Week | Phase | Primary Goal | Success Criteria |
|------|-------|-------------|------------------|
| Week 1 | Phase 1 | Immediate Restoration | test_Services.java complete |
| Week 2 | Phase 2 | Infrastructure | db_default_mock() complete |
| Week 3 | Phase 2 | Quality | Negative tests added |
| Week 4 | Phase 3 | Validation | All tests pass |

## Conclusion

This comprehensive restoration plan addresses the critical testing issues in manager-app-java:

1. **Immediate**: Restore lost test coverage and fix API incompatibilities
2. **Short-term**: Complete test infrastructure and add comprehensive coverage  
3. **Long-term**: Establish sustainable testing practices

The plan prioritizes functionality restoration while maintaining code quality standards. Critical test losses are recovered first, followed by systematic improvements to prevent similar issues in the future.