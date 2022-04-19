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
public class LocationWithEventsDto extends LocationDto {

    private List<RealEventDto> events;

    public List<RealEventDto> getEvents() {
        return events;
    }


    public LocationWithEventsDto(Long id, String name, String street, String city, String area_code, List<Event> events) {
        super(id, name, street, city, area_code);
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
