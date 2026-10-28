import sqlite3
from employee_app.exceptions import AuthenticationError

def authenticate_user(conn: sqlite3.Connection, username, password):
    row = conn.execute(
        "SELECT id, username, role, full_name "
        "FROM users "
        "WHERE username = ? AND password = ?",
        (username, password)
    ).fetchone()

    if row is None:
        raise AuthenticationError("Invalid username or password")

    return row