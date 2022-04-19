package at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.news;

import at.ac.tuwien.sepm.groupphase.backend.entity.Event;
import at.ac.tuwien.sepm.groupphase.backend.entity.User;
import lombok.*;

import java.time.LocalDate;
import java.util.List;
@Data
@Builder
@AllArgsConstructor
@Getter
@Setter
@NoArgsConstructor
@ToString
public class NewsInputDto {
    private Long id;
    private String title;
    private String text;
    private String author;
    private LocalDate date;
    private String summary;
    private String newsCol;
    private NewsEventDto event;
    private List<NewsUserDto> readers;
    private List<NewsImageDto> newsImages;



}
