package at.ac.tuwien.sepm.groupphase.backend.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
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
public class NewsImage {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "seq_news_image_id")
    @SequenceGenerator(name = "seq_news_image_id", sequenceName = "seq_news_image_id")
    @Column(name = "news_image_id")
    private Long id;

    @Column(nullable = false)
    private String original_name;

    @JsonIgnore
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    @ManyToOne()
    @JoinColumn(name = "news_id", nullable = false)
    private News news;

    @Lob
    @ToString.Exclude
    @Column(nullable = false)
    private byte[] pic;

}

