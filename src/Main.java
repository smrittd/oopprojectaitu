import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        DatabaseConnection.initializeDatabase();
        Library library = new Library("Almaty Tech Library");

        initDefaultBooks(library);

        Scanner scanner = new Scanner(System.in);
        boolean running = true;

        System.out.println("=== ACTIVE LIBRARY MANAGEMENT SYSTEM ===");

        while (running) {
            System.out.println("\nAvailable actions:");
            System.out.println("1. Display book catalog");
            System.out.println("2. Sort catalog by title");
            System.out.println("3. Filter books by author");
            System.out.println("4. Register member (in Postgres DB)");
            System.out.println("5. Lend book to member");
            System.out.println("0. Exit");
            System.out.print("Select action: ");


            if (!scanner.hasNextLine()) {
                break;
            }

            String input = scanner.nextLine().trim();


            if (input.isEmpty()) {
                continue;
            }

            int choice;
            try {
                choice = Integer.parseInt(input);
            } catch (NumberFormatException e) {
                System.out.println("Error: Please enter a valid number!");
                continue;
            }

            switch (choice) {
                case 1:
                    library.displayLibraryInfo();
                    break;
                case 2:
                    library.sortLibraryCatalog();
                    System.out.println("Catalog sorted.");
                    library.displayLibraryInfo();
                    break;
                case 3:
                    System.out.print("Enter author name: ");
                    String author = scanner.nextLine();
                    List<Book> filtered = library.filterByAuthor(author);
                    if (filtered.isEmpty()) {
                        System.out.println("No books found by this author.");
                    } else {
                        for (Book b : filtered) {
                            System.out.println("Found: " + b);
                        }
                    }
                    break;
                case 4:
                    System.out.print("Enter member name: ");
                    String name = scanner.nextLine();
                    System.out.print("Enter unique User ID: ");
                    String userId = scanner.nextLine();

                    LibraryMember member = new LibraryMember(name, userId);
                    library.addMember(member);
                    saveMemberToPostgres(member);
                    break;
                case 5:
                    System.out.print("Enter member User ID: ");
                    String mId = scanner.nextLine();
                    LibraryMember currentMember = library.findMemberById(mId);

                    if (currentMember == null) {
                        System.out.println("Error: Register member first (Action 4).");
                        break;
                    }

                    System.out.print("Enter Book ID: ");
                    String bId = scanner.nextLine();
                    library.processBorrow(bId, currentMember);
                    updateBookStatusInPostgres(bId, false);
                    break;
                case 0:
                    running = false;
                    System.out.println("Exiting program.");
                    break;
                default:
                    System.out.println("Invalid action!");
            }
        }
        scanner.close();
    }

    private static void initDefaultBooks(Library library) {
        Book b1 = new Book("Grokking Algorithms", "B-01", "Aditya Bhargava");
        Book b2 = new Book("War and Peace", "B-02", "Leo Tolstoy");
        Book b3 = new Book("Anna Karenina", "B-03", "Leo Tolstoy");
        library.addBook(b1);
        library.addBook(b2);
        library.addBook(b3);

        for (LibraryItem item : library.getItems()) {
            if (item instanceof Book b) {
                saveBookToPostgres(b);
            }
        }
    }

    private static void saveMemberToPostgres(LibraryMember member) {
        String sql = "INSERT INTO library_members (user_id, name) VALUES (?, ?) ON CONFLICT (user_id) DO NOTHING";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, member.getUserId());
            pstmt.setString(2, member.getName());
            pstmt.executeUpdate();
            System.out.println("[PostgreSQL]: Member registered in database.");
        } catch (SQLException e) {
            System.out.println("DB Error: " + e.getMessage());
        }
    }

    private static void saveBookToPostgres(Book book) {
        String sql = "INSERT INTO books (book_id, title, author, is_available) VALUES (?, ?, ?, ?) ON CONFLICT (book_id) DO NOTHING";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, book.getBookId());
            pstmt.setString(2, book.getTitle());
            pstmt.setString(3, book.getAuthor());
            pstmt.setBoolean(4, book.isAvailable());
            pstmt.executeUpdate();
        } catch (SQLException ignored) {}
    }

    private static void updateBookStatusInPostgres(String bookId, boolean status) {
        String sql = "UPDATE books SET is_available = ? WHERE book_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setBoolean(1, status);
            pstmt.setString(2, bookId);
            pstmt.executeUpdate();
        } catch (SQLException ignored) {}
    }
}