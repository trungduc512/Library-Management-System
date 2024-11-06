package classes;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class Borrower extends User {

    private List<BorrowedBookRecord> borrowedBooks;
    private List<Book> searchedBooks;

    // Constructor
    public Borrower(int id, String fullName, String userName, String password) {
        super(id, fullName, userName, password);
        this.borrowedBooks = new ArrayList<>();
        this.searchedBooks = new ArrayList<>();
    }

    // Hàm cập nhật hồ sơ
    public void updateProfile(String fullName) {
        this.setFullName(fullName);
        String sql = "UPDATE Users SET ho_ten = ? WHERE id = ?";
        try (Connection conn = DatabaseHelper.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, fullName);
            stmt.setInt(2, getId());
            stmt.executeUpdate();
            System.out.println("Profile updated in database.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    //incrementBorrowedBooks
    private void incrementBorrowedBooks(String isbn, int quantity) {
        Book book = User.getBookByIsbn(isbn);
        if (book != null) {
            String sql = "UPDATE Books SET borrowed_books = ? WHERE isbn = ?";
            try (Connection conn = DatabaseHelper.getConnection();
                 PreparedStatement stmt = conn.prepareStatement(sql)) {
                // Thêm borrowed_books
                stmt.setInt(1, book.getBorrowedBooks() + quantity);
                stmt.setString(2, isbn);
                stmt.executeUpdate();
                System.out.println("classes.Book updated in database.");
            } catch (SQLException e) {
                e.printStackTrace();
            }
        } else {
            System.out.println("Không update sách");
        }
    }

    //reduceBorrowedBooks
    private void reduceBorrowedBooks(String isbn, int quantity) {
        Book book = User.getBookByIsbn(isbn);
        if (book != null) {
            String sql = "UPDATE Books SET borrowed_books = ? WHERE isbn = ?";
            try (Connection conn = DatabaseHelper.getConnection();
                 PreparedStatement stmt = conn.prepareStatement(sql)) {
                // Trừ borrowed_books
                stmt.setInt(1, book.getBorrowedBooks() - quantity);
                stmt.setString(2, isbn);
                stmt.executeUpdate();
                System.out.println("classes.Book updated in database.");
            } catch (SQLException e) {
                e.printStackTrace();
            }
        } else {
            System.out.println("Không update sách");
        }
    }

    // Hàm tìm kiếm sách theo tiêu đề
    public void searchBooks(String title) {
        searchedBooks = User.searchBooksByTitle(title); // Tìm kiếm sách và lưu vào danh sách
        if (searchedBooks.isEmpty()) {
            System.out.println("Không tìm thấy sách nào với tiêu đề: " + title);
        } else {
            System.out.println("Kết quả tìm kiếm:");
            for (Book book : searchedBooks) {
                System.out.println(
                        book.getTitle() + " - " + book.getAuthor() + " (ISBN: " + book.getIsbn() + ")");
            }
        }
    }

    // Hàm mượn sách từ danh sách đã tìm kiếm
    // Kết hợp trong main lấy isbn từ bảng searchBook
    // Hàm mượn sách từ danh sách đã tìm kiếm
    public boolean borrowBookByIsbn(String isbn, int quantity) {
        // Tìm sách trong danh sách đã tìm kiếm
        Book book = getBookByIsbn(isbn);
        assert book != null;
        if (quantity <= book.getTotalBooks() - book.getBorrowedBooks()) {
            System.out.println("Mượn sách: " + book.getTitle());

            // Gọi hàm để thêm bản ghi mượn sách
            if (addBorrowedBookRecord(this.getId(), book.getTitle(), book.getIsbn(), quantity)) {
                // Cập nhật số lượng sách trong đối tượng classes.Book
                incrementBorrowedBooks(book.getIsbn(), quantity); // Tăng số lượng sách đã mượn
            }
            return true;
        } else {
            System.out.println("Không đủ sách để mượn. Tổng số sách có sẵn: " + book.getTotalBooks());
            return false;
        }
    }


    // Thêm bản ghi vào bảng classes.BorrowedBookRecord
    private boolean addBorrowedBookRecord(int borrowerId, String title, String isbn, int quantity) {
        String sql = "INSERT INTO BorrowedBookRecord (borrower_id, title, isbn, quantity, borrowed_date) "
                       + "VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseHelper.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, borrowerId);
            stmt.setString(2, title);
            stmt.setString(3, isbn);
            stmt.setInt(4, quantity);
            stmt.setDate(5, java.sql.Date.valueOf(LocalDate.now())); // Ngày hiện tại
            stmt.executeUpdate();
            System.out.println("Đã mượn sách và lưu vào cơ sở dữ liệu thành công!");
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // Hàm liệt kê các bản mượn
    public List<BorrowedBookRecord> listBorrowedBooks() {
        List<BorrowedBookRecord> records = new ArrayList<>();
        String sql = "SELECT * FROM BorrowedBookRecord WHERE borrower_id = ?";

        try (Connection conn = DatabaseHelper.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, this.getId());
            ResultSet rs = stmt.executeQuery();
            System.out.println("Sách đã mượn từ cơ sở dữ liệu:");

            while (rs.next()) {
                int recordId = rs.getInt("id"); // ID tự động tạo
                String title = rs.getString("title");
                String isbn = rs.getString("isbn");
                int quantity = rs.getInt("quantity");
                LocalDate borrowedDate = rs.getDate("borrowed_date").toLocalDate();
                records.add(new BorrowedBookRecord(recordId, title, isbn, quantity, borrowedDate));
                System.out.println(
                        "ID Bản ghi: " + recordId + ", ISBN: " + isbn + ", Số lượng: " + quantity
                                + ", Ngày mượn: "
                                + borrowedDate);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return records;
    }

    // Hàm trả sách
    public boolean returnBook(int recordId) {
        // Tìm bản ghi trong BorrowedBookRecord
        BorrowedBookRecord record = findBorrowedRecordById(recordId);
        if (record == null) {
            System.out.println("Không tìm thấy bản ghi mượn sách.");
            return false;
        }

        // Cập nhật lại số lượng sách đã mượn
        Book book = User.getBookByIsbn(record.getIsbn());
        if (book != null) {
            // Giảm số lượng sách đã được mượn
            reduceBorrowedBooks(book.getIsbn(), record.getQuantity());
        }

        // Xóa bản ghi
        return deleteBorrowedBookRecord(recordId);
    }

    // Hàm xóa bản ghi mượn sách khỏi BorrowedBookRecord
    private boolean deleteBorrowedBookRecord(int recordId) {
        String sql = "DELETE FROM BorrowedBookRecord WHERE id = ?";
        try (Connection conn = DatabaseHelper.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, recordId);
            stmt.executeUpdate();
            System.out.println("Trả sách thành công.");
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // Hàm tìm bản ghi mượn sách theo ID
    private BorrowedBookRecord findBorrowedRecordById(int recordId) {
        String sql = "SELECT * FROM BorrowedBookRecord WHERE id = ?";
        try (Connection conn = DatabaseHelper.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, recordId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                String title = rs.getString("title");
                String isbn = rs.getString("isbn");
                int quantity = rs.getInt("quantity");
                LocalDate borrowedDate = rs.getDate("borrowed_date").toLocalDate();
                return new BorrowedBookRecord(recordId, title, isbn, quantity, borrowedDate);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    // Hàm đăng ký (register)
    public static boolean register(String fullName, String userName, String password) {
        // Kiểm tra nếu tên tài khoản đã tồn tại
        if (Borrower.userExists(userName)) {
            System.out.println("Tên tài khoản đã tồn tại");
            return false;
        }

        String hashedPassword = hashPassword(password);  // Mã hóa mật khẩu

        String sql = "INSERT INTO Borrowers (ho_ten, ten_tai_khoan, password) VALUES (?, ?, ?)";
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
        String sql = "SELECT * FROM Borrowers  WHERE ten_tai_khoan = ?";
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


