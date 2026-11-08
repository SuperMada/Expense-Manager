def test_expenses_with_no_matching_user(temp_db):
    """Expenses with no matching user."""
    rows = temp_db.execute(
        "SELECT e.id "
        "FROM expenses e "
        "LEFT JOIN users u ON e.user_id = u.id "
        "WHERE u.id IS NULL"
    ).fetchall()
    assert rows == []

def test_expenses_with_no_approval_row(temp_db):
    """Expenses with no approval row."""
    rows = temp_db.execute(
        "SELECT e.id "
        "FROM expenses e "
        "LEFT JOIN approvals a ON e.id = a.expense_id "
        "WHERE a.id IS NULL"
    ).fetchall()
    assert rows == []

def test_pending_approvals_with_reviewer_or_review_date(temp_db):
    """pending approvals that have a reviewer or review date."""
    rows = temp_db.execute(
        "SELECT a.id "
        "FROM approvals a "
        "WHERE a.status = 'pending' "
        "AND (a.reviewer_id IS NOT NULL OR a.review_date IS NOT NULL)"
    ).fetchall()
    assert rows == []

def test_decided_approvals_missing_reviewer_or_review_date_or_reviewed_by_non_manager(temp_db):
    """approved/denied approvals missing a reviewer or review date, or reviewed by a non-manager."""
    rows = temp_db.execute(
        "SELECT a.id "
        "FROM approvals a "
        "LEFT JOIN users u ON a.reviewer_id = u.id "
        "WHERE a.status IN ('approved', 'denied') "
        "AND (a.reviewer_id IS NULL "
        "     OR a.review_date IS NULL "
        "     OR u.id IS NULL "
        "     OR u.role <> 'manager')"
    ).fetchall()
    assert rows == []

def test_status_or_review_date_breaks_contract(temp_db):
    """Approvals whose status or review_date breaks the shared data contract (8.3)."""
    rows = temp_db.execute(
        "SELECT a.id "
        "FROM approvals a "
        "WHERE a.status NOT IN ('pending', 'approved', 'denied') "
        "   OR (a.review_date IS NOT NULL "
        "       AND a.review_date NOT GLOB '[0-9][0-9][0-9][0-9]-[0-9][0-9]-[0-9][0-9]')"
    ).fetchall()
    assert rows == []