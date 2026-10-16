package kg.attractor.java.model;

public class Employee {
    private int id;
    private String fullName;

    public String getFullName() {
        return fullName;
    }

    public int getId() {
        return id;
    }

    public Employee(int id, String fullName) {
        this.id = id;
        this.fullName = fullName;
    }
}
