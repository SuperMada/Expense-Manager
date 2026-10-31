import sqlite3 as sql
import sys
from datetime import date
from employee_app import services, validators
from employee_app.exceptions import AuthenticationError, AuthorizationError, ValidationError, ExpenseManagerError

MENU = """
----- Employee Expense Manager -----
1. Submit expense
2. View my expenses
3. Edit pending expense (Not implemented)
4. Delete pending expense (Not implemented)
5. Review history (Not implemented)
0. Exit"""

MENU_VIEW_EXPENSES = """
----- Filter Expenses by Status -----
1. All
2. Pending
3. Approved
4. Denied
"""

def _read_input(prompt: str):
    while True:
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
            print(e)

def login_menu(conn: sql.Connection, expected_role: str):
    attempts = 0
    while True:
        username = _read_input("Username: ")
        password = _read_input("Password: ")
        try:
            user = services.authenticate_user(conn, username, password)
        except AuthenticationError:
            print("Invalid username or password")
            attempts += 1
            if attempts >= 3:
                print("Too many failed attempts.")
                sys.exit(1)
            continue

        try:
            services.role_required(user, expected_role)
        except AuthorizationError:
            print("This account does not have access to this app.")
            sys.exit(1)

        print()
        print(f"Welcome, {user['full_name']}!")
        return user

def submit_expense_menu(conn: sql.Connection, user: sql.Row):
    print("Submit Expense")

    amount = _prompt_until_valid("Amount: ", validators.validate_amount)
    description = _prompt_until_valid("Description: ", validators.validate_description)
    category = _prompt_until_valid("Category: ", validators.validate_category)

    # Date of the expense, defaults to today if not provided
    while True:
        raw = input("Date (YYYY-MM-DD) (Enter for today): ")
        try:
            # if input is empty, default to today
            if not raw:
                expense_date = date.today().isoformat()
            else:
                expense_date = validators.validate_expense_date(raw)
            break
        except ValidationError as e:
            print(e)

    try:
        expense_id = services.submit_expense(conn, user, amount, category, description, expense_date)
        print(f"Expense submitted. ID: {expense_id}")
    except ExpenseManagerError as e:
        print(f"Failed to submit expense: {e}")


def view_expenses_menu(conn: sql.Connection, user: sql.Row):
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
        rows = services.get_own_expenses(conn, user, status)
    except ExpenseManagerError as e:
        print(f"Failed to view expenses: {e}")
        return
    
    if not rows:
        print("No expenses found.")
        return

    header = f"{'ID':<5} | {'Date':<12} | {'Category':<16} | {'Amount':<11} | {'Status':<10}"
    print(header)
    print("-" * len(header))
    for row in rows:
        print(f"{row['id']:<5} | {row['expense_date']:<12} | {row['category']:<16} | ${row['amount']:<10.2f} | {row['status']:<10}")


def edit_expense_menu(conn: sql.Connection, user: sql.Row):
    print("Edit Expense")
    expense_id = _prompt_until_valid("Expense ID: ", validators.validate_expense_id)

    try:
        expense = services.get_own_pending_expense(conn, user, expense_id)
    except ExpenseManagerError as e:
        print(f"Failed to retrieve expense: {e}")
        return

    # Print which expense is being edited
    print(f"Current: amount= {expense['amount']}, category_id= {expense['category_id']}, description= {expense['description']!r}, date= {expense['expense_date']}")

    amount = _prompt_until_valid("New amount (Enter to keep current): ",
                                 validators.validate_amount,
                                 default=str(expense['amount']))
    category = _prompt_until_valid("New category ID (Enter to keep current): ",
                                   validators.validate_category,
                                   default=str(expense['category_id']))
    description = _prompt_until_valid("New description (Enter to keep current): ",
                                      validators.validate_description,
                                      default=expense['description'])
    expense_date = _prompt_until_valid("New expense date (YYYY-MM-DD) (Enter to keep current): ",
                                       validators.validate_expense_date,
                                       default=expense['expense_date'])

    try:
        services.update_expense(conn, user, expense_id, amount, category, description, expense_date)
        print(f"Expense {expense_id} updated")
    except ExpenseManagerError as e:
        print(f"Failed to update expense: {e}")

def delete_expense_menu(conn: sql.Connection, user: sql.Row):
    print("Delete Pending Expense")
    expense_id = _prompt_until_valid("Expense ID: ", validators.validate_expense_id)

    try:
        expense = services.get_own_pending_expense(conn, user, expense_id)
    except ExpenseManagerError as e:
        print(f"Failed to retrieve expense: {e}")
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
        services.delete_expense(conn, user, expense_id)
        print(f"Expense {expense_id} deleted")
    except ExpenseManagerError as e:
        print(f"Failed to delete expense: {e}")


def employee_menu(conn: sql.Connection, user):
    while True:
        print(MENU)

        choice = input("Select an option: ")
        if choice == "0":
            return
        elif choice == "1":
            submit_expense_menu(conn, user)
        elif choice == "2":
            view_expenses_menu(conn, user)
        elif choice == "3":
            edit_expense_menu(conn, user)
        elif choice == "4":
            delete_expense_menu(conn, user)
        elif choice == "5":
            print("Review history is not implemented.")
        else:
            print("Invalid option.")
