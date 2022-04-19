package at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.ticket;

import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.seat.SeatDisplayDto;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class TicketCheckoutDto {

    private int id;
    private String title;
    private String image;
    private String locationName;
    private String hallName;
    private LocalDateTime date;
    private Long performanceId;
    private Long hallId;

    private List<SeatDisplayDto> seats;

}
