from datetime import date

import pytest

from employee_app import services
from employee_app.exceptions import AuthorizationError, InvalidStateError


@pytest.mark.integration
@pytest.mark.negative
def test_edit_other_user_expense(temp_db, emp_jane):
    """Covers: FR-A4, FR-E3, PTC-07"""
    with pytest.raises(AuthorizationError):
        services.update_expense(
            temp_db, emp_jane, 10, 50.00, 2, "hijacked", date.today().isoformat()
        )


@pytest.mark.integration
@pytest.mark.negative
def test_delete_other_user_expense(temp_db, emp_jane):
    """Covers: FR-A4, FR-E4, PTC-08"""
    with pytest.raises(AuthorizationError):
        services.delete_expense(temp_db, emp_jane, 10)


@pytest.mark.integration
@pytest.mark.smoke
def test_submit_expense(temp_db, emp_jane):
    """Covers: FR-E1, PTC-09"""
    new_expense_id = services.submit_expense(
        temp_db, emp_jane, 45.50, 2, "Lunch", date.today().isoformat()
    )

    # get the new expense and approval
    expense = temp_db.execute(
        "SELECT * FROM expenses WHERE id = ?", (new_expense_id,)
    ).fetchone()

    approval = temp_db.execute(
        "SELECT * FROM approvals WHERE expense_id = ?", (new_expense_id,)
    ).fetchone()

    assert expense["user_id"] == emp_jane["id"]
    assert float(expense["amount"]) == pytest.approx(45.50)
    assert approval["status"] == "pending"
    assert approval["reviewer_id"] is None
    assert approval["review_date"] is None


@pytest.mark.integration
def test_view_all_own_expenses(temp_db, emp_jane):
    """Covers: FR-E2, PTC-11"""
    rows = services.get_own_expenses(temp_db, emp_jane)

    # check that all expected expense IDs are present
    expected_ids = {1, 2, 3, 4, 5, 6, 7}
    actual_ids = set()
    for r in rows:
        actual_ids.add(r["id"])
    assert actual_ids == expected_ids

    # check date is sorted in descending order
    dates = []
    for r in rows:
        dates.append(r["expense_date"])
    assert dates == sorted(dates, reverse=True)


@pytest.mark.integration
def test_view_pending_filter(temp_db, emp_jane):
    """Covers: FR-E2, PTC-12"""
    rows = services.get_own_expenses(temp_db, emp_jane, "pending")
    expected_ids = {1, 4, 6}
    actual_ids = set()
    for r in rows:
        actual_ids.add(r["id"])

    assert actual_ids == expected_ids
    for r in rows:
        assert r["status"] == "pending"


@pytest.mark.integration
@pytest.mark.negative
def test_edit_non_pending(temp_db, emp_jane):
    """Covers: FR-E3, PTC-15"""
    with pytest.raises(InvalidStateError):
        services.update_expense(
            temp_db, emp_jane, 2, 99.99, 2, "something", date.today().isoformat()
        )


@pytest.mark.integration
@pytest.mark.smoke
def test_delete_with_confirmation(temp_db, emp_jane):
    """Covers: FR-E4, PTC-16"""
    services.delete_expense(temp_db, emp_jane, 1)

    assert temp_db.execute(
        "SELECT * FROM expenses WHERE id = ?", (1,)
    ).fetchone() is None

    assert temp_db.execute(
        "SELECT * FROM approvals WHERE expense_id = ?", (1,)
    ).fetchone() is None


@pytest.mark.integration
@pytest.mark.negative
def test_delete_non_pending(temp_db, emp_jane):
    """Covers: FR-E4, PTC-18"""
    with pytest.raises(InvalidStateError):
        services.delete_expense(temp_db, emp_jane, 2)


@pytest.mark.integration
def test_review_history(temp_db, emp_jane):
    """Covers: FR-E5, PTC-19"""
    rows = services.get_review_history(temp_db, emp_jane)
    expected_ids = {2, 3, 5, 7}
    actual_ids = set()
    for r in rows:
        actual_ids.add(r["id"])
    assert actual_ids == expected_ids

    # check id 7 has no comment and from gabe newell
    for r in rows:
        if r["id"] == 7:
            assert r["comment"] is None
            assert r["reviewer"] == "Gabe Newell"


@pytest.mark.integration
def test_review_history_excludes_pending(temp_db, emp_jane):
    """Covers: FR-E5, PTC-20"""
    rows = services.get_review_history(temp_db, emp_jane)
    pending_ids = {1, 4, 6}
    for r in rows:
        assert r["status"] in {"approved", "denied"}
        assert r["id"] not in pending_ids
