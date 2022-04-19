package at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.ticket;

import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.seat.SeatDisplayDto;
import lombok.Data;

import java.util.List;

@Data
public class TicketQueueDto {

    private int id;
    private List<SeatDisplayDto> seats;

}
