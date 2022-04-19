package at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.performance;

import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.events.HallDto;
import lombok.*;

import java.time.LocalDateTime;
@Data
@Getter
@Setter
@EqualsAndHashCode
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SimpleCreatePerformanceFromEventDto {
    private long id;
    private double min_price;
    private HallDto hall;
    private LocalDateTime date;
}
