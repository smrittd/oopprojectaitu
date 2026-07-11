public abstract class LibraryItem {
    private String title;
    private String bookId;

    public LibraryItem(String title, String bookId) {
        this.title = title;
        this.bookId = bookId;
    }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getBookId() { return bookId; }
    public void setBookId(String bookId) { this.bookId = bookId; }

    public abstract void displayInfo();
}