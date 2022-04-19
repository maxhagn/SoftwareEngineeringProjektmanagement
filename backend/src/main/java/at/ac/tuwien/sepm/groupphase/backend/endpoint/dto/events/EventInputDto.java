package at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.events;

import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.performance.PerformanceDetailDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.performance.SimpleCreatePerformanceFromEventDto;
import at.ac.tuwien.sepm.groupphase.backend.entity.enums.Category;
import lombok.*;
import org.springframework.stereotype.Component;

import java.util.List;

@Data
@Getter
@Setter
@EqualsAndHashCode
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EventInputDto {
    Long id;
    String title;
    String description;
    Category category;
    Integer duration;
    Integer artist;
    LocationWithAreaCodeDto location;
    List<SimpleCreatePerformanceFromEventDto> performances;
}
