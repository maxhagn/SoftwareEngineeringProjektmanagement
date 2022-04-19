package at.ac.tuwien.sepm.groupphase.backend.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.List;
import java.util.Objects;
import java.util.Set;

@Entity
@Getter
@Setter
@EqualsAndHashCode
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "location")
public class Location {
    @Id
    //automatically generates id's (note: could also do this in SQL-file)
    @GeneratedValue(generator = "GivenOrGeneratedId")
    @GenericGenerator(name = "GivenOrGeneratedId", strategy = "at.ac.tuwien.sepm.groupphase.backend.entity.helper.GivenOrGeneratedId")
    @Column(name = "id")
    private Long id;

    //not optional -> can't be null
    @Column(nullable = false)
    private String name;

    //not optional -> can't be null
    @Column(nullable = false)
    private String street;

    //not optional -> can't be null
    @Column(nullable = false)
    private String city;

    //not optional -> can't be null
    @Column(nullable = false)
    private String area_code;

    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    @OneToMany(mappedBy = "location")
    private List<Event> events;

    @JsonIgnore
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    @OneToMany(mappedBy = "location", fetch = FetchType.EAGER)
    private Set<Hall> halls;

    //NOT OPTIONAL
    @JsonIgnore
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    @OneToMany(mappedBy = "location")
    private Set<PriceCategory> priceCategories;
}


