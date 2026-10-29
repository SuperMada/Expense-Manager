# Empty exceptions to be properly defined later

class ExpenseManagerError(Exception):
    pass


class AuthenticationError(ExpenseManagerError):
    pass


class AuthorizationError(ExpenseManagerError):
    pass