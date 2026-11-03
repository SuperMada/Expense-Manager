import sys

from employee_app import menu

def main():
    try:
        user = menu.login_menu(expected_role="employee")
        menu.employee_menu(user)
    except Exception as e:
        print(f"An error occurred: {e}")
        sys.exit(1)

if __name__ == "__main__":
    main()