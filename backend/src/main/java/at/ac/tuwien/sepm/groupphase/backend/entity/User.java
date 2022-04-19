package at.ac.tuwien.sepm.groupphase.backend.entity;


import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import org.apache.commons.lang3.builder.EqualsExclude;
import org.apache.commons.lang3.builder.HashCodeExclude;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.Objects;
import java.util.Set;

@Entity
@Getter
@Setter
@EqualsAndHashCode
@ToString
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
@Table(name = "USER")
public class User {
    @Id
    @GeneratedValue(generator = "GivenOrGeneratedId")
    @GenericGenerator(name = "GivenOrGeneratedId", strategy = "at.ac.tuwien.sepm.groupphase.backend.entity.helper.GivenOrGeneratedId")
    @Column(name = "id")
    private Long id;

    //is only null after user is deleted
    @Column(nullable = true, unique = true)
    private String email;

    //not optional -> can't be null
    @Column(nullable = false)
    private String password;

    @Column(name = "birthday", nullable = true)
    private LocalDate birthday;

    @Column(name = "sign_in_attempts")
    public Integer signInAttempts = 0;

    @Column(name = "deleted")
    private Integer deleted;

    //not optional -> can't be null
    @Column(nullable = false)
    private boolean admin;

    //TODO: ask about necessity of first- & lastname
    //not optional -> can't be null
    @Column(name = "firstname")
    private String firstname;

    @Column(name = "surname")
    private String surname;


    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    @JsonIgnore
    @OneToMany(mappedBy = "user", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    private Set<Ticket> tickets;

    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    @JsonIgnore
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "user_has_seen_news",
        joinColumns = @JoinColumn(name = "user_id"),
        inverseJoinColumns = @JoinColumn(name = "news_id"))
    private Set<News> seenNews;

}