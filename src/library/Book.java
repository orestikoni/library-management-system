package library;

import java.io.Serializable;

public class Book implements Serializable {
    private static final long serialVersionUID = 4365661966376544526L;
    
    private String title;
    private String author;
    private String genre;
    private String isbn;
    private int quantity;
    private String coverImagePath; // Field for the cover image file path

    public Book(String title, String author, String genre, String isbn, int quantity, String coverImagePath) {
        this.title = title;
        this.author = author;
        this.genre = genre;
        this.isbn = isbn;
        this.quantity = quantity;
        this.coverImagePath = coverImagePath;
    }

    // Getters and Setters
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getAuthor() { return author; }
    public void setAuthor(String author) { this.author = author; }

    public String getGenre() { return genre; }
    public void setGenre(String genre) { this.genre = genre; }

    public String getIsbn() { return isbn; }
    public void setIsbn(String isbn) { this.isbn = isbn; }

    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }

    public String getCoverImagePath() { return coverImagePath; }
    public void setCoverImagePath(String coverImagePath) { this.coverImagePath = coverImagePath; }

    @Override
    public String toString() {
        return title;
    }
}
