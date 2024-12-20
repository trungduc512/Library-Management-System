package Model;

import java.util.Map;

/**
 * The Document class serves as an abstract representation of a document in the library system.
 * It contains common attributes such as title, author, description, thumbnail URL, and document quantities,
 * and provides methods to manage and retrieve document information.
 */
public abstract class Document {
    protected String title;
    protected String author;
    protected String description;
    protected String thumbnailURL;
    protected int totalDocument;
    protected int borrowedDocument;

    public Document() {
    }

    public Document(String title, String author, String description, String thumbnailURL, int totalDocument, int borrowedDocument) {
        this.title = title;
        this.author = author;
        this.description = description;
        this.thumbnailURL = thumbnailURL;
        this.totalDocument = totalDocument;
        this.borrowedDocument = borrowedDocument;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getThumbnailURL() {
        return thumbnailURL;
    }

    public void setThumbnailURL(String thumbnailURL) {
        this.thumbnailURL = thumbnailURL;
    }

    public int getTotalDocument() {
        return totalDocument;
    }

    public void setTotalDocument(int totalDocument) {
        this.totalDocument = totalDocument;
    }

    public int getBorrowedDocument() {
        return borrowedDocument;
    }

    public void setBorrowedDocument(int borrowedDocument) {
        this.borrowedDocument = borrowedDocument;
    }

    public abstract Map<String, String> getInfo();

    public void printDetails() {
        String details = "Document{" +
                "title='" + title + '\'' +
                ", author='" + author + '\'' +
                ", description='" + description + '\'' +
                ", thumbnailURL='" + thumbnailURL + '\'' +
                '}';
        System.out.println(details);
    }
}
