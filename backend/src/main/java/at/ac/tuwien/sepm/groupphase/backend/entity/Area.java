package at.ac.tuwien.sepm.groupphase.backend.entity;

import at.ac.tuwien.sepm.groupphase.backend.entity.enums.Type;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.Set;

@Entity
@Getter
@Setter
@EqualsAndHashCode
@ToString
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
@Table(name = "AREA")
public class Area {
    @Id
    //automatically generates id's (note: could also do this in SQL-file)
    @GeneratedValue(generator = "GivenOrGeneratedId")
    @GenericGenerator(name = "GivenOrGeneratedId", strategy = "at.ac.tuwien.sepm.groupphase.backend.entity.helper.GivenOrGeneratedId")
    @Column(name = "id")
    private Long id;

    @Column(nullable = false, name = "start_col")
    private int startCol;

    @Column(nullable = false, name = "end_col")
    private int endCol;

    @Column(nullable = false, name = "start_row")
    private int startRow;

    @Column(nullable = false, name = "end_row")
    private int endRow;

    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    @ManyToOne
    @JoinColumn(name = "price_category_id")
    private PriceCategory priceCategory;

    @Column
    private String name;

    @Column(nullable = false)
    private Type type;

    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    @JsonIgnore
    @OneToMany(mappedBy = "area", fetch = FetchType.EAGER)
    private Set<Seat> seats;

    @ManyToOne
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    @JsonIgnore
    @JoinColumn(name = "hall_id", nullable = false)
    private Hall hall;

}
