package kg.attractor.java.model;

public class BookListItem {
    private Book book;
    private Employee currentHolder;

    public Book getBook() {
        return book;
    }

    public Employee getCurrentHolder() {
        return currentHolder;
    }

    public BookListItem(Book book, Employee currentHolder) {
        this.book = book;
        this.currentHolder = currentHolder;
    }

    public boolean isBorrowed(){
        return currentHolder!=null;
    }
}
