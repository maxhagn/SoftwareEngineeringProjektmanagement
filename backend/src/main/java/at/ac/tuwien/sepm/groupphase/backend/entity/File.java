package at.ac.tuwien.sepm.groupphase.backend.entity;


import lombok.*;

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
@Table(name = "FILE")
public class File {

    @Id
    //automatically generates id's (note: could also do this in SQL-file)
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "seq_picture_id")
    @SequenceGenerator(name = "seq_picture_id", sequenceName = "seq_picture_id")
    @Column(name = "upload_name")
    private String upload_name;

    @Column(nullable = false)
    private String original_name;

    @Column(nullable = false)
    private String upload_dir;

}

