package at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.seat;

import at.ac.tuwien.sepm.groupphase.backend.entity.enums.Type;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

import javax.validation.constraints.Min;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SeatSelectDto {

    @NonNull
    private Long ticketId;

    @NonNull
    private Type type;

    @Nullable
    @Min(value = 0, message = "Row must be at least 0")
    private Integer row;

    @Nullable
    @Min(value = 0, message = "Column must be at least 0")
    private Integer col;

    @Nullable
    private Long areaId;

    @Nullable
    @Min(value = 1, message = "Amount of selected seats must be greater than 0")
    private Integer seatCount;

}
