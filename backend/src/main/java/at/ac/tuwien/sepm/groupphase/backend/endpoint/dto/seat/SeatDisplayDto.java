package at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.seat;

import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class SeatDisplayDto {

    private String areaName;
    private Boolean isSection;
    private int row;
    private int col;
    private float price;

}
