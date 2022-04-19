package at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.events;

import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.ticket.SeatDto;
import lombok.*;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.List;

@Getter
@Setter
@EqualsAndHashCode
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HallDto {
    private Long id;
    @NotNull
    @Size(max = 255)
    private String name;

    @NotNull
    @Min(1)
    private int cols;

    @NotNull
    @Min(1)
    private int rows;

    @NotEmpty
    private List<@Valid AreaDto> areas;

    private List<SeatDto> seats;
}
