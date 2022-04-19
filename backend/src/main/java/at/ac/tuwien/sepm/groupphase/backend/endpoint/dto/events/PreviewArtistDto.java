package at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.events;

import lombok.*;

@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PreviewArtistDto {
    private Long id;
    private String firstname;
    private String surname;
}
