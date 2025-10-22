package library;

import java.io.Serializable;

public class Reservation implements Serializable {
    private static final long serialVersionUID = 1L;
    private String bookTitle;
    private String username;
    private String status;

    public Reservation(String bookTitle, String username) {
        this.bookTitle = bookTitle;
        this.username = username;
        this.status = "Reserved";
    }

    public String getBookTitle() { 
        return bookTitle; 
    }
    public String getUsername() { 
        return username; 
    }
    public String getStatus() { 
        return status; 
    }

    public void setStatus(String status) { 
        this.status = status; 
    }
}
