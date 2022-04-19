package at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.events;

import at.ac.tuwien.sepm.groupphase.backend.entity.Event;
import at.ac.tuwien.sepm.groupphase.backend.entity.Hall;
import lombok.*;

import java.time.LocalDateTime;

@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PerformanceOutDto {
    private Long id;
    private LocalDateTime datetime;
    private Hall hall;
    private Event event;
    private double min_price;
}
