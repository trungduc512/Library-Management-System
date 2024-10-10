package com.lms;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;


public class User {

  private int id;
  private String hoTen;       // Họ tên người dùng
  private String tenTaiKhoan;  // Tên tài khoản (username)
  private String password;

  // Constructor
  protected User() {
  }

  protected User(int id, String hoTen, String tenTaiKhoan, String password) {
    this.id = id;
    this.hoTen = hoTen;
    this.tenTaiKhoan = tenTaiKhoan;
    this.password = password;
  }

  // Getters và Setters
  public int getId() {
    return id;
  }

  public void setId(int id) {
    this.id = id;
  }

  public String getHoTen() {
    return hoTen;
  }

  public void setHoTen(String hoTen) {
    this.hoTen = hoTen;
  }

  public String getTenTaiKhoan() {
    return tenTaiKhoan;
  }

  public void setTenTaiKhoan(String tenTaiKhoan) {
    this.tenTaiKhoan = tenTaiKhoan;
  }

  public String getPassword() {
    return password;
  }

  public void setPassword(String password) {
    this.password = password;
  }

  //Hàm tìm sách theo tên
  public static List<Book> searchBooksByTitle(String title) {
    List<Book> books = new ArrayList<>();
    String sql = "SELECT * FROM Books WHERE title LIKE ? LIMIT 5"; // Giới hạn kết quả 5 cuốn
    try (Connection conn = DatabaseHelper.getConnection();
        PreparedStatement stmt = conn.prepareStatement(sql)) {
      stmt.setString(1, "%" + title + "%");
      ResultSet rs = stmt.executeQuery();
      while (rs.next()) {
        String bookTitle = rs.getString("title");
        String author = rs.getString("author");
        String isbn = rs.getString("isbn");
        String description = rs.getString("description");
        int totalBook = rs.getInt("total_books");
        int borrowedBook = rs.getInt("borrowed_books");
        books.add(new Book(bookTitle, author, isbn, description, totalBook, borrowedBook));
      }
    } catch (SQLException e) {
      e.printStackTrace();
    }
    return books;
  }

  // Hàm lấy danh sách sách từ cơ sở dữ liệu
  public static List<Book> getAllBooks() {
    List<Book> books = new ArrayList<>();
    String sql = "SELECT * FROM Books";
    try (Connection conn = DatabaseHelper.getConnection();
        PreparedStatement stmt = conn.prepareStatement(sql);
        ResultSet rs = stmt.executeQuery()) {
      while (rs.next()) {
        String title = rs.getString("title");
        String author = rs.getString("author");
        String isbn = rs.getString("isbn");
        String description = rs.getString("description");
        int totalBooks = rs.getInt("total_books");
        int borrowedBooks = rs.getInt("borrowed_books");
        books.add(new Book(title, author, isbn, description, totalBooks, borrowedBooks));
      }
    } catch (SQLException e) {
      e.printStackTrace();
    }
    return books;
  }

  // Hàm lấy thông tin sách từ ISBN
  public static Book getBookByIsbn(String isbn) {
    String sql = "SELECT * FROM Books WHERE isbn = ?";
    try (Connection conn = DatabaseHelper.getConnection();
        PreparedStatement stmt = conn.prepareStatement(sql)) {
      stmt.setString(1, isbn);
      ResultSet rs = stmt.executeQuery();
      if (rs.next()) {
        String title = rs.getString("title");
        String author = rs.getString("author");
        String description = rs.getString("description");
        int totalBooks = rs.getInt("total_books");
        int borrowedBooks = rs.getInt("borrowed_books");
        return new Book(title, author, isbn, description, totalBooks, borrowedBooks);
      }
    } catch (SQLException e) {
      e.printStackTrace();
    }
    return null;  // Nếu không tìm thấy sách
  }

}
//  // Hàm đăng ký (register)
//
//  public static boolean register(String hoTen, String tenTaiKhoan, String password) {
//    // Kiểm tra nếu tên tài khoản đã tồn tại
//    if (com.lms.User.userExists(tenTaiKhoan)) {
//      System.out.println("Tên tài khoản đã tồn tại");
//      return false;
//    }
//
//    String hashedPassword = hashPassword(password);  // Mã hóa mật khẩu
//
//    String sql = "INSERT INTO Users (ho_ten, ten_tai_khoan, password) VALUES (?, ?, ?)";
//    try (Connection conn = com.lms.DatabaseHelper.getConnection();
//        PreparedStatement stmt = conn.prepareStatement(sql,
//            PreparedStatement.RETURN_GENERATED_KEYS)) {
//      stmt.setString(1, hoTen);
//      stmt.setString(2, tenTaiKhoan);
//      stmt.setString(3, password);
//      stmt.executeUpdate();
//      System.out.println("Đăng ký thành công");
//      return true;
//    } catch (SQLException e) {
//      e.printStackTrace();
//    }
//    return false;
//  }
//
//  // Hàm đăng nhập (login)
//  public static com.lms.User login(String tenTaiKhoan, String password) {
//    String sql = "SELECT * FROM Users WHERE ten_tai_khoan = ?";
//    try (Connection conn = com.lms.DatabaseHelper.getConnection();
//        PreparedStatement stmt = conn.prepareStatement(sql)) {
//      stmt.setString(1, tenTaiKhoan);
//      ResultSet rs = stmt.executeQuery();
//
//      if (rs.next()) {
//        String storedPassword = rs.getString("password");
//
//        // Kiểm tra mật khẩu có khớp hay không
//        if (checkPassword(password, storedPassword)) {
//          int userId = rs.getInt("user_id");
//          String hoTen = rs.getString("ho_ten");
//          System.out.println("Đăng nhập thành công");
//          return new com.lms.User(userId, hoTen, tenTaiKhoan, storedPassword);  // Đăng nhập thành công
//        } else {
//          System.out.println("Sai mật khẩu");
//        }
//      } else {
//        System.out.println("Không tìm thấy người dùng");
//      }
//    } catch (SQLException e) {
//      e.printStackTrace();
//    }
//    return null;  // Đăng nhập thất bại
//  }
//
//  // Hàm đăng xuất (logout)
//  public static void logout(com.lms.User user) {
//    System.out.println("Người dùng " + user.getTenTaiKhoan() + " đã đăng xuất.");
//    // Thực hiện các hành động cần thiết khi đăng xuất (nếu cần)
//  }
//
//  // Hàm kiểm tra tên tài khoản đã tồn tại chưa
//  public static boolean userExists(String tenTaiKhoan) {
//    String sql = "SELECT * FROM Users WHERE ten_tai_khoan = ?";
//    try (Connection conn = com.lms.DatabaseHelper.getConnection();
//        PreparedStatement stmt = conn.prepareStatement(sql)) {
//      stmt.setString(1, tenTaiKhoan);
//      ResultSet rs = stmt.executeQuery();
//      return rs.next();
//    } catch (SQLException e) {
//      e.printStackTrace();
//    }
//    return false;
//  }
//
//  // Mã hóa mật khẩu (hash password)
//  private static String hashPassword(String password) {
//    // Ở đây bạn có thể sử dụng thư viện mã hóa mạnh như BCrypt hoặc SHA-256
//    return password;  // Trong ví dụ này, trả về mật khẩu như cũ (cần thay bằng mã hóa thực sự)
//  }
//
//  // Kiểm tra mật khẩu (check password)
//  private static boolean checkPassword(String password, String hashedPassword) {
//    // So sánh mật khẩu đã mã hóa
//    return password.equals(hashedPassword);  // Thay thế bằng hàm kiểm tra mật khẩu mã hóa thực sự
//  }
//}
