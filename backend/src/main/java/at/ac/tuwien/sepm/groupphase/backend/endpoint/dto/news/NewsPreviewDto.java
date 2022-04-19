package at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.news;

public class NewsPreviewDto {
    private int id;
    private String title;
    private String summary;
    private String image;

    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getSummary() {
        return summary;
    }

    public String getImage() {
        return image;
    }
}
