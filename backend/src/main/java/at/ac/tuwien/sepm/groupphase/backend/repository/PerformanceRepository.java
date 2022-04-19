package at.ac.tuwien.sepm.groupphase.backend.repository;


import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.events.PerformanceOutDto;
import at.ac.tuwien.sepm.groupphase.backend.entity.Location;
import at.ac.tuwien.sepm.groupphase.backend.entity.Performance;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.sql.Time;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Repository
public interface PerformanceRepository extends JpaRepository<Performance, Long> {

    /**
     * filters performances according to the following  parameters
     * @param fromDateTime start of event datetime
     * @param toDateTime end of event datetime
     * @param title filters by title of event
     * @param name filters by name of hall
     * @param price filters by price plus minus 10
     * @param pageRequest filters by page and page size of 6
     * @return list of performances that satisfy the query
     */
    @Query(value = "Select new at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.events.PerformanceOutDto (performance.id,performance.datetime, performance.hall, performance.event, performance.min_price ) from Performance performance" +
        " left join Event event on performance.event=event" +
        " left join Hall hall on performance.hall=hall" +
        " where (:title is null or lower(event.title) like lower(concat('%', :title,'%'))) " +
        " and (:name is null or lower(hall.name) like lower(concat('%', :name,'%')))" +
        " and (:fromDateTime is null or performance.datetime BETWEEN :fromDateTime AND :toDateTime)" +
        " and (:price is 0 or performance.min_price BETWEEN :price-10 AND :price+10)"
    )
    Page<PerformanceOutDto> extendedFilter(@Param("fromDateTime") LocalDateTime fromDateTime,
                                           @Param("toDateTime") LocalDateTime toDateTime,
                                          @Param("title") String title,
                                          @Param("name") String name,
                                          @Param("price") int price,
                                          Pageable pageRequest
    );

    /**
     * filters performances according to the following parameters
     * @param mixedQuery filters by part of name
     * @param pageRequest filters by page and page size of 6
     * @return list of performances that satisfy the query
     */
    @Query(value = "Select new at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.events.PerformanceOutDto (performance.id,performance.datetime, performance.hall, performance.event, performance.min_price ) from Performance performance" +
        " left join Event event on performance.event=event" +
        " left join Hall hall on performance.hall=hall" +
        " where lower(event.title) like lower(concat('%', :mixedQuery,'%')) " +
        " or lower(hall.name) like lower(concat('%', :mixedQuery,'%'))"
    )
    Page<PerformanceOutDto> mixedFilter(@Param("mixedQuery") String mixedQuery,
                                        Pageable pageRequest
    );


}
