package library;

import java.io.Serializable;
import java.time.LocalDate;

public class BorrowedBookRecord implements Serializable {
    private static final long serialVersionUID = 1L;

    private String username;
    private Book book;
    private LocalDate borrowDate;
    private LocalDate dueDate;
    private boolean returned;      // true if the book has been returned
    private LocalDate returnDate;  // the date the book was returned (if any)

    public BorrowedBookRecord(String username, Book book, LocalDate borrowDate, LocalDate dueDate, boolean returned, LocalDate returnDate) {
        this.username = username;
        this.book = book;
        this.borrowDate = borrowDate;
        this.dueDate = dueDate;
        this.returned = returned;
        this.returnDate = returnDate;
    }

    public String getUsername() {
        return username;
    }

    public Book getBook() {
        return book;
    }
   
    // A helper getter to return just the book title.
    public String getBookTitle() {
        return book != null ? book.getTitle() : "";
    }

    public LocalDate getBorrowDate() {
        return borrowDate;
    }

    public LocalDate getDueDate() {
        return dueDate;
    }

    public boolean isReturned() {
        return returned;
    }

    public LocalDate getReturnDate() {
        return returnDate;
    }
   
    // Add these setter methods so that the record can be updated.
    public void setReturned(boolean returned) {
        this.returned = returned;
    }

    public void setReturnDate(LocalDate returnDate) {
        this.returnDate = returnDate;
    }

    /**
     * Computes the overdue status:
     * - If the book has been returned:
     *     * "Returned Late" if returned after the due date.
     *     * "Returned On Time" otherwise.
     * - If the book has not been returned:
     *     * "Overdue" if today is after the due date.
     *     * "Not Overdue" otherwise.
     */
    public String getOverdueStatus() {
        LocalDate today = LocalDate.now();
        if (returned) {
            if (returnDate != null && returnDate.isAfter(dueDate)) {
                return "Returned Late";
            } else {
                return "Returned On Time";
            }
        } else {
            return today.isAfter(dueDate) ? "Overdue" : "Not Overdue";
        }
    }
}


