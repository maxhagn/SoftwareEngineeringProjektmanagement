package at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.news;

import lombok.*;

import java.time.LocalDate;

@Builder
@AllArgsConstructor
@Getter
@Setter
@NoArgsConstructor
@ToString
public class NewsOutputDetailDto {
    private Long id;
    private String title;
    private String text;
    private String author;
    private LocalDate date;





}
