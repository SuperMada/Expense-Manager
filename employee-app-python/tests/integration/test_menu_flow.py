import logging

import pytest
import sqlite3 as sql
from contextlib import contextmanager
from unittest.mock import patch

from employee_app import menu

@contextmanager
def _mock_io(inputs):
    """ Patch built-in input() and getpass.getpass() with provided inputs."""
    input_iter = iter(inputs)
    with (
        patch("builtins.input", lambda _: next(input_iter)),
        patch("getpass.getpass", lambda _: next(input_iter))
    ):
        yield

@pytest.mark.integration
@pytest.mark.smoke
def test_valid_employee_login(menu_environment, capsys):
    """Covers: FR-A1, PTC-01"""
    with _mock_io(["emp_jane", "Passw0rd!"]):
        user = menu.login_menu(expected_role="employee")
        output = capsys.readouterr().out
        assert "Welcome, Jane Doe" in output
        assert user is not None


@pytest.mark.integration
@pytest.mark.negative
def test_login_invalid_username(menu_environment, capsys):
    """Covers: FR-A1, PTC-02"""
    with _mock_io(["nobody", "Passw0rd!", "emp_jane", "Passw0rd!"]):
        menu.login_menu(expected_role="employee")

    output = capsys.readouterr().out
    assert output.count("Invalid username or password.") == 1


@pytest.mark.integration
@pytest.mark.negative
def test_login_wrong_password(menu_environment, capsys):
    """Covers: FR-A1, PTC-03"""
    with _mock_io(["emp_jane", "wrongpass", "emp_jane", "Passw0rd!"]):
        menu.login_menu(expected_role="employee")

    output = capsys.readouterr().out
    assert output.count("Invalid username or password.") == 1


@pytest.mark.integration
@pytest.mark.negative
def test_login_empty_input(menu_environment, capsys):
    """Covers: FR-A1, PTC-04"""
    mock_inputs = [
        "", "", "",           # 3 blank usernames
        "emp_jane",
        "", "", "",           # 3 blank passwords
        "Passw0rd!",
    ]
    with _mock_io(mock_inputs):
        user = menu.login_menu(expected_role="employee")

    output = capsys.readouterr().out
    assert "Invalid username or password." not in output
    assert user["full_name"] == "Jane Doe"


@pytest.mark.integration
@pytest.mark.negative
def test_login_wrong_role(menu_environment, capsys):
    """Covers: FR-A2, PTC-05"""
    with _mock_io(["mgr_john", "Passw0rd!"]):
        with pytest.raises(SystemExit) as e:
            menu.login_menu(expected_role="employee")

    assert e.value.code == 1
    output = capsys.readouterr().out
    assert "This account does not have access to this app." in output


@pytest.mark.integration
@pytest.mark.negative
@pytest.mark.boundary
def test_login_three_strikes(menu_environment, capsys):
    """Covers: FR-A3, PTC-06"""
    incorrect_login_data = [
        "emp_jane", "wrongpass",
        "emp_jane", "wrongpass",
        "emp_jane", "wrongpass"
    ] 

    with pytest.raises(SystemExit) as e:
        with _mock_io(incorrect_login_data):
            menu.login_menu(expected_role="employee")

    assert e.value.code == 1
    output = capsys.readouterr().out
    assert "Too many failed attempts." in output


@pytest.mark.integration
@pytest.mark.smoke
def test_menu_submit_invalid_amount_reprompts(menu_environment, capsys):
    """Covers: FR-E1, PTC-10"""
    mock_data = [
        "emp_jane", "Passw0rd!", # login as jane
        "1",                # submit expense
        "0",                # invalid amount
        "45.50",            # valid retry
        "2",                # category
        "Lunch",            # description
        "",                 # blank date -> today
        "0",                # exit menu
    ]
    with _mock_io(mock_data):
        user = menu.login_menu(expected_role="employee")
        menu.employee_menu(user)
    output = capsys.readouterr().out
    assert output.count("Amount must be greater than 0") == 1
    assert "Expense submitted. ID:" in output


