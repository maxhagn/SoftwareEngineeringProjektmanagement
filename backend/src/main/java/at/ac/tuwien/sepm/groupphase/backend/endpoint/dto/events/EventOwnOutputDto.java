package at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.events;

import at.ac.tuwien.sepm.groupphase.backend.entity.Artist;
import at.ac.tuwien.sepm.groupphase.backend.entity.EventImage;
import at.ac.tuwien.sepm.groupphase.backend.entity.News;
import at.ac.tuwien.sepm.groupphase.backend.entity.Performance;
import at.ac.tuwien.sepm.groupphase.backend.entity.enums.Category;

import java.util.List;
import java.util.Set;

public class EventOwnOutputDto {
    private Long id;
    private String title;

    private String description;

    private Category category;

    private Integer duration;

    private String artist;

    private String news;

    private String performances;

    private String eventImages;

    public EventOwnOutputDto(Long id, String title,
                             String description,
                             Category category,
                             Integer duration,
                             String artist,
                             String news,
                             String performances,
                             String eventImages) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.category = category;
        this.duration = duration;
        this.artist = artist;
        this.news = news;
        this.performances = performances;
        this.eventImages = eventImages;
    }

    public EventOwnOutputDto(){

    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public Integer getDuration() {
        return duration;
    }

    public void setDuration(Integer duration) {
        this.duration = duration;
    }


}
