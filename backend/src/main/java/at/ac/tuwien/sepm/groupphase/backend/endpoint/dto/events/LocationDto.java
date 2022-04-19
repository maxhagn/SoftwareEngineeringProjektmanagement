package at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.events;

import lombok.*;

@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class LocationDto {
    private Long id;
    private String name;
    private String street;
    private String city;
    private String area_code;
}
