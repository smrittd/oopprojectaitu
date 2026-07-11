import java.util.List;

public class Main {
    public static void main(String[] args) {
        System.out.println("Initializing Active Library Management System");

        Library library = new Library("Almaty Tech Library");

        Book b1 = new Book("Grokking Algorithms", "B-01", "Aditya Bhargava");
        Book b2 = new Book("War and Peace", "B-02", "Leo Tolstoy");
        Book b3 = new Book("Anna Karenina", "B-03", "Leo Tolstoy");

        library.addBook(b1);
        library.addBook(b2);
        library.addBook(b3);

        LibraryMember student1 = new LibraryMember("Alisher", "U-101");
        LibraryMember student2 = new LibraryMember("Leyla", "U-102");

        System.out.println("Executing Borrowing Logic:");
        library.processBorrow("B-01", student1);
        library.processBorrow("B-01", student2);
        library.processBorrow("B-02", student2);

        System.out.println("Checking Members and Catalog States:");
        student1.displayInfo();
        student2.displayInfo();
        library.displayLibraryInfo();

        System.out.println("Data Pool Filtering (Tolstoy Books):");
        List<Book> tolstoyBooks = library.filterByAuthor("Leo Tolstoy");
        for (Book b : tolstoyBooks) {
            System.out.println("Found: " + b.toString());
        }

        System.out.println("Data Pool Sorting:");
        library.sortLibraryCatalog();
        library.displayLibraryInfo();
    }
}