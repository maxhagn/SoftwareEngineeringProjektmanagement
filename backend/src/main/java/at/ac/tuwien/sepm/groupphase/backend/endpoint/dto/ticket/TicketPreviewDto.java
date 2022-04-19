package at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.ticket;

import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.events.EventDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.events.EventPreviewDto;
import at.ac.tuwien.sepm.groupphase.backend.entity.enums.Status;
import lombok.*;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PUBLIC)
@Getter
@Setter
@Builder
public class TicketPreviewDto {
    private Long id;
    private EventPreviewDto event;
    private String date;
    private List<SeatDto> seats;
    private String barcode;
    private String hall;
    private Status status;
}
