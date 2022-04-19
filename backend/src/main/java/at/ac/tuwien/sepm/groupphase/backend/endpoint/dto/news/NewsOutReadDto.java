package at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.news;

import lombok.*;

import java.util.Date;
import java.util.List;
@Builder
@AllArgsConstructor
@Getter
@Setter
@NoArgsConstructor
@ToString
public class NewsOutReadDto {
    private List<Long> ids;
}
