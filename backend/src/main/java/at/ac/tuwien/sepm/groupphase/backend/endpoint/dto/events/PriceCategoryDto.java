package at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.events;

import lombok.*;

@Getter
@Setter
@EqualsAndHashCode
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PriceCategoryDto {
    private Long id;
    private float price;
    private String name;
}
