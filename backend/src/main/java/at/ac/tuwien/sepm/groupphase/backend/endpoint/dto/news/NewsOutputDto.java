package at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.news;

import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.events.EventDto;
import at.ac.tuwien.sepm.groupphase.backend.entity.Event;
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
public class NewsOutputDto {
    private Long id;
    private String title;
    private String text;
    private String author;
    private LocalDate date;
    private String summary;
    private String newsCol;
    private List<NewsImageDto> newsImages;


}
