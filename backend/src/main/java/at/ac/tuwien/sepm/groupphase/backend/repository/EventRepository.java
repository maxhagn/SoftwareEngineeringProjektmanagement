package at.ac.tuwien.sepm.groupphase.backend.repository;

import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.events.TopEventDto;
import at.ac.tuwien.sepm.groupphase.backend.entity.Event;
import at.ac.tuwien.sepm.groupphase.backend.entity.enums.Category;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface EventRepository extends JpaRepository<Event, Long> {

    /**
     * gets all top ten events according to criteria
     * @param category  of event
     * @param begin of event
     * @param end of event
     * @return top ten events
     */
    @Query(value =
        "select new at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.events.TopEventDto(event.id,event.title, count(ticket.id)) " +
            "from Event event " +
            "inner join Performance performance on performance.event.id = event.id " +
            "inner join Ticket ticket on ticket.performance.id = performance.id " +
            "where event.category = :category " +
            "and (ticket.createDate BETWEEN :begin AND :end)" +
            "group by event.id , event.title order by count(ticket.id) desc" )
    List<TopEventDto> getTopEvents(@Param("category") Category category, @Param("begin") LocalDateTime begin, @Param("end") LocalDateTime end);



    /**
     * filters news images by id of news
     * @param id filters by id
     * @return list of events that satisfy the query
     */
    @Query(value = "select event from Event event " +
        "where event.artist.id = :id")
    List<Event> getEventsByArtistId(@Param("id") Long id);

    /**
     * filters news images by id of news
     * @param id filters by id
     * @return list of events that satisfy the query
     */
    @Query(value = "select event from Event event " +
        "where event.location.id = :id")
    List<Event> getEventsByLocationId(@Param("id") Long id);

    /**
     * filters event according to the following  parameters
     * @param title filters by part of title
     * @return list of artists that satisfy the query
     */
    @Query(value = "select event from Event event " +
        "where (:title is null or lower(event.title) like lower(concat('%', :title,'%')))" +
        "and (:duration = 0 or event.duration between :duration-30 and :duration+30)" +
        "and (:description is null or lower(event.description) like lower(concat('%', :description,'%')))" +
        "and (:category is null or event.category = :category)")
    Page<Event> filterDetailed(@Param("title") String title, @Param("duration") int duration, @Param("description") String description, @Param("category") Category category,
                               Pageable pageRequest
    );

    /**
     * filters event according to the following  parameters
     * @param mixedQuery filters by part of title
     * @return list of artists that satisfy the query
     */
    @Query(value = "select event from Event event " +
        "where  lower(event.title) like lower(concat('%', :mixedQuery,'%'))" +
        "or  lower(event.description) like lower(concat('%', :mixedQuery,'%'))")
    Page<Event> filterMixed(@Param("mixedQuery") String mixedQuery,
                            Pageable pageRequest
    );

    /**
     * filters event according to the following  parameters
     * @param title filters by part of title
     * @return list of artists that satisfy the query
     */
    @Query(value = "select event from Event event " +
        "where lower(event.title) like lower(concat('%', :title,'%'))")
    Page<Event> filter(@Param("title") String title,
                       Pageable pageRequest
    );


}
