package kg.attractor.java.model;

import java.time.LocalDate;

public class Loan {
    private int bookId;
    private int employeeId;
    private LocalDate borrowedAt;
    private LocalDate returnedAt;

    public int getBookId() {
        return bookId;
    }

    public int getEmployeeId() {
        return employeeId;
    }

    public LocalDate getBorrowedAt() {
        return borrowedAt;
    }

    public LocalDate getReturnedAt() {
        return returnedAt;
    }

    public Loan(int bookId, int employeeId, LocalDate borrowedAt) {
        this.bookId = bookId;
        this.employeeId = employeeId;
        this.borrowedAt = borrowedAt;
    }

    public boolean isActive(){
        return returnedAt==null;
    }

    void setReturnedAt(LocalDate returnedAt) {
        this.returnedAt = returnedAt;
    }

    public void markReturned(LocalDate date){
        this.setReturnedAt(date);
    }
}
