import sqlite3 as sql
from pathlib import Path

import pytest

PROJECT_ROOT = Path(__file__).resolve().parents[2]
SCHEMA_PATH = PROJECT_ROOT / "sql" / "schema.sql"
SEED_PATH = PROJECT_ROOT / "sql" / "seed.sql"

@pytest.fixture
def temp_db(tmp_path):
    """Create a temporary SQLite database with schema and seed data."""
    db_path = tmp_path / "temp.db"
    conn = sql.connect(db_path)
    conn.row_factory = sql.Row
    conn.execute("PRAGMA foreign_keys = ON")
    conn.executescript(SCHEMA_PATH.read_text())
    conn.executescript(SEED_PATH.read_text())
    yield conn
    conn.close()