package Model;

import services.AuthenticationService;
import services.LMSService;
import services.UserService;

import java.util.List;

public class LMS {
    private List<Borrower> borrowerList;
    private List<Book> bookList;
    private User currentUser;

    private static LMS instance = null;

    private LMS() {
        borrowerList = null;
        bookList = null;
        currentUser = null;
    }

    public static LMS getInstance() {
        if (instance == null) {
            instance = new LMS();
        }
        return instance;
    }

    public User getCurrentUser() {
        return currentUser;
    }

    public List<Borrower> getBorrowerList() {
        if (this.borrowerList == null) {
            this.setBorrowerList();
        }
        return borrowerList;
    }

    public List<Book> getBookList() {
        setBookList();
        return bookList;
    }

    private void setBorrowerList() {
        borrowerList = LMSService.getBorrowerList();
    }

    public void setBookList() {
        this.bookList = UserService.getAllBooks();
    }

    public boolean loginBorrower(String userName, String password) {
        currentUser = AuthenticationService.loginBorrower(userName, password);
        return currentUser != null;
    }

    public boolean loginLibrarian(String userName, String password) {
        currentUser = AuthenticationService.loginLibrarian(userName, password);
        return currentUser != null;
    }

    public void logoutCurrentUser() {
        currentUser = null;
    }
}
