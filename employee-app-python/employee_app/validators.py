from datetime import date, timedelta
from employee_app.exceptions import ValidationError

AMOUNT_MSG = "Amount must be greater than 0, at most 10000.00, with at most 2 decimals."
DESC_MSG = "Description must be 1-200 characters (including spaces)."
CATEGORY_MSG = "Category must be 1-6."
DATE_MSG = "Date must be YYYY-MM-DD, not in the future, and within the last 90 days."


def validate_amount(raw:str):
    """VR-01: Validate amount format and range.

    Args:
        raw (str): The raw input string representing the amount.

    Returns:
        float: The validated amount.

    Raises:
        ValidationError: If the amount is invalid.
    """
    raw = raw.strip()

    # Empty check
    if raw == "":
        raise ValidationError(AMOUNT_MSG)

    # Character and decimal point check
    for char in raw:
        if not (char.isdigit() or char == '.'):
            raise ValidationError(AMOUNT_MSG)
    if raw.count('.') == 1:
        decimal_part = raw.split('.')[1]
        if len(decimal_part) > 2:
            raise ValidationError(AMOUNT_MSG)
    elif raw.count('.') > 1:
        raise ValidationError(AMOUNT_MSG)

    # Convert to float and range check
    try:
        amount = float(raw)
    except ValueError:
        raise ValidationError(AMOUNT_MSG)
    if amount <= 0 or amount > 10000:
        raise ValidationError(AMOUNT_MSG)

    return amount

def validate_description(raw:str):
    """VR-02: Validate description format and length.

    Args:
        raw (str): The raw input string representing the description.

    Returns:
        str: The validated description.

    Raises:
        ValidationError: If the description is invalid.
    """
    raw = raw.strip()
    if len(raw) == 0 or len(raw) > 200:
        raise ValidationError(DESC_MSG)

    return raw

def validate_category(raw:str):
    """VR-03: Validate category format and range.

    Args:
        raw (str): The raw input string representing the category.

    Returns:
        int: The validated category.

    Raises:
        ValidationError: If the category is invalid.
    """
    raw = raw.strip()
    if raw not in ["1", "2", "3", "4", "5", "6"]:
        raise ValidationError(CATEGORY_MSG)

    return int(raw)

def validate_expense_date(raw:str):
    """VR-04: Validate expense date format and range.

    Args:
        raw (str): The raw input string representing the expense date.

    Returns:
        str: The validated expense date in ISO format (a string like 'YYYY-MM-DD').

    Raises:
        ValidationError: If the expense date is invalid.
    """
    raw = raw.strip()

    # ISO format check
    try:
        expense_date = date.fromisoformat(raw)
    except ValueError:
        raise ValidationError(DATE_MSG)
    if expense_date.isoformat() != raw:
        raise ValidationError(DATE_MSG)

    # Future date and 90-day range check
    today = date.today()
    if expense_date > today or expense_date < today - timedelta(days=90):
        raise ValidationError(DATE_MSG)

    return expense_date.isoformat()