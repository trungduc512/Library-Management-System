package Model;

import classes.DatabaseHelper;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
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
    String sql = "INSERT INTO Books (title, author, isbn, description, totalBooks, borrowedBooks, thumbnailURL) VALUES (?, ?, ?, ?, ?, ?, ?)";
    try (Connection conn = DatabaseHelper.getConnection();
         PreparedStatement stmt = conn.prepareStatement(sql)) {

      stmt.setString(1, book.getTitle());
      stmt.setString(2, book.getAuthor());
      stmt.setString(3, book.getIsbn());
      stmt.setString(4, book.getDescription());
      stmt.setInt(5, book.getTotalBooks());
      stmt.setInt(6, book.getBorrowedBooks());
      stmt.setString(7, book.getThumbnailURL());
      stmt.executeUpdate();
    } catch (SQLException e) {
      e.printStackTrace();
    }
  }

  // Hàm cập nhật sách trong database
  public void updateTotalBook(Book book) {
    String sql = "UPDATE Books SET totalBooks = ? WHERE isbn = ?";
    try (Connection conn = DatabaseHelper.getConnection();
        PreparedStatement stmt = conn.prepareStatement(sql)) {

      stmt.setInt(1, book.getTotalBooks());
      stmt.setString(2, book.getIsbn());
      stmt.executeUpdate();
      System.out.println("Update book successfully.");
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
      System.out.println("Delete book successfully");
    } catch (SQLException e) {
      e.printStackTrace();
    }
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

  // Hàm sửa số lượng sách
  public void updateBook(String isbn,
      int increaseQuantity) {
    Book book = User.getBookByIsbn(isbn);
    if (book != null) {
      book.setTotalBooks(
          book.getTotalBooks() + increaseQuantity); // Cập nhật thông tin sách vào cơ sở dữ liệu
      // Cập nhật thông tin sách vào cơ sở dữ liệu
      updateTotalBook(book);
      System.out.println("Updated book: " + book.getTitle());
    } else {
      System.out.println("Book not found.");
    }
  }

  // Trả về lượng sách có thể xóa của sách đó
  public int getDeleteQuantity(String isbn) {
    Book book = User.getBookByIsbn(isbn);
    if (book != null) {
      return book.getTotalBooks() - book.getBorrowedBooks();
    } else {
      System.out.println("Book not found.");
    }
    return 0;
  }

  // Hàm giảm số lượng sách
  public void decreaseBook(String isbn, int decreaseQuantity) {
    Book book = User.getBookByIsbn(isbn);
    if (book != null) {
      book.setTotalBooks(
          book.getTotalBooks() - decreaseQuantity); // Cập nhật thông tin sách vào cơ sở dữ liệu
      // Cập nhật thông tin sách vào cơ sở dữ liệu
      updateTotalBook(book);
      System.out.println("Updated book: " + book.getTitle());
    } else {
      System.out.println("Book not found.");
    }
  }

  // Hàm xóa sách
  public void deleteBook(String isbn) {
    Book book = User.getBookByIsbn(isbn);
    if (book != null) {
      deleteBook(book.getIsbn()); // Xóa sách khỏi cơ sở dữ liệu
      System.out.println("Deleted book: " + book.getTitle());
    } else {
      System.out.println("Book not found.");
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
        String fullName = rs.getString("fullName");
        String userName = rs.getString("userName");
        String password = rs.getString("password");
        borrowers.add(new Borrower(id, fullName, userName, password));
        System.out.println("ID: " + id + ", full name: " + fullName + ", user name: " + userName);
      }
    } catch (SQLException e) {
      e.printStackTrace();
    }
    return borrowers;
  }

  // Hàmm returnStatusBorrower trả về trạng thái người mượn sách
  public String returnStatus(int borrowerId) {
    String sql = "SELECT returnDate FROM BorrowedBookRecord WHERE borrowerId = ?";
    LocalDate today = LocalDate.now();
    boolean hasBorrowed = false;
    boolean hasOverdue = false;

    try (Connection conn = DatabaseHelper.getConnection();
        PreparedStatement stmt = conn.prepareStatement(sql)) {
      stmt.setInt(1, borrowerId);
      ResultSet rs = stmt.executeQuery();

      while (rs.next()) {
        hasBorrowed = true;
        LocalDate returnDate = rs.getDate("returnDate").toLocalDate();
        if (returnDate.isBefore(today)) {
          hasOverdue = true;
        }
      }
    } catch (SQLException e) {
      e.printStackTrace();
    }

    if (!hasBorrowed) {
      return "No Borrowed";
    }
    if (hasOverdue) {
      return "Overdue";
    }
    return "Good Borrower";
  }

  // Hàm đăng ký (register)
  public static boolean register(String fullName, String userName, String password) {
    // Kiểm tra nếu tên tài khoản đã tồn tại
    if (Librarian.userExists(userName)) {
      System.out.println("User already exists.");
      return false;
    }

    String hashedPassword = hashPassword(password);  // Mã hóa mật khẩu

    String sql = "INSERT INTO Librarians (fullName, userName, password) VALUES (?, ?, ?)";
    try (Connection conn = DatabaseHelper.getConnection();
        PreparedStatement stmt = conn.prepareStatement(sql,
            PreparedStatement.RETURN_GENERATED_KEYS)) {
      stmt.setString(1, fullName);
      stmt.setString(2, userName);
      stmt.setString(3, password);
      stmt.executeUpdate();
      System.out.println("Register successfully.");
      return true;
    } catch (SQLException e) {
      e.printStackTrace();
    }
    return false;
  }

  // Hàm kiểm tra tên tài khoản đã tồn tại chưa
  public static boolean userExists(String userName) {
    String sql = "SELECT * FROM Librarians  WHERE userName = ?";
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
