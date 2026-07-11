import java.util.Objects;

public class Book extends LibraryItem {
    private String author;
    private boolean isAvailable;

    public Book(String title, String bookId, String author) {
        super(title, bookId);
        this.author = author;
        this.isAvailable = true;
    }

    public String getAuthor() { return author; }
    public void setAuthor(String author) { this.author = author; }

    public boolean isAvailable() { return isAvailable; }
    public void setAvailable(boolean available) { isAvailable = available; }

    @Override
    public void displayInfo() {
        System.out.println("Book: " + getTitle() + " | Author: " + author + " | ID: " + getBookId() + " | Available: " + isAvailable);
    }

    @Override
    public String toString() {
        return "Book{title='" + getTitle() + "', author='" + author + "', bookId='" + getBookId() + "'}";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Book book = (Book) o;
        return Objects.equals(getBookId(), book.getBookId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getBookId());
    }
}
