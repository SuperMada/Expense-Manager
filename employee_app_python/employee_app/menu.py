import sqlite3
from employee_app import services
from employee_app.exceptions import AuthenticationError

def _read_input(prompt: str):
    while True:
        value = input(prompt).strip()
        if value:
            return value

def login_menu(conn: sqlite3.Connection, expected_role: str):
    while True:
        username = _read_input("Username: ")
        password = _read_input("Password: ")
        try:
            user = services.authenticate_user(conn, username, password)
        except AuthenticationError:
            print("Invalid username or password")
            continue

        print()
        print(f"Welcome, {user['full_name']}!")
        return user

def employee_menu(conn: sqlite3.Connection, user):
    while True:
        print("Employee Menu")
        print("0. Exit")
        choice = input("Select an option: ")
        if choice == "0":
            return
