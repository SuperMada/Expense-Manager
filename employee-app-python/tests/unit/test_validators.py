import pytest
from datetime import date, timedelta
from employee_app import validators
from employee_app.exceptions import ValidationError


# VR-01 - Amount Validation

@pytest.mark.parametrize(
    "raw, expected",
    [
        ("45.50", 45.50),
        ("10000", 10000),
        ("12.5", 12.5)
    ]
)
def test_amount_valid(raw, expected):
    """Covers: VR-01, PTC-22"""
    result = validators.validate_amount(raw)
    assert result == pytest.approx(expected)

@pytest.mark.negative
@pytest.mark.boundary
def test_amount_zero_rejected():
    """Covers: VR-01, PTC-23"""
    with pytest.raises(ValidationError):
        validators.validate_amount("0")

@pytest.mark.negative
@pytest.mark.boundary
def test_amount_above_max_rejected():
    """Covers: VR-01, PTC-24"""
    with pytest.raises(ValidationError):
        validators.validate_amount("10000.01")

@pytest.mark.negative
@pytest.mark.boundary
def test_amount_negative_rejected():
    """Covers: VR-01, PTC-25"""
    with pytest.raises(ValidationError):
        validators.validate_amount("-5")


@pytest.mark.negative
@pytest.mark.boundary
def test_amount_three_decimals_rejected():
    """Covers: VR-01, PTC-26"""
    with pytest.raises(ValidationError):
        validators.validate_amount("0.001")


@pytest.mark.negative
@pytest.mark.parametrize(
    "raw",
    [
        "abc",
        "$50"
    ]
)
def test_amount_non_numeric_rejected(raw):
    """Covers: VR-01, PTC-27"""
    with pytest.raises(ValidationError):
        validators.validate_amount(raw)


# VR-02 - Description Validation

@pytest.mark.parametrize(
    "raw",
    [
        "Lunch",
        "a",
        "a" * 200,
    ]
)
def test_description_valid(raw):
    """Covers: VR-02, PTC-28"""
    result = validators.validate_description(raw)
    assert result == raw

@pytest.mark.negative
@pytest.mark.boundary
def test_description_empty_rejected():
    """Covers: VR-02, PTC-29"""
    with pytest.raises(ValidationError):
        validators.validate_description("")


@pytest.mark.negative
def test_description_whitespace_rejected():
    """Covers: VR-02, PTC-30"""
    with pytest.raises(ValidationError):
        validators.validate_description("   ")

@pytest.mark.negative
@pytest.mark.boundary
def test_description_too_long_rejected():
    """Covers: VR-02, PTC-31"""
    with pytest.raises(ValidationError):
        validators.validate_description("a" * 201)

# VR-03 - Category

@pytest.mark.parametrize(
    "raw, expected",
    [
        ("1", 1),
        ("2", 2),
        ("3", 3),
        ("4", 4),
        ("5", 5),
        ("6", 6)
    ] 
)
def test_category_valid(raw, expected):
    """Covers: VR-03, PTC-32"""
    result = validators.validate_category(raw)
    assert result == expected


@pytest.mark.negative
@pytest.mark.boundary
def test_category_zero_rejected():
    """Covers: VR-03, PTC-33"""
    with pytest.raises(ValidationError):
        validators.validate_category("0")


@pytest.mark.negative
@pytest.mark.boundary
def test_category_seven_rejected():
    """Covers: VR-03, PTC-34"""
    with pytest.raises(ValidationError):
        validators.validate_category("7")


@pytest.mark.negative
def test_category_non_numeric_rejected():
    """Covers: VR-03, PTC-35"""
    with pytest.raises(ValidationError):
        validators.validate_category("abc")


# VR-04 - Date Validation

def test_date_valid():
    """Covers: VR-04, PTC-36"""
    today = date.today()
    ninety_days_ago = (today - timedelta(days=90)).isoformat()
    result_today = validators.validate_expense_date(today.isoformat())
    result_ninety_days_ago = validators.validate_expense_date(ninety_days_ago)
    assert result_today == today.isoformat()
    assert result_ninety_days_ago == ninety_days_ago

@pytest.mark.negative
@pytest.mark.boundary
def test_date_future_rejected():
    """Covers: VR-04, PTC-37"""
    tomorrow = (date.today() + timedelta(days=1)).isoformat()
    with pytest.raises(ValidationError):
        validators.validate_expense_date(tomorrow)


@pytest.mark.negative
@pytest.mark.boundary
def test_date_too_old_rejected():
    """Covers: VR-04, PTC-38"""
    ninety_one_days_ago = (date.today() - timedelta(days=91)).isoformat()
    with pytest.raises(ValidationError):
        validators.validate_expense_date(ninety_one_days_ago)


# VR-08 - Expense ID validation

@pytest.mark.parametrize(
    "raw, expected",
    [
        ("7", 7),
        ("1", 1),
        ("999", 999)
    ]
)
def test_expense_id_valid(raw, expected):
    """Covers: VR-08, PTC-39"""
    result = validators.validate_expense_id(raw)
    assert result == expected

@pytest.mark.negative
@pytest.mark.boundary
def test_expense_id_zero_rejected():
    """Covers: VR-08, PTC-40"""
    with pytest.raises(ValidationError):
        validators.validate_expense_id("0")


@pytest.mark.negative
@pytest.mark.boundary
def test_expense_id_negative_rejected():
    """Covers: VR-08, PTC-41"""
    with pytest.raises(ValidationError):
        validators.validate_expense_id("-1")


@pytest.mark.negative
@pytest.mark.parametrize(
    "raw",
    [
        ("1.5"),
        ("abc"),
    ]
)
def test_expense_id_non_integer_rejected(raw):
    """Covers: VR-08, PTC-42"""
    with pytest.raises(ValidationError):
        validators.validate_expense_id(raw)