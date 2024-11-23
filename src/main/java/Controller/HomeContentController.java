package Controller;

import Model.Book;
import Model.Borrower;
import Model.LMS;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.chart.PieChart;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.util.List;

public class HomeContentController {

    private MenuController menuController;

    @FXML
    private VBox borrowerList;

    @FXML
    private PieChart pieChart;

    @FXML
    private Label statisticLabel;

    @FXML
    private HBox bookContainer;

    public void setMenuController(MenuController menuController) {
        this.menuController = menuController;
    }

    @FXML
    public void initialize() {
        List<Borrower> topBorrowerList = LMS.getInstance().getCurrentUser().getTopBorrowers();
        int i = 0;
        for (Borrower borrower : topBorrowerList) {
            Label borrowerLabel = new Label(String.valueOf(++i) + ". " + borrower.getFullName());
            borrowerLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #000000;");
            borrowerList.getChildren().add(borrowerLabel);
        }

        int totalBook = LMS.getInstance().getCurrentUser().getTotalBooks();
        int totalBorrowedBooks = LMS.getInstance().getCurrentUser().getTotalBorrowedBooks();
        double borrowedPercentage = (double) totalBorrowedBooks / totalBook * 100;
        statisticLabel.setText(totalBorrowedBooks + " out of " + totalBook + " books");
        // Populate Pie Chart
        pieChart.getData().addAll(
                new PieChart.Data("Borrowed books", borrowedPercentage),
                new PieChart.Data("In stock", 100 - borrowedPercentage)
        );
        List<Book> popularBooks = LMS.getInstance().getCurrentUser().listTopBorrowedBooks();
        for (Book book : popularBooks) {
            bookContainer.getChildren().add(createBookZone(book));
        }
    }

    /**
     * Creates a fixed-size VBox for a single book with image, title, and ISBN.
     */
    private VBox createBookZone(Book book) {
        // Create the container
        VBox bookZone = new VBox(5); // Spacing of 5 between elements
        bookZone.setAlignment(Pos.CENTER);
        bookZone.setPrefSize(150, 200); // Fixed size
        bookZone.getStyleClass().add("popular-item");
        bookZone.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2) {
                handleBookClick(book);
            }
        }); // Handle click event

        // Create the image
        ImageView bookImage = new ImageView(new Image(book.getThumbnailURL()));
        bookImage.setFitWidth(100);
        bookImage.setFitHeight(120);
        bookImage.setPreserveRatio(true);

        // Create the title
        Label bookTitle = new Label(book.getTitle());
        bookTitle.getStyleClass().add("popular-title");

        // Create the ISBN
        Label bookISBN = new Label("ISBN: " + book.getIsbn());
        bookISBN.getStyleClass().add("popular-isbn");

        // Add elements to the container
        bookZone.getChildren().addAll(bookImage, bookTitle, bookISBN);

        return bookZone;
    }

    /**
     * Handles the click event for a book zone.
     */
    private void handleBookClick(Book book) {
        menuController.toSearchScreen(book.getIsbn());
    }
}
