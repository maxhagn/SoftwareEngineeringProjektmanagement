package at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.events.Location;

import lombok.*;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class CreateHall {
    private String name;
    private int cols;
    private int rows;
    private List<CreateArea> areas;
}
