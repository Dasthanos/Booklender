package kg.attractor.java.model;

public class Employee {
    private int id;
    private String fullName;
    private String email;
    private String password;

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public Employee(int id, String fullName, String email, String password) {
        this.id = id;
        this.fullName = fullName;
        this.email = email;
        this.password = password;
    }

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
