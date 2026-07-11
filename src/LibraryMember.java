import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class LibraryMember {
    private String name;
    private String userId;
    private final List<Book> borrowedBooks;

    public LibraryMember(String name, String userId) {
        this.name = name;
        this.userId = userId;
        this.borrowedBooks = new ArrayList<>();
    }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public List<Book> getBorrowedBooks() { return borrowedBooks; }

    public void takeBook(Book book) {
        borrowedBooks.add(book);
    }

    public void returnBook(Book book) {
        borrowedBooks.remove(book);
    }

    public void displayInfo() {
        System.out.println("Member: " + name + " | User ID: " + userId + " | Books Borrowed: " + borrowedBooks.size());
    }

    @Override
    public String toString() {
        return "LibraryMember{name='" + name + "', userId='" + userId + "'}";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LibraryMember member = (LibraryMember) o;
        return Objects.equals(userId, member.userId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userId);
    }
}