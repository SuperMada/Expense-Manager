# Empty exceptions to be properly defined later

class ExpenseManagerError(Exception):
    pass

class ValidationError(ExpenseManagerError):
    pass

class AuthenticationError(ExpenseManagerError):
    pass

class AuthorizationError(ExpenseManagerError):
    pass

class NotFoundError(ExpenseManagerError):
    pass

class InvalidStateError(ExpenseManagerError):
    pass

class DatabaseError(ExpenseManagerError):
    pass