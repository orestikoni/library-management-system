package library;

import java.time.LocalDate;

public class BorrowedBook {
    private String username;
    private String bookTitle;
    private LocalDate borrowDate;
    private LocalDate overdueDate;
    private boolean isOverdue;

    public BorrowedBook(String username, String bookTitle, LocalDate borrowDate, LocalDate overdueDate, boolean isOverdue) {
        this.username = username;
        this.bookTitle = bookTitle;
        this.borrowDate = borrowDate;
        this.overdueDate = overdueDate;
        this.isOverdue = isOverdue;
    }

    public String getUsername() {
        return username;
    }

    public String getBookTitle() {
        return bookTitle;
    }

    public LocalDate getBorrowDate() {
        return borrowDate;
    }

    public LocalDate getOverdueDate() {
        return overdueDate;
    }

    public boolean isOverdue() {
        return isOverdue;
    }

    public void setOverdue(boolean overdue) {
        isOverdue = overdue;
    }
}
