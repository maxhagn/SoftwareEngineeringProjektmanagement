package at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.events;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class TopEventQueryDto {

    String category;

    String date;

}
