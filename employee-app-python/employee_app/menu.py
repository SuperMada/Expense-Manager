import sqlite3 as sql
import sys
import getpass
from datetime import date
from employee_app import services, validators
from employee_app.exceptions import AuthenticationError, AuthorizationError, ValidationError, ExpenseManagerError, DatabaseError
from employee_app import database
from employee_app import logger

log = logger.get_logger()

MENU = """
----- Employee Expense Manager -----
1. Submit expense
2. View my expenses
3. Edit pending expense
4. Delete pending expense
5. Review history
0. Exit"""

MENU_VIEW_EXPENSES = """
----- Filter Expenses by Status -----
1. All
2. Pending
3. Approved
4. Denied
"""

def _read_input(prompt: str, mask=False):
    while True:
        if mask:
            value = getpass.getpass(prompt).strip()
        else:
            value = input(prompt).strip()
        if value:
            return value

def _prompt_until_valid(prompt: str, validate: callable, default=None):
    while True:
        try:
            value = input(prompt)
            # If a default value is provided and the user enters nothing, use the default
            if default is not None and not value:
                value = default
            return validate(value)
        except ValidationError as e:
            log.warning("Validation error (retrying): %s", e)
            print(e)


def _handle_operation_error(operation: str, error: ExpenseManagerError, user_id):
    log.warning("%s failed for user_id = %s: %s", operation, user_id, error)
    if isinstance(error, DatabaseError):
        print("Unexpected error. See log.")
    else:
        print(f"Failed to {operation}: {error}")


def login_menu(expected_role: str):
    attempts = 0
    while True:
        username = _read_input("Username: ")
        password = _read_input("Password: ", mask=True)
        try:
            with database.get_connection() as conn:
                user = services.authenticate_user(conn, username, password)
        except AuthenticationError:
            attempts += 1
            log.warning("Failed login attempt for username: %s (attempt %d/3)", username, attempts)
            print("Invalid username or password.")
            if attempts >= 3:
                log.error("Too many failed login attempts, closing application.")
                print("Too many failed attempts.")
                sys.exit(1)
            continue

        try:
            services.role_required(user, expected_role)
        except AuthorizationError:
            log.error(
                "Login denied (wrong role): user: %s's role is %s, expected role %s",
                user['username'], user['role'], expected_role
            )
            print("This account does not have access to this app.")
            sys.exit(1)

        log.info("Login successful for user: %s", user['username'])
        print()
        print(f"Welcome, {user['full_name']}!")
        return user

def submit_expense_menu(user: sql.Row):
    print("Submit Expense")

    amount = _prompt_until_valid("Amount: ", validators.validate_amount)
    print("Available categories:")
    for id, name in services.get_categories():
        print(f"{id}. {name}")
    category = _prompt_until_valid("Category: ", validators.validate_category)
    description = _prompt_until_valid("Description: ", validators.validate_description)
    expense_date = _prompt_until_valid(
        "Date (YYYY-MM-DD) (Enter for today): ",
        validators.validate_expense_date,
        default=date.today().isoformat()
    )

    try:
        with database.get_connection() as conn:
            expense_id = services.submit_expense(conn, user, amount, category, description, expense_date)
    except ExpenseManagerError as e:
        _handle_operation_error("submit expense", e, user['id'])
        return

    log.info(
        "Expense submitted for '%s': user_id= %s, expense_id= %s",
        user['username'], user['id'], expense_id
    )
    print(f"Expense submitted. ID: {expense_id}")

def view_expenses_menu(user: sql.Row):
    print(MENU_VIEW_EXPENSES)
    while True:
        choice = input("Select a status: ").strip()
        if choice == "1":
            status = None
        elif choice == "2":
            status = "pending"
        elif choice == "3":
            status = "approved"
        elif choice == "4":
            status = "denied"
        else:
            print("Invalid option.")
            continue
        break

    try:
        with database.get_connection() as conn:
            rows = services.get_own_expenses(conn, user, status)
    except ExpenseManagerError as e:
        _handle_operation_error("view expenses", e, user['id'])
        return
    
    if not rows:
        print("No expenses found.")
        return

    header = f"{'ID':<5} | {'Date':<12} | {'Category':<16} | {'Amount':<11} | {'Status':<10}"
    print(header)
    print("-" * len(header))
    for row in rows:
        print(f"{row['id']:<5} | {row['expense_date']:<12} | {row['category']:<16} | ${row['amount']:<10.2f} | {row['status']:<10}")


