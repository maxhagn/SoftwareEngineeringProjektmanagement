package at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.events;

import at.ac.tuwien.sepm.groupphase.backend.entity.enums.Type;
import lombok.*;

@Getter
@Setter
@EqualsAndHashCode
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AreaDto {
    private Long id;
    private int startCol;
    private int endCol;
    private int startRow;
    private int endRow;
    private String name;
    private int limit;
    private PriceCategoryDto priceCategory;
    private Type type;

}
