package at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.events;

import at.ac.tuwien.sepm.groupphase.backend.entity.Artist;
import at.ac.tuwien.sepm.groupphase.backend.entity.Location;
import at.ac.tuwien.sepm.groupphase.backend.entity.enums.Category;
import lombok.*;

import javax.persistence.*;
@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RealEventDto {

    private Long id;

    private String title;


    private String description;


    private Category category;


    private Integer duration;

    private Location location;


}
