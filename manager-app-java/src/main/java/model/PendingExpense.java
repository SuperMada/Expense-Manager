package model;

public record PendingExpense(int id, String fullName, String date, String category, double amount, String description) {}
