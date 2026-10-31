import sqlite3 as sql
import sys
from datetime import date
from employee_app import services, validators
from employee_app.exceptions import AuthenticationError, AuthorizationError, ValidationError, ExpenseManagerError

MENU = """
----- Employee Expense Manager -----
1. Submit expense
2. View my expenses (Not implemented)
3. Edit pending expense (Not implemented)
4. Delete pending expense (Not implemented)
5. Review history (Not implemented)
0. Exit"""

def _read_input(prompt: str):
    while True:
        value = input(prompt).strip()
        if value:
            return value

def _prompt_until_valid(prompt: str, validate: callable):
    while True:
        try:
            return validate(input(prompt))
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


def employee_menu(conn: sql.Connection, user):
    while True:
        print(MENU)

        choice = input("Select an option: ")
        if choice == "0":
            return
        elif choice == "1":
            submit_expense_menu(conn, user)
        elif choice == "2":
            print("View my expenses is not implemented.")
        elif choice == "3":
            print("Edit pending expense is not implemented.")
        elif choice == "4":
            print("Delete pending expense is not implemented.")
        elif choice == "5":
            print("Review history is not implemented.")
        else:
            print("Invalid option.")
