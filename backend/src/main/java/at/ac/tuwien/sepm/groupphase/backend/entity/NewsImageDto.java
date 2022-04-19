package at.ac.tuwien.sepm.groupphase.backend.endpoint.dto;

import lombok.*;

import javax.persistence.*;

@Entity
@Getter
@Setter
@EqualsAndHashCode
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "NEWS_IMAGE")
public class NewsImageDto {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "seq_news_image_id")
    @SequenceGenerator(name = "seq_news_image_id", sequenceName = "seq_news_image_id")
    @Column(name = "news_image_id")
    private Long id;

    @Column(nullable = false)
    private String original_name;

    @Column
    private Long news_id;

    @Lob
    @Column(nullable = false)
    private byte[] pic;

}

