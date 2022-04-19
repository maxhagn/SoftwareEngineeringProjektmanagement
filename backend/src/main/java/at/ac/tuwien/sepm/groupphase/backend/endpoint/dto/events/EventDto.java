package at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.events;

import at.ac.tuwien.sepm.groupphase.backend.entity.enums.Category;

import java.util.List;

public class EventDto extends EventPreviewDto {
    private List<PerformanceDto> performances;
    private LocationDto location;
    private PreviewArtistDto artist;

    public EventDto(int id, String title, String description, Category category, int duration, List<PerformanceDto> performances, LocationDto location, PreviewArtistDto artist) {
        super(id, title, description, category, duration);
        this.performances = performances;
        this.location = location;
        this.artist = artist;
    }

    public EventDto(int id, String name, String description, Category category, int duration) {
        super(id, name, description, category, duration);
    }

    public List<PerformanceDto> getPerformances() {
        return performances;
    }

    public LocationDto getLocation() {
        return location;
    }

    public PreviewArtistDto getArtist() {
        return artist;
    }


}
