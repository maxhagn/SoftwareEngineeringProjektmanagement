package at.ac.tuwien.sepm.groupphase.backend.repository;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.events.PreviewArtistDto;
import at.ac.tuwien.sepm.groupphase.backend.entity.Artist;
import at.ac.tuwien.sepm.groupphase.backend.entity.Location;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import javax.transaction.Transactional;
import java.util.List;

@Repository
@Transactional
public interface ArtistRepository extends JpaRepository<Artist, Long> {


    /**
     * filters horse according to the following  parameters
     * @param firstname filters by part of firstname
     * @param lastname filters by part of lastname
     * @return list of artists that satisfy the query
     */
    @Query(value = "Select new at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.events.PreviewArtistDto(artist.id, artist.firstname, artist.surname) from Artist artist " +
        "where (:firstname is null or lower(artist.firstname) like lower(concat('%', :firstname,'%'))) " +
        " or (:lastname is null or lower(artist.surname) like lower(concat('%', :lastname,'%')))" +
        " order by artist.firstname "
    )
    Page<PreviewArtistDto> filter(@Param("firstname") String firstname,
                                  @Param("lastname") String lastname,
                                  Pageable pageRequest
    );

}
