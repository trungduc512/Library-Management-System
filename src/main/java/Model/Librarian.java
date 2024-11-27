package Model;

import services.LibrarianService;

import java.util.ArrayList;
import java.util.List;

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

    // Hàm lưu sách vào database
    public void save(Book book) {
        LibrarianService.saveBook(book);
    }

    public void save(Thesis thesis) {
        LibrarianService.saveThesis(thesis);
    }

    // Hàm cập nhật sách trong database
    public void updateTotalBook(Book book) {
        LibrarianService.updateTotalBook(book);
    }

    // Hàm cạp nhật luận văn
    public void updateTotalThesis(Thesis thesis) {
        LibrarianService.updateTotalThesis(thesis);
    }

    // Hàm xóa sách khỏi database
    public void delete(String isbn) {
        LibrarianService.deleteBook(isbn);
    }

    // Hàm thêm sách
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

    // Hàm sửa số lượng sách
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

    public void updateThesis(long id, int increaseQuantity) {
        Thesis thesis = User.getThesisById(id);
        if (thesis != null) {
            thesis.setTotalDocument(
                    thesis.getTotalDocument() + increaseQuantity);
            // Cập nhật thông tin sách vào cơ sở dữ liệu
            updateTotalThesis(thesis);
        }
    }

    // Trả về lượng sách có thể xóa của sách đó
    public int getDeleteQuantity(String isbn) {
        Book book = User.getBookByIsbn(isbn);
        if (book != null) {
            return book.getTotalDocument() - book.getBorrowedDocument();
        } else {
            System.out.println("Book not found.");
        }
        return 0;
    }

    public int getDeleteQuantity(long id) {
        Thesis thesis = User.getThesisById(id);
        if (thesis != null) {
            return thesis.getTotalDocument() - thesis.getBorrowedDocument();
        } else {
            System.out.println("Thesis not found.");
        }
        return 0;
    }

    // Hàm giảm số lượng sách
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

    // Hàm xóa sách
    public void deleteBook(String isbn) {
        LibrarianService.deleteBook(isbn);
    }

    // Hàm liệt kê danh sách borrower
    public static List<Borrower> listBorrowers() {
        return LibrarianService.listBorrowers();
    }

    // Hàm returnStatusBorrower trả về trạng thái người mượn sách
    public String returnStatus(int borrowerId) {
        return LibrarianService.returnStatus(borrowerId);
    }

    // Hàm đăng ký
    public static boolean register(String fullName, String userName, String password) {
        return LibrarianService.register(fullName, userName, password);
    }
}

