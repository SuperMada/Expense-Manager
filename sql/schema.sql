PRAGMA foreign_keys = ON;
 
CREATE TABLE users (
    id        INTEGER PRIMARY KEY,
    username  TEXT NOT NULL UNIQUE,
    password  TEXT NOT NULL,
    role      TEXT NOT NULL CHECK (role IN ('employee', 'manager')),
    full_name TEXT NOT NULL
);
 
CREATE TABLE categories (
    id   INTEGER PRIMARY KEY,
    name TEXT NOT NULL UNIQUE
);
 
CREATE TABLE expenses (
    id           INTEGER PRIMARY KEY,
    user_id      INTEGER NOT NULL,
    category_id  INTEGER NOT NULL,
    amount       REAL NOT NULL CHECK (amount > 0 AND amount <= 10000),
    description  TEXT NOT NULL,
    expense_date TEXT NOT NULL,  -- YYYY-MM-DD
    created_at   TEXT NOT NULL,  -- YYYY-MM-DD HH:MM:SS
    FOREIGN KEY (user_id)     REFERENCES users(id),
    FOREIGN KEY (category_id) REFERENCES categories(id)
);
 
CREATE TABLE approvals (
    id          INTEGER PRIMARY KEY,
    expense_id  INTEGER NOT NULL UNIQUE,
    status      TEXT NOT NULL DEFAULT 'pending'
                CHECK (status IN ('pending', 'approved', 'denied')),
    reviewer_id INTEGER,         -- NULL until reviewed
    comment     TEXT,
    review_date TEXT,            -- YYYY-MM-DD
    FOREIGN KEY (expense_id)  REFERENCES expenses(id),
    FOREIGN KEY (reviewer_id) REFERENCES users(id)
);
