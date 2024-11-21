package services;

import dao.BookReviewDAO;
import org.controlsfx.control.tableview2.filter.filtereditor.SouthFilter;
import org.json.simple.JSONObject;
import org.json.simple.JSONArray;
import org.json.simple.parser.JSONParser;

import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Objects;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) throws Exception {
        String isbn = "9781525950858";
        GoogleBooksAPIClient apiClient = new GoogleBooksAPIClient(isbn);
        ArrayList<String> authorList = apiClient.getAuthors();
        String title = apiClient.getTitle();
        String description = apiClient.getDescription();
        String publisher = apiClient.getPublisher();
        String publishedDate = apiClient.getPublishedDate();
        System.out.println("Title:");
        System.out.println(title);
        System.out.println("Authors:");
        for (String author : authorList) {
            System.out.println(author);
        }
        System.out.println("Publisher:");
        System.out.println(publisher);
        System.out.println("publishedDate:");
        System.out.println(publishedDate);
        System.out.println("Description:");
        System.out.println(description);
        System.out.println("ISBN: ");
        System.out.println(apiClient.getISBN());

        BookReviewDAO d = new BookReviewDAO();
        d.addReview(2, "9780760349632", 4, "ass", "silly",
                Timestamp.valueOf(LocalDateTime.now()));
        d.addReview(1, "9780760349632", 5, "hehe", "this is a masterpiece",
                Timestamp.valueOf(LocalDateTime.now()));
        d.addReview(3, "9780760349632", 4, "dangzz", "this is a alo",
                Timestamp.valueOf(LocalDateTime.now()));
    }
}


