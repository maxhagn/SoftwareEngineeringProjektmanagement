package at.ac.tuwien.sepm.groupphase.backend.endpoint.mapper;

import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.events.EventPreviewDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.ticket.SeatDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.ticket.TicketPreviewDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.user.UserDto;
import at.ac.tuwien.sepm.groupphase.backend.entity.Performance;
import at.ac.tuwien.sepm.groupphase.backend.entity.Ticket;
import at.ac.tuwien.sepm.groupphase.backend.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Named;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Mapper
public abstract class TicketMapper {

    /**
     * Implicitly maps a User Entity to a User Dto
     *
     * @param ticket The Entity to map to a Ticket Dto
     * @return the Ticket mapped
     */
    public TicketPreviewDto entityToPreviewDto(Ticket ticket){
        String barcode = "" + ticket.getId() + ((ticket.hashCode()+"adfadf").hashCode() & 0xfffffff);
        LocalDateTime date = ticket.getPerformance().getDatetime();
        return TicketPreviewDto.builder()
            .id(ticket.getId())
            .status(ticket.getStatus())
            .date(date.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")))
            .event(EventMapper.INSTANCE.eventToPreviewDto(ticket.getPerformance().getEvent()))
            .hall(ticket.getPerformance().getHall().getName())
            .seats(ticket.getSeats().stream().map(SeatMapper.INSTANCE::seatToDto).collect(Collectors.toList()))
            .barcode(barcode)
            .build();
    }


}
