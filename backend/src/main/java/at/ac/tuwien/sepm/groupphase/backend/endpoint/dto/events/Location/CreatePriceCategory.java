package at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.events.Location;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class CreatePriceCategory {
    private String name;
    private Long tmpId;
    private float price;
}
