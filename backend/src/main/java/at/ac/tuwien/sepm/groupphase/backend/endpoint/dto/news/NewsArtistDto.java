package at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.news;

import at.ac.tuwien.sepm.groupphase.backend.entity.Event;
import lombok.*;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import java.util.List;
@Data
@Getter
@Setter
@EqualsAndHashCode
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NewsArtistDto {
    @Id
    //automatically generates id's (note: could also do this in SQL-file)
    @GeneratedValue(generator = "GivenOrGeneratedId")
    @GenericGenerator(name = "GivenOrGeneratedId", strategy = "at.ac.tuwien.sepm.groupphase.backend.entity.helper.GivenOrGeneratedId")
    @Column(name = "id")
    private Long id;

    //not optional -> can't be null
    @Column(nullable = false)
    private String firstname;

    //not optional -> can't be null
    @Column(nullable = false)
    private String surname;

    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    @OneToMany(mappedBy = "artist")
    private List<Event> events;
}
