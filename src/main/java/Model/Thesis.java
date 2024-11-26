package Model;

import java.util.HashMap;
import java.util.Map;

public class Thesis extends Document {
    private long id;
    private String university;

    public Thesis() {
    }

    public Thesis(String university) {
        this.university = university;
    }

    public Thesis(String title, String author, String description, int totalTheses,
                  int borrowedTheses, String thumbnailURL, String university) {
        super(title, author, description, thumbnailURL, totalTheses, borrowedTheses);
        this.university = university;
    }

    @Override
    public Map<String, String> getInfo() {
        Map<String, String> info = new HashMap<>();
        info.put("Title", this.getTitle());
        info.put("Author", this.getAuthor());
        info.put("University", this.getUniversity());
        info.put("Total theses", String.valueOf(this.getTotalDocument()));
        info.put("Borrowed theses", String.valueOf(this.getBorrowedDocument()));

        return info;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getUniversity() {
        return university;
    }

    public void setUniversity(String university) {
        this.university = university;
    }

    @Override
    public void printDetails() {
        String details = "Thesis{" +
                "id='" + id + '\'' +
                ", title='" + title + '\'' +
                ", author='" + author + '\'' +
                ", university='" + university + '\'' +
                ", description='" + description + '\'' +
                ", totalTheses=" + totalDocument + '\'' +
                ", borrowedTheses=" + borrowedDocument + '\'' +
                ", thumbnailURL='" + thumbnailURL + '\'' +
                '}';
        System.out.println(details);
    }
}
