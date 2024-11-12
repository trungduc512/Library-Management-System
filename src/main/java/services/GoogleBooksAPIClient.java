package services;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class GoogleBooksAPIClient {
    private final JSONObject volumeInfo;

    public GoogleBooksAPIClient(String isbn) throws Exception {
        JSONArray itemsArray = getJsonArray(isbn);
        JSONObject itemsObject = (JSONObject) itemsArray.getFirst();
        volumeInfo = (JSONObject) itemsObject.get("volumeInfo");
    }

    public String getTitle() {
        return (String) volumeInfo.get("title");
    }

    public ArrayList<String> getAuthors() {
        JSONArray authorArray = (JSONArray) volumeInfo.get("authors");
        ArrayList<String> authorList = new ArrayList<>();
        if (authorArray == null) {
            authorList.add("N/A");
            return authorList;
        }
        for (Object object : authorArray) {
            String author = (String) object;
            authorList.add(author);
        }
        return authorList;
    }

    public String getPublisher() {
        String result = (String) volumeInfo.get("publisher");
        return (result != null) ? result : "N/A" ;
    }

    public String getPublishedDate() {
        return (String) volumeInfo.get("publishedDate");
    }

    public String getDescription() {
        String result = (String) volumeInfo.get("description");
        return (result != null) ? result : "N/A";
    }

    public String getThumbnailURL() {
        JSONObject imageLinks = (JSONObject) volumeInfo.get("imageLinks");

        if (imageLinks == null) {
            return "https://tinyurl.com/bdcvmdny";
        }

        String thumbnail = (String) imageLinks.get("thumbnail");

        if (thumbnail == null || thumbnail.trim().isEmpty()) {
            return "https://tinyurl.com/bdcvmdny";
        }

        return thumbnail;
    }

    public String getISBN() {
        JSONArray ISBNList = (JSONArray) volumeInfo.get("industryIdentifiers");
        for (Object obj : ISBNList) {
            JSONObject identifier = (JSONObject) obj;
            if ("ISBN_13".equals(identifier.get("type"))) {
                return (String) identifier.get("identifier");
            }
        }
        return null;
    }

    private static JSONArray getJsonArray(String isbn) throws IOException, ParseException {
        String apiURL = "https://www.googleapis.com/books/v1/volumes?q=isbn:";
        URL url = new URL(apiURL + isbn);

        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        connection.connect();

        JSONObject responseObject = getJsonObject(connection, url);
        return (JSONArray) responseObject.get("items");
    }

    //get JSONObject from api
    private static JSONObject getJsonObject(HttpURLConnection connection, URL url) throws IOException, ParseException {
        int responseCode = connection.getResponseCode();
        if (responseCode != 200) {
            throw new RuntimeException("HttpResponseCode: " + responseCode);
        }

        StringBuilder informationString = new StringBuilder();
        Scanner scanner = new Scanner(url.openStream());
        while (scanner.hasNext()) {
            informationString.append(scanner.nextLine());
        }
        scanner.close();

        JSONParser parser = new JSONParser();
        return (JSONObject) parser.parse(String.valueOf(informationString));
    }
}
