package services;

import Model.Borrower;
import Model.Librarian;
import Model.User;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class AuthenticationService {

    public static User loginBorrower(String userName, String password) {
        String sql = "SELECT * FROM Borrowers WHERE userName = ?";
        try (Connection conn = DatabaseHelper.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, userName);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                String storedPassword = rs.getString("password");

                if (password.equals(storedPassword)) {
                    int userId = rs.getInt("id");
                    String fullName = rs.getString("fullName");
                    System.out.println("Login successfully.");
                    return new Borrower(userId, fullName, userName, storedPassword);
                } else {
                    System.out.println("Wrong password.");
                }
            } else {
                System.out.println("User not exists.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static User loginLibrarian(String userName, String password) {
        String sql = "SELECT * FROM Librarians WHERE userName = ?";
        try (Connection conn = DatabaseHelper.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, userName);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                String storedPassword = rs.getString("password");

                if (password.equals(storedPassword)) {
                    int userId = rs.getInt("id");
                    String fullName = rs.getString("userName");
                    System.out.println("Login successfully.");
                    return new Librarian(userId, fullName, userName, storedPassword);
                } else {
                    System.out.println("Wrong password.");
                }
            } else {
                System.out.println("User not exists.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static boolean userExists(String userName) {
        String sql = "SELECT * FROM Borrowers  WHERE userName = ?";
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

    public static boolean registerUser(String fullName, String userName, String password) {
        if (userExists(userName)) {
            System.out.println("User already exists");
            return false;
        }

        String sql = "INSERT INTO Borrowers (fullName, userName, password) VALUES (?, ?, ?)";
        try (Connection conn = DatabaseHelper.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql,
                     PreparedStatement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, fullName);
            stmt.setString(2, userName);
            stmt.setString(3, password);
            stmt.executeUpdate();
            System.out.println("Register successfully!");

            return true;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
}
