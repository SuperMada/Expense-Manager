import sys

from employee_app import menu
from employee_app import database

def main():
    try:
        with database.get_connection() as conn:
            user = menu.login_menu(conn, expected_role="employee")
            menu.employee_menu(conn, user)
    except Exception as e:
        print(f"An error occurred: {e}")
        sys.exit(1)

if __name__ == "__main__":
    main()