package at.ac.tuwien.sepm.groupphase.backend.repository;

import at.ac.tuwien.sepm.groupphase.backend.entity.Artist;
import at.ac.tuwien.sepm.groupphase.backend.entity.Ticket;
import at.ac.tuwien.sepm.groupphase.backend.entity.enums.Status;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface TicketRepository extends JpaRepository<Ticket, Long> {

    /**
     * filters tickets according to the following parameters
     * @param userId filters by userid
     * @param status filters by status
     * @param secondStatus filters by status
     * @param pageRequest page number and page size 4
     * @return list of tickets that satisfy the query
     */
    @Query(value = "Select ticket from Ticket ticket" +
        " left join Performance p on p = ticket.performance" +
        " where ticket.user.id = :userId and ( ticket.status = :status" +
        " or ticket.status = :secondStatus ) "
    )
    Page<Ticket> filter(@Param("userId") Long userId,
                        @Param("status") Status status,
                        @Param("secondStatus") Status secondStatus,
                        Pageable pageRequest
    );

    /**
     * filters tickets according to the following parameters
     * @param userId filters by userid
     * @param status filters by status
     * @param pageRequest page number and page size 4
     * @return list of tickets that satisfy the query
     */
    @Query(value = "Select ticket from Ticket ticket" +
        " left join Performance p on p = ticket.performance" +
        " where ticket.user.id = :userId and ticket.status = :status"
    )
    Page<Ticket> filterWithOneStatus(@Param("userId") Long userId,
                        @Param("status") Status status,
                        Pageable pageRequest
    );

}
