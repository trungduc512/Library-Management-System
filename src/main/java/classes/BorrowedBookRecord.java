package classes;

import java.time.LocalDate;

public class BorrowedBookRecord {

    private int borrowerId;
    private String documentId;
    private int quantity;
    private LocalDate borrowedDate;
    private LocalDate returnDate;
    private String title;
    private String type;
    private String status;

    // Constructor
    public BorrowedBookRecord(int borrowerId, String title, String documentId, int quantity,
                              LocalDate borrowedDate, LocalDate returnDate, String type) {
        this.borrowerId = borrowerId;
        this.documentId = documentId;
        this.quantity = quantity;
        this.borrowedDate = borrowedDate;
        this.returnDate = returnDate;
        this.title = title;
        this.type = type;
    }

    // Getters và Setters
    public int getBorrowerId() {
        return borrowerId;
    }

    public void setBorrowerId(int borrowerId) {
        this.borrowerId = borrowerId;
    }

    public String getDocumentId() {
        return documentId;
    }

    public void setDocumentId(String documentId) {
        this.documentId = documentId;
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

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
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
