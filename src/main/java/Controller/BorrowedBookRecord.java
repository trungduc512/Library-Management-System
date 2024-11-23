package Controller;

import java.time.LocalDate;

public class BorrowedBookRecord {

    private int borrowerId;
    private String isbn;
    private int quantity;
    private LocalDate borrowedDate;
    private LocalDate returnDate;
    private String title;
    private String status;

    // Constructor
    public BorrowedBookRecord(int borrowerId, String title, String isbn, int quantity,
                              LocalDate borrowedDate, LocalDate returnDate) {
        this.borrowerId = borrowerId;
        this.isbn = isbn;
        this.quantity = quantity;
        this.borrowedDate = borrowedDate;
        this.returnDate = returnDate;
        this.title = title;
    }

    // Getters và Setters
    public int getBorrowerId() {
        return borrowerId;
    }

    public void setBorrowerId(int borrowerId) {
        this.borrowerId = borrowerId;
    }

    public String getIsbn() {
        return isbn;
    }

    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public LocalDate getBorrowedDate() {
        return borrowedDate;
    }

    public LocalDate getReturnDate() {
        return returnDate;
    }

    public String getStatus() {
        if (returnDate.isBefore(LocalDate.now())) {
            return "Overdue";
        } else {
            return "Borrowing";
        }
    }



    public String getTitle() {
        return title;
    }
}
