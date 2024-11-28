package Model;

import services.LibrarianService;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a Librarian, a user who can manage books and theses in the library system.
 * The Librarian class extends the User class and provides additional functionality for adding, updating, deleting,
 * and managing documents (books and theses) in the system. It also provides methods for listing borrowers and
 * checking their borrowing status.
 */
public class Librarian extends User {
    private List<Document> documents;

    // Constructor
    public Librarian() {
    }

    public Librarian(int id, String fullName, String userName, String password) {
        super(id, fullName, userName, password);
        this.documents = new ArrayList<>();
    }

    @Override
    public boolean login(String userName, String password) {
        Librarian librarian = LibrarianService.login(userName, password);
        if (librarian != null) {
            System.out.println("Login successfully.");
            this.id = librarian.id;
            this.fullName = librarian.fullName;
            this.userName = librarian.userName;
            this.password = librarian.password;

            return true;
        }
        System.out.println("Login failed.");
        return false;
    }

    /**
     * Saves a book to the database.
     *
     * @param book the book to be saved.
     */
    public void save(Book book) {
        LibrarianService.saveBook(book);
    }

    /**
     * Saves a thesis to the database.
     *
     * @param thesis the thesis to be saved.
     */
    public void save(Thesis thesis) {
        LibrarianService.saveThesis(thesis);
    }

    public void updateTotalBook(Book book) {
        LibrarianService.updateTotalBook(book);
    }

    public void updateTotalThesis(Thesis thesis) {
        LibrarianService.updateTotalThesis(thesis);
    }

    public void delete(String isbn) {
        LibrarianService.deleteBook(isbn);
    }

    /**
     * Adds a book to the library system.
     * If the book already exists, updates its quantity.
     *
     * @param title        the title of the book.
     * @param author       the author of the book.
     * @param isbn         the ISBN of the book.
     * @param description  the description of the book.
     * @param totalBooks   the total quantity of the book.
     * @param thumbnailURL the URL of the book's thumbnail image.
     */
    public void addBook(String title, String author, String isbn, String description,
                        int totalBooks, String thumbnailURL) {
        Book FindBook = User.getBookByIsbn(isbn);
        if (FindBook != null) {
            System.out.println("Book already exists");
            updateBook(isbn, totalBooks);
            return;
        }
        Book newBook = new Book(title, author, isbn, description, totalBooks, 0, thumbnailURL);
        save(newBook); // Lưu vào cơ sở dữ liệu
        System.out.println("Added book: " + title + " successfully.");
    }

    /**
     * Adds a thesis to the library system.
     * If the thesis already exists, updates its quantity.
     *
     * @param title        the title of the thesis.
     * @param author       the author of the thesis.
     * @param university   the university where the thesis was submitted.
     * @param description  the description of the thesis.
     * @param totalTheses  the total quantity of the thesis.
     * @param thumbnailURL the URL of the thesis's thumbnail image.
     */
    public void addThesis(String title, String author, String university, String description,
                          int totalTheses, String thumbnailURL) {
        ArrayList<Thesis> list = User.searchThesisByTitle(title);
        for (int i = 0; i < list.size(); i++) {
            Thesis FindThesis = list.get(i);
            if (FindThesis != null && FindThesis.getAuthor().equals(author)
                    && FindThesis.getUniversity().equals(university)
                    && FindThesis.getTitle().equals(title)) {
                updateThesis(FindThesis.getId(), totalTheses);
                return;
            }
        }

        Thesis newThesis = new Thesis(title, author, description, totalTheses, 0, thumbnailURL, university);
        save(newThesis); // Lưu vào cơ sở dữ liệu
        System.out.println("Added thesis: " + title + " successfully.");
    }

    /**
     * Updates the quantity of a book based on ISBN.
     *
     * @param isbn          the ISBN of the book.
     * @param increaseQuantity the quantity to increase.
     */
    public void updateBook(String isbn,
                           int increaseQuantity) {
        Book book = User.getBookByIsbn(isbn);
        if (book != null) {
            book.setTotalDocument(
                    book.getTotalDocument() + increaseQuantity);
            // Cập nhật thông tin sách vào cơ sở dữ liệu
            updateTotalBook(book);
            System.out.println("Updated book: " + book.getTitle());
        } else {
            System.out.println("Book not found.");
        }
    }

    /**
     * Updates the quantity of a thesis based on its ID.
     *
     * @param id             the ID of the thesis.
     * @param increaseQuantity the quantity to increase.
     */
    public void updateThesis(long id, int increaseQuantity) {
        Thesis thesis = User.getThesisById(id);
        if (thesis != null) {
            thesis.setTotalDocument(
                    thesis.getTotalDocument() + increaseQuantity);
            // Cập nhật thông tin sách vào cơ sở dữ liệu
            updateTotalThesis(thesis);
        }
    }

    /**
     * Returns the quantity of books that can be deleted based on ISBN.
     *
     * @param isbn the ISBN of the book.
     * @return the quantity of books that can be deleted.
     */
    public int getDeleteQuantity(String isbn) {
        Book book = User.getBookByIsbn(isbn);
        if (book != null) {
            return book.getTotalDocument() - book.getBorrowedDocument();
        } else {
            System.out.println("Book not found.");
        }
        return 0;
    }

    /**
     * Returns the quantity of theses that can be deleted based on ID.
     *
     * @param id the ID of the thesis.
     * @return the quantity of theses that can be deleted.
     */
    public int getDeleteQuantity(long id) {
        Thesis thesis = User.getThesisById(id);
        if (thesis != null) {
            return thesis.getTotalDocument() - thesis.getBorrowedDocument();
        } else {
            System.out.println("Thesis not found.");
        }
        return 0;
    }

    /**
     * Decreases the quantity of a book based on ISBN.
     *
     * @param isbn          the ISBN of the book.
     * @param decreaseQuantity the quantity to decrease.
     */
    public void decreaseBook(String isbn, int decreaseQuantity) {
        Book book = User.getBookByIsbn(isbn);
        if (book != null) {
            book.setTotalDocument(
                    book.getTotalDocument() - decreaseQuantity);
            // Cập nhật thông tin sách vào cơ sở dữ liệu
            updateTotalBook(book);
            System.out.println("Updated book: " + book.getTitle());
        } else {
            System.out.println("Book not found.");
        }
    }

    /**
     * Decreases the quantity of a thesis based on ID.
     *
     * @param id             the ID of the thesis.
     * @param decreaseQuantity the quantity to decrease.
     */
    public void decreaseThesis(long id, int decreaseQuantity) {
        Thesis thesis = User.getThesisById(id);

        if (thesis != null) {
            thesis.setTotalDocument(
                    thesis.getTotalDocument() - decreaseQuantity);
            // Cập nhật thông tin sách vào cơ sở dữ liệu
            updateTotalThesis(thesis);
            System.out.println("Updated thesis: " + thesis.getTitle());
        } else {
            System.out.println("Thesis not found.");
        }
    }

    public void deleteBook(String isbn) {
        LibrarianService.deleteBook(isbn);
    }

    public static List<Borrower> listBorrowers() {
        return LibrarianService.listBorrowers();
    }

    public String returnStatus(int borrowerId) {
        return LibrarianService.returnStatus(borrowerId);
    }

    public static boolean register(String fullName, String userName, String password) {
        return LibrarianService.register(fullName, userName, password);
    }
}