def edit_expense_menu(user: sql.Row):
    print("Edit Expense")
    expense_id = _prompt_until_valid("Expense ID: ", validators.validate_expense_id)

    try:
        with database.get_connection() as conn:
            expense = services.get_own_pending_expense(conn, user, expense_id)
    except ExpenseManagerError as e:
        _handle_operation_error("retrieve pending expense", e, user['id'])
        return

    # Print which expense is being edited
    print(f"Current: amount= {expense['amount']}, category_id= {expense['category_id']}, description= {expense['description']!r}, date= {expense['expense_date']}")

    amount = _prompt_until_valid(
        "New amount (Enter to keep current): ",
        validators.validate_amount,
        default=str(expense['amount'])
    )
    category = _prompt_until_valid(
        "New category ID (Enter to keep current): ",
        validators.validate_category,
        default=str(expense['category_id'])
    )
    description = _prompt_until_valid(
        "New description (Enter to keep current): ",
        validators.validate_description,
        default=expense['description']
    )
    expense_date = _prompt_until_valid(
        "New expense date (YYYY-MM-DD) (Enter to keep current): ",
        validators.validate_expense_date,
        default=expense['expense_date']
    )

    try:
        with database.get_connection() as conn:
            services.update_expense(conn, user, expense_id, amount, category, description, expense_date)
    except ExpenseManagerError as e:
        _handle_operation_error("update expense", e, user['id'])
        return

    log.info(
        "Expense updated: user_id=%s, expense_id=%s, amount=%s, category_id=%s, description=%s, expense_date=%s",
        user['id'], expense_id, amount, category, description, expense_date
    )
    print(f"Expense {expense_id} updated")


def delete_expense_menu(user: sql.Row):
    print("Delete Pending Expense")
    expense_id = _prompt_until_valid("Expense ID: ", validators.validate_expense_id)

    try:
        with database.get_connection() as conn:
            expense = services.get_own_pending_expense(conn, user, expense_id)
    except ExpenseManagerError as e:
        _handle_operation_error("retrieve pending expense", e, user['id'])
        return

    print(
        f"About to delete expense: ID={expense_id}, "
        f"amount={expense['amount']}, "
        f"category_id={expense['category_id']}, "
        f"description={expense['description']!r}, "
        f"date={expense['expense_date']}"
    )
    confirm = input("Confirm delete? (y/Y to confirm, any other key to cancel): ")
    if confirm.lower() != 'y':
        print("Delete cancelled.")
        return

    try:
        with database.get_connection() as conn:
            services.delete_expense(conn, user, expense_id)
    except ExpenseManagerError as e:
        _handle_operation_error("delete expense", e, user['id'])
        return

    log.info(
        "Expense deleted: user_id=%s, expense_id=%s",
        user['id'], expense_id
    )
    print(f"Expense {expense_id} deleted")

def view_review_history_menu(user: sql.Row):
    print("Review History")

    try:
        with database.get_connection() as conn:
            rows = services.get_review_history(conn, user)
    except ExpenseManagerError as e:
        _handle_operation_error("load review history", e, user['id'])
        return

    if not rows:
        print("No review history found.")
        return

    header = f"{'Expense ID':<10} | {'Status':<10} | {'Reviewer':<20} | {'Review Date':<15} | Comment"
    print(header)
    print("-" * len(header))
    for row in rows:
        comment = row['comment'] if row['comment'] else '(no comment)'
        print(f"{row['id']:<10} | {row['status']:<10} | {row['reviewer']:<20} | {row['review_date']:<15} | {comment}")
        

def employee_menu(user):
    while True:
        print(MENU)

        choice =_prompt_until_valid("Select an option: ", validators.validate_menu_choice)
        if choice == 0:
            return
        elif choice == 1:
            submit_expense_menu(user)
        elif choice == 2:
            view_expenses_menu(user)
        elif choice == 3:
            edit_expense_menu(user)
        elif choice == 4:
            delete_expense_menu(user)
        elif choice == 5:
            view_review_history_menu(user)
        else:
            print("Invalid option.")
