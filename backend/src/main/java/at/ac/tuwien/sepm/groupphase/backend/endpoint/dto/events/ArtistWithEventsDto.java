package at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.events;

import at.ac.tuwien.sepm.groupphase.backend.entity.Event;
import lombok.*;

import java.util.LinkedList;
import java.util.List;

@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class ArtistWithEventsDto extends PreviewArtistDto {
    private List<RealEventDto> events;

    public ArtistWithEventsDto(Long id, String firstname, String surname) {
        super(id, firstname,surname);
    }

    public List<RealEventDto> getEvents() {
        return events;
    }


    public ArtistWithEventsDto(Long id, String firstName, String lastName, List<Event> events) {
        super(id, firstName, lastName);
        List<RealEventDto> eventDtos = new LinkedList<>();
        for (Event e : events) {
            eventDtos.add(RealEventDto.builder()
                .title(e.getTitle())
                .description(e.getDescription())
                .category(e.getCategory())
                .duration(e.getDuration())
                .id(e.getId())
                .build());
        }
        this.events = eventDtos;
    }

}
