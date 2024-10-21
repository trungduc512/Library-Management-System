package classes;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class Librarian extends User {

    private List<Book> books;

    // Constructor
    private Librarian() {
    }

    public Librarian(int id, String fullName, String userName, String password) {
        super(id, fullName, userName, password);
        this.books = new ArrayList<>();
    }

    // Hàm lưu sách vào database
    public void save(Book book) {
        String sql = "INSERT INTO Books (title, author, isbn, description, total_books, borrowed_books) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseHelper.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, book.getTitle());
            stmt.setString(2, book.getAuthor());
            stmt.setString(3, book.getIsbn());
            stmt.setString(4, book.getDescription());
            stmt.setInt(5, book.getTotalBooks());
            stmt.setInt(6, book.getBorrowedBooks());  // Thêm borrowed_books
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Hàm cập nhật sách trong database
    public void updateTotalBook(Book book) {
        String sql = "UPDATE Books SET  total_books = ? WHERE isbn = ?";
        try (Connection conn = DatabaseHelper.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, book.getTotalBooks());
            stmt.setString(2, book.getIsbn());
            stmt.executeUpdate();
            System.out.println("Cập nhật sách thành công");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Hàm xóa sách khỏi database
    public void delete(String isbn) {
        String sql = "DELETE FROM Books WHERE isbn = ?";
        try (Connection conn = DatabaseHelper.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, isbn);
            stmt.executeUpdate();
            System.out.println("Xóa sách thành công");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Hàm thêm sách
    public void addBook(String title, String author, String isbn, String description,
                        int totalBooks) {
        Book FindBook = User.getBookByIsbn(isbn);
        if (FindBook != null) {
            System.out.println("Sách đã có sẵn trong thư viện");
            return;
        }
        Book newBook = new Book(title, author, isbn, description, totalBooks, 0);
        save(newBook); // Lưu vào cơ sở dữ liệu
        System.out.println("Thêm sách với tiêu đề: " + title + "thành công");

    }

    // Hàm sửa số lượng sách
    public void updateBook(String isbn,
                           int incretotalBooks) {
        Book book = User.getBookByIsbn(isbn);
        if (book != null) {
            book.setTotalBooks(
                    book.getTotalBooks() + incretotalBooks); // Cập nhật thông tin sách vào cơ sở dữ liệu
            // Cập nhật thông tin sách vào cơ sở dữ liệu
            updateTotalBook(book);
            System.out.println("Updated book: " + book.getTitle());
        } else {
            System.out.println("classes.Book not found.");
        }
    }

    // Hàm xóa sách
    public void deleteBook(String isbn) {
        Book book = User.getBookByIsbn(isbn);
        if (book != null) {
            deleteBook(book.getIsbn()); // Xóa sách khỏi cơ sở dữ liệu
            System.out.println("Deleted book: " + book.getTitle());
        } else {
            System.out.println("classes.Book not found.");
        }
    }

    // Hàm liệt kê danh sách borrower
    public static List<Borrower> listBorrowers() {
        List<Borrower> borrowers = new ArrayList<>();
        String sql = "SELECT * FROM Borrowers";
        try (Connection conn = DatabaseHelper.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                int id = rs.getInt("id");
                String fullName = rs.getString("ho_ten");
                String userName = rs.getString("ten_tai_khoan");
                String password = rs.getString("password");
                borrowers.add(new Borrower(id, fullName, userName, password));
                System.out.println("ID: " + id + ", Họ tên: " + fullName + ", Tên tài khoản: " + userName);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return borrowers;
    }

    // Hàm đăng ký (register)
    public static boolean register(String fullName, String userName, String password) {
        // Kiểm tra nếu tên tài khoản đã tồn tại
        if (Librarian.userExists(userName)) {
            System.out.println("Tên tài khoản đã tồn tại");
            return false;
        }

        String hashedPassword = hashPassword(password);  // Mã hóa mật khẩu

        String sql = "INSERT INTO Librarians (ho_ten, ten_tai_khoan, password) VALUES (?, ?, ?)";
        try (Connection conn = DatabaseHelper.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql,
                     PreparedStatement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, fullName);
            stmt.setString(2, userName);
            stmt.setString(3, password);
            stmt.executeUpdate();
            System.out.println("Đăng ký thành công");
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // Hàm kiểm tra tên tài khoản đã tồn tại chưa
    public static boolean userExists(String userName) {
        String sql = "SELECT * FROM Librarians  WHERE ten_tai_khoan = ?";
        try (Connection conn = DatabaseHelper.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, userName);
            ResultSet rs = stmt.executeQuery();
            return rs.next();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // Mã hóa mật khẩu (hash password)
    private static String hashPassword(String password) {
        // Ở đây bạn có thể sử dụng thư viện mã hóa mạnh như BCrypt hoặc SHA-256
        return password;  // Trong ví dụ này, trả về mật khẩu như cũ (cần thay bằng mã hóa thực sự)
    }
}

