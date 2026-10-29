import sqlite3
from employee_app.exceptions import AuthenticationError, AuthorizationError

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

def role_required(user, expected_role):
    if user is None or user["role"] != expected_role:
        raise AuthorizationError(f"This account does not have access to this app.")