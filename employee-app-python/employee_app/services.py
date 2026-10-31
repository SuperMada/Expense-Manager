import sqlite3 as sql
from employee_app.exceptions import AuthenticationError, AuthorizationError, DatabaseError

def authenticate_user(conn: sql.Connection, username, password):
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


def submit_expense(conn: sql.Connection,
                   user: sql.Row,
                   amount: float,
                   category: int,
                   description: str,
                   expense_date: str):

    role_required(user, "employee")
    try:
        cursor = conn.execute(
            "INSERT INTO expenses "
            "(user_id, category_id, amount, description, expense_date, created_at) "
            "VALUES (?, ?, ?, ?, ?, datetime('now'))",
            (user["id"], category, amount, description, expense_date)
        )

        expense_id = cursor.lastrowid
        if expense_id is None:
            raise DatabaseError("Failed to submit expense")

        conn.execute(
            "INSERT INTO approvals (expense_id, status) "
            "VALUES (?, 'pending')",
            (expense_id,)
        )

    except sql.Error as e:
        raise DatabaseError(f"Database error: {e}")

    return expense_id

def get_own_expenses(conn: sql.Connection, user: sql.Row, status=None):
    role_required(user, "employee")

    try:
        rows = conn.execute(
            "SELECT e.id, e.expense_date, c.name as category, e.amount, a.status "
            "FROM expenses e "
            "INNER JOIN categories c ON e.category_id = c.id "
            "INNER JOIN approvals a ON e.id = a.expense_id "
            "WHERE e.user_id = ? "
            "AND (? is NULL OR a.status = ?)" # Filter by status if provided
            "ORDER BY e.expense_date DESC, e.id DESC",
            (user["id"], status, status)
        ).fetchall()
    except sql.Error as e:
        raise DatabaseError(f"Database error: {e}")

    return rows