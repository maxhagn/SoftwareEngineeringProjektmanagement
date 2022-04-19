package at.ac.tuwien.sepm.groupphase.backend.endpoint.mapper;

import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.ticket.TicketQueueDto;
import at.ac.tuwien.sepm.groupphase.backend.entity.Ticket;
import org.mapstruct.Mapper;

@Mapper(uses = SeatDisplayMapper.class, componentModel = "spring")
public interface TicketQueueMapper {

    TicketQueueDto entityToTicketQueueDto(Ticket ticket);

}