@pytest.mark.integration
def test_view_empty_result(menu_environment, capsys):
    """Covers: FR-E2, PTC-13"""
    # Delete all expenses and approvals for emp_jane (user id = 3)
    conn = sql.connect(menu_environment)
    conn.execute(
        "DELETE FROM approvals WHERE expense_id IN "
        "(SELECT id FROM expenses WHERE user_id = 3)"
    )
    conn.execute(
        "DELETE FROM expenses WHERE user_id = 3"
    )
    conn.commit()
    conn.close()

    with _mock_io(["emp_jane", "Passw0rd!", "2", "1", "0"]):
        user = menu.login_menu(expected_role="employee")
        menu.employee_menu(user)

    output = capsys.readouterr().out
    assert "No expenses found." in output

@pytest.mark.integration
def test_edit_keep_defaults(menu_environment, capsys):
    """Covers: FR-E3, PTC-14"""
    conn = sql.connect(menu_environment)
    conn.row_factory = sql.Row
    before_edit = conn.execute(
        "SELECT * FROM expenses WHERE id = 1"
    ).fetchone()
    conn.close()

    mock_data = [
        "emp_jane", "Passw0rd!", # login as jane
        "3", "1",        # edit expense with ID 1
        "", "", "", "",  # keep all defaults
        "0"              # exit menu
    ]

    with _mock_io(mock_data):
        user = menu.login_menu(expected_role="employee")
        menu.employee_menu(user)

    conn = sql.connect(menu_environment)
    conn.row_factory = sql.Row
    after_edit = conn.execute(
        "SELECT * FROM expenses WHERE id = 1"
    ).fetchone()
    conn.close()

    assert before_edit == after_edit
        
        
@pytest.mark.integration
@pytest.mark.negative
def test_delete_cancelled(menu_environment, capsys):
    """Covers: FR-E4, PTC-17"""
    mock_data = [
        "emp_jane", "Passw0rd!", # login as jane
        "4", "1",        # attempt to delete expense with ID 1
        "n",             # cancel deletion
        "0"              # exit menu
    ]
    with _mock_io(mock_data):
        user = menu.login_menu(expected_role="employee")
        menu.employee_menu(user)

    conn = sql.connect(menu_environment)
    row = conn.execute(
        "SELECT * FROM expenses WHERE id = 1"
    ).fetchone()
    conn.close()

    assert row is not None


@pytest.mark.integration
def test_login_writes_log_no_password(menu_environment, caplog):
    """Covers: FR-S3, PTC-21"""
    caplog.set_level(logging.INFO, logger="employee_app")
    with _mock_io(["emp_jane", "Passw0rd!"]):
        menu.login_menu(expected_role="employee")

    caplog_txt = caplog.text

    assert "INFO" in caplog_txt
    assert "Login successful" in caplog_txt
    assert "Passw0rd!" not in caplog_txt


@pytest.mark.integration
@pytest.mark.negative
def test_login_empty_input_not_counted(menu_environment, capsys):
    """Covers: FR-A1, FR-A3, PTC-43."""
    mock_data = [
        "", "", "", "", "",     # 5x blank username
        "emp_jane",
        "", "", "", "", "",     # 5x blank password
        "wrongpass",
        "emp_jane", "wrongpass",
        "emp_jane", "wrongpass" # 3rd attempt -> exit
    ]

    with _mock_io(mock_data):
        with pytest.raises(SystemExit) as e:
            menu.login_menu(expected_role="employee")

    assert e.value.code == 1
    output = capsys.readouterr().out
    assert "Too many failed attempts." in output
    assert output.count("Invalid username or password.") == 3


@pytest.mark.integration
def test_logging_successful_operations(menu_environment, caplog):
    """Covers: FR-S3, PTC-44"""
    caplog.set_level(logging.INFO, logger="employee_app")
    mock_data = [
        "emp_jane", "Passw0rd!",
        "1", "45.50", "2", "Lunch", "",    # submit
        "3", "1", "", "", "", "",          # edit id 1
        "4", "1", "y",                     # delete id 1
        "0",
    ]

    with _mock_io(mock_data):
        user = menu.login_menu(expected_role="employee")
        menu.employee_menu(user)

    caplog_txt = caplog.text
    assert "Expense submitted" in caplog_txt
    assert "Expense updated" in caplog_txt
    assert "Expense deleted" in caplog_txt