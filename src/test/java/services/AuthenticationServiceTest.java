package services;

import static org.junit.jupiter.api.Assertions.*;

import Model.Book;
import Model.Borrower;
import Model.Librarian;
import Model.Thesis;
import java.sql.Date;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class AuthenticationServiceTest {

  private static final String testFullName = "Test Librarian";
  private static final String testUsername = "librariantest";
  private static final String testPassword = "passwordtest";
  private Librarian librarian;
  private static final String testFullNameBorrower = "Test Borrower";
  private static final String testUsernameBorrower = "borrowertest";
  private static final String testPasswordBorrower = "passwordtest";
  private Borrower borrower;

  @BeforeEach
  void setUp() throws SQLException {
    String sqlInsert = "INSERT INTO Librarians (fullName, userName, password) VALUES (?, ?, ?)";
    try (Connection conn = DatabaseHelper.getConnection();
        PreparedStatement stmt = conn.prepareStatement(sqlInsert)) {
      stmt.setString(1, testFullName);
      stmt.setString(2, testUsername);
      stmt.setString(3, testPassword);
      stmt.executeUpdate();
    }
    librarian = new Librarian(1, testFullName, testUsername, testPassword);
    String sqlInsertBorrower = "INSERT INTO Borrowers (fullName, userName, password) VALUES (?, ?, ?)";
    try (Connection conn = DatabaseHelper.getConnection();
        PreparedStatement stmt = conn.prepareStatement(sqlInsertBorrower)) {
      stmt.setString(1, testFullNameBorrower);
      stmt.setString(2, testUsernameBorrower);
      stmt.setString(3, testPasswordBorrower);
      stmt.executeUpdate();
    }
    borrower = new Borrower(1, testFullNameBorrower, testUsernameBorrower, testPasswordBorrower);
  }

  @AfterEach
  void tearDown() throws SQLException {

    String sqlDelete = "DELETE FROM Librarians WHERE userName = ?";
    try (Connection conn = DatabaseHelper.getConnection();
        PreparedStatement stmt = conn.prepareStatement(sqlDelete)) {
      stmt.setString(1, testUsername);
      stmt.executeUpdate();
    }
    String sqlDeleteBorrower = "DELETE FROM Borrowers WHERE userName = ?";
    try (Connection conn = DatabaseHelper.getConnection();
        PreparedStatement stmt = conn.prepareStatement(sqlDeleteBorrower)) {
      stmt.setString(1, testUsernameBorrower);
      stmt.executeUpdate();
    }
  }

  @Test
  void testLoginLibrarianSuccess() {
    assertTrue(AuthenticationService.loginLibrarian(testUsername, testPassword) != null,
        "Login should succeed with valid credentials.");
  }

  @Test
  void testLoginLibrarianFailureWrongPassword() {
    assertNull(AuthenticationService.loginLibrarian(testUsername, "wrongpassword"),
        "Login should fail with invalid password.");
  }

  @Test
  void testLoginLibrarianFailureNonExistentUser() {
    assertNull(AuthenticationService.loginLibrarian("nonexistentuser", "anyPassword"),
        "Login should fail for non-existent user.");
  }

  @Test
  void testLoginLibrarianFailureEmptyUsername() {
    assertNull(AuthenticationService.loginLibrarian("", testPassword),
        "Login should fail with empty username.");
  }

  @Test
  void testLoginLibrarianFailureEmptyPassword() {
    assertNull(AuthenticationService.loginLibrarian(testUsername, ""),
        "Login should fail with empty password.");
  }

  @Test
  void testLoginBorrowerSuccess() {
    assertTrue(
        AuthenticationService.loginBorrower(testUsernameBorrower, testPasswordBorrower) != null,
        "Login should succeed with valid credentials.");
  }

  @Test
  void testLoginBorrowerFailureWrongPassword() {
    assertNull(AuthenticationService.loginBorrower(testUsernameBorrower, "wrongpassword"),
        "Login should fail with invalid password.");
  }

  @Test
  void testLoginBorrowerFailureNonExistentUser() {
    assertNull(AuthenticationService.loginBorrower("nonexistentuser", "anyPassword"),
        "Login should fail for non-existent user.");
  }

  @Test
  void testLoginBorrowerFailureEmptyUsername() {
    assertNull(AuthenticationService.loginBorrower("", testPasswordBorrower),
        "Login should fail with empty username.");
  }

  @Test
  void testLoginBorrowerFailureEmptyPassword() {
    assertNull(AuthenticationService.loginBorrower(testUsernameBorrower, ""),
        "Login should fail with empty password.");
  }

  @Test
  void testRegister() {
    String testFullName = "Test Librarian";
    String testUserName = "testUser";
    String testPassword = "testPassword";

    String deleteSql = "DELETE FROM borrowers WHERE userName = ?";

    try (Connection conn = DatabaseHelper.getConnection()) {

      try (PreparedStatement stmt = conn.prepareStatement(deleteSql)) {
        stmt.setString(1, testUserName);
        stmt.executeUpdate();
      }

      boolean registerResult = AuthenticationService.registerUser(testFullName, testUserName,
          testPassword);
      assertTrue(registerResult, "User registration should succeed.");

      boolean duplicateRegisterResult = AuthenticationService.registerUser(testFullName,
          testUserName,
          testPassword);
      assertFalse(duplicateRegisterResult, "Duplicate registration should fail.");

    } catch (SQLException e) {
      fail("Database error occurred during the test: " + e.getMessage());
    } finally {
      try (Connection conn = DatabaseHelper.getConnection();
          PreparedStatement stmt = conn.prepareStatement(deleteSql)) {
        stmt.setString(1, testUserName);
        stmt.executeUpdate();
      } catch (SQLException e) {
        e.printStackTrace();
      }
    }
  }

}