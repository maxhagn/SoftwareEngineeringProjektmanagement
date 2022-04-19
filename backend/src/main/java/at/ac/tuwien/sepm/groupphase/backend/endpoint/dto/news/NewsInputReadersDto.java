package at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.news;

import at.ac.tuwien.sepm.groupphase.backend.entity.User;
import lombok.*;

import java.time.LocalDate;
import java.util.List;

@Builder
@AllArgsConstructor
@Getter
@Setter
@NoArgsConstructor
@ToString
public class NewsInputReadersDto {
    private Long id;
    private List<User> readers;



}
