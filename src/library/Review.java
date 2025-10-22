package library;

import java.io.Serializable;

public class Review implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private String bookTitle;
    private String username;
    private int rating;       // 0 to 5 stars
    private String reviewText; // up to 50 words

    public Review(String bookTitle, String username, int rating, String reviewText) {
        this.bookTitle = bookTitle;
        this.username = username;
        this.rating = rating;
        this.reviewText = reviewText;
    }

    public String getBookTitle() {
        return bookTitle;
    }

    public String getUsername() {
        return username;
    }

    public int getRating() {
        return rating;
    }

    public String getReviewText() {
        return reviewText;
    }
    
    @Override
    public String toString() {
        return "By " + username + " | Rating: " + rating + " stars\n" + reviewText;
    }
}
