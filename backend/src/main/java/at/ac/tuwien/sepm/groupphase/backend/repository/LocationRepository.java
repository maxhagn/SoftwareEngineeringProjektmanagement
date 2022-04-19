package at.ac.tuwien.sepm.groupphase.backend.repository;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.events.LocationDto;
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
public interface LocationRepository extends JpaRepository<Location, Long> {

    /**
     * filters location according to the following  parameters
     * @param name filters by part of name
     * @param street filters by part of description
     * @param city filters by race
     * @param area_code filters by  score
     * @return list of locations that satisfy the query
     */
    @Query(value = "Select new at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.events.LocationDto(location.id, location.name, location.street,  location.city, location.area_code) from Location location " +
        "where (:name is null or lower(location.name) like lower(concat('%', :name,'%'))) " +
        " and (:street is null or lower(location.street) like lower(concat('%', :street,'%')))" +
        " and (:city is null or lower(location.city) like lower(concat('%', :city,'%')))" +
        " and (:area_code is null or lower(location.area_code) like lower(concat('%', :area_code,'%')))"
    )
    Page<LocationDto> extendedFilter(@Param("name") String name,
                                     @Param("street") String street,
                                     @Param("city") String city,
                                     @Param("area_code") String area_code,
                                     Pageable pageRequest
    );

    /**
     * filters horse according to the following  parameters
     * @param mixedQuery filters by part of name
     * @return list of horses that satisfy the query
     */
    @Query(value = "Select new at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.events.LocationDto(location.id, location.name, location.street,  location.city, location.area_code) from Location location " +
        "where lower(location.name) like lower(concat('%', :mixedQuery,'%')) " +
        " or lower(location.street) like lower(concat('%', :mixedQuery,'%'))" +
        " or lower(location.city) like lower(concat('%', :mixedQuery,'%'))" +
        " or lower(location.area_code) like lower(concat('%', :mixedQuery,'%'))"
    )
    Page<LocationDto> mixedFilter(@Param("mixedQuery") String mixedQuery,
                                Pageable pageRequest
    );


}
