package at.ac.tuwien.sepm.groupphase.backend.entity;


import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.List;
import java.util.Set;

@Entity
@Getter
@Setter
@EqualsAndHashCode
@ToString
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
@Table(name = "HALL")
public class Hall {
    @Id
    //automatically generates id's (note: could also do this in SQL-file)
    @GeneratedValue(generator = "GivenOrGeneratedId")
    @GenericGenerator(name = "GivenOrGeneratedId", strategy = "at.ac.tuwien.sepm.groupphase.backend.entity.helper.GivenOrGeneratedId")
    @Column(name = "id")
    private Long id;

    @Column(nullable = false)
    private String name;

    @EqualsAndHashCode.Exclude
    @Column(nullable = false)
    private int cols;

    @EqualsAndHashCode.Exclude
    @Column(nullable = false)
    private int rows;

    @JsonIgnore
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    @OneToMany(mappedBy = "hall")
    private Set<Performance> performances;

    @JsonIgnore
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    @ManyToOne()
    @JoinColumn(name = "location_id", nullable = false)
    private Location location;


    @JsonIgnore
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    @OneToMany(mappedBy = "hall", fetch = FetchType.EAGER)
    private Set<Area> areas;

}

