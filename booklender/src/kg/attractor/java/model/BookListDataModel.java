package kg.attractor.java.model;

import java.util.List;

public class BookListDataModel {
    private List<BookListItem> books;

    public BookListDataModel(List<BookListItem> books) {
        this.books = books;
    }

    public List<BookListItem> getBooks() {
        return books;
    }
}
