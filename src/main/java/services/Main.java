package services;

import org.controlsfx.control.tableview2.filter.filtereditor.SouthFilter;
import org.json.simple.JSONObject;
import org.json.simple.JSONArray;
import org.json.simple.parser.JSONParser;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Objects;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) {

        try {
            //create API request
            String isbn = "059035342X"; //Harry Potter
            String apiURL = "https://www.googleapis.com/books/v1/volumes?q=isbn:";
            URL url = new URL(apiURL + isbn);

            //set connection
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.connect();

            //Check if connect is made
            int responseCode = connection.getResponseCode();

            // 200 is OK
            if (responseCode != 200) {
                throw new RuntimeException("HttpResponseCode: " + responseCode);
            } else {

            //Get JSON file in String
            StringBuilder informationString = new StringBuilder();
            Scanner scanner = new Scanner(url.openStream());

            while (scanner.hasNext()) {
                informationString.append(scanner.nextLine());
            }
            //Close the scanner
            scanner.close();

            //print all content of JSON file
            //System.out.println(informationString);

             //create JSON Object
            JSONParser parser = new JSONParser();
            JSONObject responseObject = (JSONObject) parser.parse(String.valueOf(informationString));
            JSONArray itemsArray = (JSONArray) responseObject.get("items");
            JSONObject itemsObject = (JSONObject) itemsArray.get(0);
            JSONObject volumeInfo = (JSONObject) itemsObject.get("volumeInfo");


            //get title of the book
            String title = (String) volumeInfo.get("title");

            //get authors
            JSONArray authorArray = (JSONArray) volumeInfo.get("authors");
            ArrayList<String> authorList = new ArrayList<>();
            for(Object object : authorArray) {
                String author = (String) object;
                authorList.add(author);
            }

            //get publisher
            String publisher = (String) volumeInfo.get("publisher");

            //get publishedDate
            String publishedDate = (String) volumeInfo.get("publishedDate");


            //get description of the book
            String description = (String) volumeInfo.get("description");

            //print gathered information
            System.out.println("Title:");
            System.out.println(title);
            System.out.println("Authors:");
            for(String author : authorList) {
                System.out.println(author);
            }
            System.out.println("Publisher:");
            System.out.println(publisher);
            System.out.println("publishedDate:");
            System.out.println(publishedDate);
            System.out.println("Description:");
            System.out.println(description);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
