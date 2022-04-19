package at.ac.tuwien.sepm.groupphase.backend.endpoint.mapper;

import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.ticket.TicketCheckoutDto;
import at.ac.tuwien.sepm.groupphase.backend.entity.Ticket;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(uses = SeatDisplayMapper.class, componentModel = "spring")
public interface TicketCheckoutMapper {

    @Mapping(
        target = "title",
        source = "performance.event.title"
    )
    @Mapping(
        target = "locationName",
        source = "performance.hall.location.name"
    )
    @Mapping(
        target = "hallName",
        source = "performance.hall.name"
    )
    @Mapping(
        target = "hallId",
        source = "performance.hall.id"
    )
    @Mapping(
        target = "date",
        source = "performance.datetime"
    )
    @Mapping(
        target = "performanceId",
        source = "performance.id"
    )
    TicketCheckoutDto entityToTicketCheckoutDto(Ticket ticket);

}
