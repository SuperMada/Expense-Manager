import os
import sqlite3
from contextlib import contextmanager
from dotenv import load_dotenv

load_dotenv()

def db_path():
    path = os.environ.get("EM_DB_PATH")
    if not path:
        raise RuntimeError("EM_DB_PATH is not set.")
    return path


@contextmanager
def get_connection(path=None):
    conn = sqlite3.connect(path or db_path(), timeout=5)
    conn.row_factory = sqlite3.Row
    conn.execute("PRAGMA foreign_keys = ON")
    try:
        yield conn
        conn.commit()
    except Exception:
        conn.rollback()
        raise
    finally:
        conn.close()