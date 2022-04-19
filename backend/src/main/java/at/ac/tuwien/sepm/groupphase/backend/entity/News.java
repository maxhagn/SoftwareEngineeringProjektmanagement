package at.ac.tuwien.sepm.groupphase.backend.entity;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.NewsImageDto;
import at.ac.tuwien.sepm.groupphase.backend.exception.ValidationException;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jdk.jfr.Timestamp;
import lombok.*;

import javax.persistence.*;
import javax.transaction.Transactional;
import java.time.LocalDate;
import java.util.LinkedList;
import java.util.List;

@Entity
@Getter
@Setter
@EqualsAndHashCode
@ToString
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
@Transactional
@Table(name = "NEWS")
public class News {
    @Id
    //automatically generates id's (note: could also do this in SQL-file)
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "seq_news_id")
    @SequenceGenerator(name = "seq_news_id", sequenceName = "seq_news_id")
    @Column(name = "id")
    private Long id;

    @Column(nullable = false)
    private String title;

    //not optional -> can't be null
    @Lob
    @Column(nullable = false)
    private String text;

    //not optional -> can't be null
    @Column(nullable = false)
    private String author;

    @Timestamp
    private LocalDate date;

    @Lob
    @Column(name = "summary")
    private String summary;

    @Column(name = "newsCol")
    private String newsCol;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "event_id"/*, nullable = false*/)
    private Event event;

    @ManyToMany(cascade = CascadeType.ALL)
    @JoinTable(
        name = "user_has_seen_news",
        joinColumns = @JoinColumn(name = "news_id"),
        inverseJoinColumns = @JoinColumn(name = "user_id"))
    private List<User> readers;

    @OneToMany( /*fetch= FetchType.LAZYfetch = FetchType.EAGER, cascade = CascadeType.ALL*/)
    @JoinColumn(name = "picture")
    private List<NewsImageDto> newsImages;

    @JsonIgnore
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinColumn(name = "image")
    private List<NewsImage> images;


    public void addImage(NewsImage image) {
        if (this.images == null){
            this.images = new LinkedList<>();
        }
        this.images.add(image);
    }

    public void addReader(User reader) {
        if (this.readers == null){
            this.readers = new LinkedList<>();
        }
        if (reader == null){
            throw new ValidationException("Given user is null");
        }
        this.readers.add(reader);
    }
}
