public class Book{
    private String title;
    private String author;
    private String isbn;

    public Book(String title, String author, String isbn){
        this.title = title;
        this.author = author;
        this.isbn = isbn;
    }


    public String getTitle() {return  title;}
    public void setTitle() {this.title = title;}

    public String getAuthor() {return author;}
    public void setAuthor(String author) { this.author = author; }

    public String getIsbn() { return isbn; }
    public void setIsbn(String isbn) { this.isbn = isbn; }

    public void displayinfo(){
        System.out.println("Book: \"" + title + "\" | Автор: " + author + "| ISBN" + isbn);
    }
}