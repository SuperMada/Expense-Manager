import sys

from employee_app import logger
from employee_app import menu

def main():
    logger.setup_logger()
    log = logger.get_logger()
    try:
        log.info("Starting employee application")
        user = menu.login_menu(expected_role="employee")
        menu.employee_menu(user)
    except Exception as e:
        print(f"An error occurred: {e}")
        sys.exit(1)

if __name__ == "__main__":
    main()