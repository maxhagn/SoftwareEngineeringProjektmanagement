package at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.news;

import at.ac.tuwien.sepm.groupphase.backend.entity.News;

import lombok.*;

import java.time.LocalDate;
import java.util.List;
@Builder
@AllArgsConstructor
@Getter
@Setter
@NoArgsConstructor
@ToString
public class NewsOutputPreviewDto {
    private Long id;
    private String title;
    private String author;
    private LocalDate date;
    private String summary;
    private List<NewsImageDto> newsImage;







}
