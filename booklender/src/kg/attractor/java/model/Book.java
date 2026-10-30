package kg.attractor.java.model;

public class Book {
    private int id;
    private String title;
    private String author;
    private String description;
    private String imagePath;

    public Book(int id, String title, String author,
                String description, String imagePath) {
        this.id = id;
        this.title = title;
        this.author = author;
        this.description = description;
        this.imagePath = imagePath;
    }

    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getAuthor() {
        return author;
    }

    public String getDescription() {
        return description;
    }

    public String getImagePath() {
        return imagePath;
    }
}