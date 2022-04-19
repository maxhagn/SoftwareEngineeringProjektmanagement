package at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.performance;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class PerformanceDetailDto {

    private int id;
    private String title;
    private String locationName;
    private String hallName;
    private int hallId;
    private LocalDateTime date;
    private int duration;
    private double minPrice;

}
