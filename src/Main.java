public class Main{
    public static void main(String[] args){
        System.out.println("Initializing Library Management System");
        Book book1 = new Book("Grokking Algorithms", "Aditya Bhargava", "100-1012");
        Book book2 = new Book("War and Peace", "Leo Tolstoy", "902-1923");
        Book book3 = new Book("Grokking Algorithms", "Aditya Bhargava", "100-1012");


        LibraryMember member1 = new LibraryMember("Alisher", "101");
        LibraryMember member2 = new LibraryMember("Leyla", "102");

        Library NationalLibrary = new Library("Qazaqstan Respublikasynyŋ kıtapxan");

        System.out.println("=== Adding Books ===");
        NationalLibrary.addBook(book1);
        NationalLibrary.addBook(book2);

        System.out.println("\n=== Library Members ===");
        member1.displayInfo();
        member2.displayInfo();
        NationalLibrary.displayLibraryInfo();

        System.out.println("\n=== Checking System for Duplicates (Object Comparison) ===");


        System.out.println("Comparing book1 (\"" + book1.getTitle() + "\") and book2 (\"" + book2.getTitle() + "\"):");
        if (book1.getIsbn().equals(book2.getIsbn())) {
            System.out.println("Result: These are the same books (ISBN match).");
        } else {
            System.out.println("Result: These are different books.");
        }
        System.out.println("\nComparing book1 (\"" + book1.getTitle() + "\") and book3 (\"" + book3.getTitle() + "\"):");
        if (book1.getIsbn().equals(book3.getIsbn())) {
            System.out.println("Result: Duplicate found! Books have identical ISBN numbers.");
        } else {
            System.out.println("Result: These are different books.");
        }
    }
}