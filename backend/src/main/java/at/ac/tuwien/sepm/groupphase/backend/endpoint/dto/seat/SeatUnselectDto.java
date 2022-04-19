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
public class SeatUnselectDto {

    @NonNull
    private Long ticketId;

    @NonNull
    private Type type;

    @Nullable
    @Min(0)
    private Integer row;

    @Nullable
    @Min(0)
    private Integer col;

    @Nullable
    private Long areaId;

}
