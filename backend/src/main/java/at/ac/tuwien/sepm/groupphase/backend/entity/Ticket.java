package at.ac.tuwien.sepm.groupphase.backend.entity;

import at.ac.tuwien.sepm.groupphase.backend.entity.enums.Status;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.time.LocalDateTime;
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
@Table(name = "TICKET")
public class Ticket {
    @Id
    //automatically generates id's (note: could also do this in SQL-file)
    @GeneratedValue(generator = "GivenOrGeneratedId")
    @GenericGenerator(name = "GivenOrGeneratedId", strategy = "at.ac.tuwien.sepm.groupphase.backend.entity.helper.GivenOrGeneratedId")
    @Column(name = "id")
    private Long id;

    @Column
    private String res_nr;

    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private Status status;

    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @ManyToOne
    @JoinColumn(name = "performance_id", nullable = false)
    private Performance performance;

    @Getter(AccessLevel.NONE)
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    @OneToMany(mappedBy = "ticket", fetch = FetchType.EAGER, cascade = CascadeType.REMOVE)
    private List<Seat> seats;

    public List<Seat> getSeats(){
        this.seats.forEach(s -> {
            Float f = s.getArea().getPriceCategory().getPrice();
            f += (float)this.performance.getMin_price();
            s.getArea().getPriceCategory().setPrice(f);
        });
        return this.seats;
    }

    @Column(name="date",nullable = false)
    private LocalDateTime createDate;

}
