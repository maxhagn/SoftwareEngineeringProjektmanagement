package at.ac.tuwien.sepm.groupphase.backend.entity;


import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;

import javax.persistence.*;
import java.util.Objects;

@Entity
@Getter
@Setter
@EqualsAndHashCode
@ToString
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
@Table(name = "SEAT")
public class Seat {

    @Id
    //automatically generates id's (note: could also do this in SQL-file)
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "seq_seat_id")
    @SequenceGenerator(name = "seq_seat_id", sequenceName = "seq_seat_id")
    @Column(name = "id")
    private Long id;

    @Column(nullable = false, name = "seat_col")
    private int seatCol;

    @Column(nullable = false, name = "seat_row")
    private int seatRow;

    @JsonIgnore
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    @ManyToOne
    @JoinColumn(name = "ticket_id", nullable = false)
    private Ticket ticket;

    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    @ManyToOne
    @JoinColumn(name = "area_id", nullable = false)
    private Area area;


}
