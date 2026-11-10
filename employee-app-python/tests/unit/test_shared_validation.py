import csv
from pathlib import Path

import pytest

from employee_app import validators
from employee_app.exceptions import ValidationError

CSV_PATH = Path(__file__).resolve().parents[3] / "shared-test-data" / "validation_cases.csv"


def _load_cases():
    cases = []
    with CSV_PATH.open(newline="", encoding="utf-8") as f:
        for row in csv.DictReader(f):
            cases.append((row["field"], row["input"], row["expected"] == "true"))
    return cases


@pytest.mark.parametrize("field,raw,expected", _load_cases())
def test_shared_validation_cases(field, raw, expected):
    """Covers: VR-08, VR-09, FR-A1 (shared data)."""
    validators_by_field = {
        "expense_id": validators.validate_expense_id,
        "menu_choice": validators.validate_menu_choice,
        "login_input": validators.validate_login_input,
    }
    validator = validators_by_field[field]

    if expected:
        validator(raw)
    else:
        with pytest.raises(ValidationError):
            validator(raw)