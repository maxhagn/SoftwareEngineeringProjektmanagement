package at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.events;

import lombok.*;

import java.util.List;
import java.util.Set;

@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class LocationWithAreaCodeDto {
    private Long id;
    private String name;
    private String street;
    private String city;
    private String area_code;
    private Set<SimpleHallDto> halls;
}

