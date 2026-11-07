import sqlite3
import sys

from employee_app import menu
from employee_app import logger
from employee_app.exceptions import ExpenseManagerError


def main():
    logger.setup_logger()
    log = logger.get_logger()
    log.info("Starting fresh run of employee app.")

    try:
        user = menu.login_menu(expected_role="employee")
        menu.employee_menu(user)
    except ExpenseManagerError as e:
        log.error("Application error: %s", e)
        print("Unexpected error. See log.")
        sys.exit(1)
    except sqlite3.Error as e:
        log.error("Database error: %s", e)
        print("Database error. Check EM_DB_PATH and that the database is set up correctly.")
        sys.exit(1)
    except Exception as e:
        log.exception("Unexpected error: %s", e)
        print("Unexpected error. See log.")
        sys.exit(1)


if __name__ == "__main__":
    main()