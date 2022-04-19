package at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.events.Location;

import at.ac.tuwien.sepm.groupphase.backend.entity.enums.Type;
import lombok.*;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class CreateLocationDto {
    private String name;
    private String street;
    private String city;
    private String country;
    private String area_code;
    private List<CreatePriceCategory> categories;
    private List<CreateHall> halls;


}

