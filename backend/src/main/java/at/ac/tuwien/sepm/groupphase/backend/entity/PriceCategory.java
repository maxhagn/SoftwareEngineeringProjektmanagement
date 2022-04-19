package at.ac.tuwien.sepm.groupphase.backend.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import org.hibernate.validator.constraints.br.CPF;

import javax.persistence.*;
import javax.transaction.Transactional;
import java.util.List;
import java.util.Objects;

@Entity
@Getter
@Setter
@EqualsAndHashCode
@ToString
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
@Table(name = "PRICE_CATEGORY")
public class PriceCategory {
    @Id
    //automatically generates id's (note: could also do this in SQL-file)
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "seq_price_category_id")
    @SequenceGenerator(name = "seq_price_category_id", sequenceName = "seq_price_category_id")
    @Column(name = "id")
    private Long id;

    @Column(nullable = false)
    private Float price;

    @Column(nullable = false)
    private String name;

    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "location_id", nullable = true)
    private Location location;

    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    @JsonIgnore
    @OneToMany(mappedBy = "priceCategory", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    private List<Area> areas;


}

