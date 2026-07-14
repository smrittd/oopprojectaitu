import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class Library {
    private String libraryName;
    private final List<LibraryItem> items;
    private final List<LibraryMember> members; // Добавили список читателей

    public Library(String libraryName) {
        this.libraryName = libraryName;
        this.items = new ArrayList<>();
        this.members = new ArrayList<>();
    }

    public void addBook(LibraryItem item) {
        items.add(item);
        System.out.println("Library registered book: " + item.getTitle());
    }

    public void addMember(LibraryMember member) {
        if (!members.contains(member)) {
            members.add(member);
        }
    }

    public LibraryMember findMemberById(String userId) {
        for (LibraryMember m : members) {
            if (m.getUserId().equals(userId)) return m;
        }
        return null;
    }

    public List<LibraryItem> getItems() { return items; }

    public Book findBookById(String bookId) {
        for (LibraryItem item : items) {
            if (item.getBookId().equals(bookId) && item instanceof Book) {
                return (Book) item;
            }
        }
        return null;
    }

    public void processBorrow(String bookId, LibraryMember member) {
        Book book = findBookById(bookId);

        if (book == null) {
            System.out.println("Transaction failed: Book ID " + bookId + " does not exist.");
            return;
        }

        if (book.isAvailable()) {
            book.setAvailable(false);
            member.takeBook(book);
            System.out.println("Success: " + member.getName() + " borrowed \"" + book.getTitle() + "\"");
        } else {
            System.out.println("Transaction failed: \"" + book.getTitle() + "\" is already out on loan.");
        }
    }

    public List<Book> filterByAuthor(String author) {
        List<Book> result = new ArrayList<>();
        for (LibraryItem item : items) {
            if (item instanceof Book book) {
                if (book.getAuthor().equalsIgnoreCase(author)) {
                    result.add(book);
                }
            }
        }
        return result;
    }

    public void sortLibraryCatalog() {
        items.sort(Comparator.comparing(LibraryItem::getTitle));
    }

    public void displayLibraryInfo() {
        System.out.println("Library Catalog for " + libraryName);
        for (LibraryItem item : items) {
            item.displayInfo();
        }
    }
}