package at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.events;

import lombok.*;

@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class LocationSearchDto {
    private String mixedQuery;
    private String name;
    private String street;
    private String city;
    private String plz;
    private String order;
    private String sortedBy;
    private int page;
}
