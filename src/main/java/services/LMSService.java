package services;

import Model.Book;
import Model.Borrower;
import Model.Librarian;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class LMSService {

    public static List<Borrower> setBorrowerList() {
        List<Borrower> borrowers = new ArrayList<>();
        String sql = "SELECT * FROM Borrowers";
        try {
            Connection conn = DatabaseHelper.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                int id = rs.getInt("id");
                String fullName = rs.getString("fullName");
                String userName = rs.getString("userName");
                String password = rs.getString("password");
                borrowers.add(new Borrower(id, fullName, userName, password));
                System.out.println("ID: " + id + ", fullname: " + fullName + ", username: " + userName);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return borrowers;
    }

    public static List<Book> setBookList() {
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
                String thumbnailURL = rs.getString("thumbnailURL");
                int totalBooks = rs.getInt("totalBooks");
                int borrowedBooks = rs.getInt("borrowedBooks");

                books.add(new Book(title, author, isbn, description, totalBooks, borrowedBooks, thumbnailURL));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return books;
    }

    public static List<Librarian> setLibrarianList() {
        List<Librarian> librarians = new ArrayList<>();
        String sql = "SELECT * FROM Librarians";

        try (Connection conn = DatabaseHelper.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                int id = rs.getInt("id");
                String fullName = rs.getString("fullName");
                String userName = rs.getString("userName");
                String password = rs.getString("password");
                librarians.add(new Librarian(id, fullName, userName, password));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return librarians;
    }
}
