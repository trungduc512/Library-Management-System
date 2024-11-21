package com.lms;

import classes.Book;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;

import java.util.Objects;

public class BookCardController {

    @FXML
    private VBox box;

    @FXML
    private Label bookAuthor;

    @FXML
    private Label bookISBN;

    @FXML
    private ImageView bookImage;

    @FXML
    private Label bookTitle;

    private Book book;

    public void setData(Book book) {
        this.book = book;

        try {
            //bookImage.setImage(new Image("http://books.google.com/books/content?id=nDcGAQAAIAAJ&printsec=frontcover&img=1&zoom=1&source=gbs_api"));
            String url;
            if (book.getThumbnailURL() != null) {
                url = book.getThumbnailURL();
            } else {
                url = "/com/lms/Images/no_cover_available.png";
            }
            bookImage.setImage(new Image(Objects.requireNonNull(getClass().getResourceAsStream(url))));

            bookTitle.setText(book.getTitle());
            bookTitle.setWrapText(true);
            bookTitle.setMaxWidth(200);

            bookAuthor.setText(book.getAuthor());
            bookISBN.setText("ISBN: " + book.getIsbn());

        } catch (Exception e) {
            e.printStackTrace();

            bookTitle.setText(book.getTitle());
            bookAuthor.setText(book.getAuthor());
            bookISBN.setText("ISBN: " + book.getIsbn());
        }
    }

    // hàm chuyển sang isbn search khi nhấn vào book card
    @FXML
    public void handleCardClick() {
        System.out.println("Clicked on: " + book.getTitle());
    }
}
