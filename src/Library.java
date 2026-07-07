import java.util.ArrayList;
import java.util.List;

public class Library {
    private String LibraryName;
    private List<Book> books;


    public Library(String LibraryName){
        this.LibraryName=LibraryName;
        this.books = new ArrayList<>();
    }

    private String getLibraryName(){return LibraryName;}
    private void setLibraryName(String libraryName){this.LibraryName = LibraryName;}

    public List<Book> getBooks(){return books;}

    public void addBook(Book book){
        books.add(book);
        System.out.println("Added book to: " + book.getTitle());
    }

    public void displayLibraryInfo(){
        System.out.println("Library Catalog for" + LibraryName);
        if (books.isEmpty()){
            System.out.println("The library has no books available.");
        } else{
            for (Book book: books){
                book.displayinfo();
            }
        }
    }
}
