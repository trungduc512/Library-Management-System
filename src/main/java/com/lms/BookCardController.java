package com.lms;

import classes.Book;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import classes.Book;
import javafx.scene.layout.HBox;

import java.util.Objects;

public class BookCardController {

    @FXML
    private HBox box;

    @FXML
    private Label bookAuthor;

    @FXML
    private Label bookAvailability;

    @FXML
    private Label bookISBN;

    @FXML
    private ImageView bookImage;

    @FXML
    private Label bookTitle;

    private Book book;

    public void setData(Book book) {
        this.book = book;
        Image image = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/com/lms/images/book_cover.jpeg")));
        bookImage.setImage(image);

        bookTitle.setText(book.getTitle());
        bookAuthor.setText(book.getAuthor());
        bookISBN.setText("ISBN: " + book.getIsbn());
        bookAvailability.setText("Availability: " + Integer.toString(book.getTotalBooks()));
    }

    @FXML
    public void handleCardClick() {
        System.out.println("Clicked on: " + book.getTitle());
    }
}
