# Revature Expense Manager

A console-based expense tracking system for small teams. It has two applications that share one database:

- A **Python Employee App** for submitting and managing personal expenses.
- A **Java Manager App** for reviewing, approving, or denying expenses and viewing reports.
- A **shared SQLite database** so both apps see the same data.

This is a **Quality Engineering and Automation** project. You will build both apps *and* prove they work with well-designed test cases, exploratory testing, and automated tests in both languages.

## Prerequisites

- Python 3.15+
- Java 15+
- Maven 3.10+
- SQLite CLI (`sqlite3`)

## Repository layout

`TODO`

## Setup

### 1. Configure the database path

Copy `.env.example` to `.env` and set `EM_DB_PATH` to an absolute path:

```
EM_DB_PATH=C:/absolute/path/to/expense-manager/database/db.db
```

Both apps read this variable. Relative paths resolve against the launch
directory and are not recommended.

### 2. Create the database

```bash
sqlite3 database/db.db sql/schema.sql
sqlite3 database/db.db sql/seed.sql
```

Creates all tables, constraints, and seed data (2 managers, 4 employees,
6 categories, 25 expenses).

### 3. Install Python + dependencies

```bash
python -m venv venv
# Linux/macOS
source venv/bin/activate
# Windows (PowerShell)
venv/Scripts/activate.ps1
pip install -r requirements.txt
```

## Running the apps

### Employee App (Python)

```bash
cd employee_app_python
python -m employee_app.main
```