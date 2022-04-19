package at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.news;

import at.ac.tuwien.sepm.groupphase.backend.entity.News;
import at.ac.tuwien.sepm.groupphase.backend.entity.Ticket;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.Set;
@Data
@Builder
@AllArgsConstructor
@Getter
@Setter
@NoArgsConstructor
@ToString
public class NewsUserDto {

    private Long id;
    private String email;
    private String password;
    private LocalDate birthday;
    public Integer signInAttempts = 0;
    private Integer deleted;
    private boolean admin;
    private String firstname;
    private String surname;
    private Set<Ticket> tickets;
    private Set<News> seenNews;
}
