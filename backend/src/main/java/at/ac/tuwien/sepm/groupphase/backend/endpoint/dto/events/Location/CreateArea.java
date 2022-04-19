package at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.events.Location;

import at.ac.tuwien.sepm.groupphase.backend.entity.enums.Type;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class CreateArea {
    private String name;
    private int startCol;
    private int startRow;
    private int endCol;
    private int endRow;
    private Type type;
    private Long tmpPriceCategoryId;
}
