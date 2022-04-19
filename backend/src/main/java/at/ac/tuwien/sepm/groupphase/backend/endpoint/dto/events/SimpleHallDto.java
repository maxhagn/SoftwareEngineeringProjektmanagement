package at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.events;


import lombok.*;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class SimpleHallDto {
    private Long id;
    @NotNull
    @Size(max = 255)
    private String name;
}

