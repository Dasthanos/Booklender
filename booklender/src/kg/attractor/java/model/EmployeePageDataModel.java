package kg.attractor.java.model;

import java.util.List;

public class EmployeePageDataModel {
    private Employee employee;
    private List<Book> currentBooks;
    private List<Book> pastBooks;

    public EmployeePageDataModel(
            Employee employee,
            List<Book> currentBooks,
            List<Book> pastBooks
    ) {
        this.employee = employee;
        this.currentBooks = currentBooks;
        this.pastBooks = pastBooks;
    }

    public Employee getEmployee() {
        return employee;
    }

    public List<Book> getCurrentBooks() {
        return currentBooks;
    }

    public List<Book> getPastBooks() {
        return pastBooks;
    }
}