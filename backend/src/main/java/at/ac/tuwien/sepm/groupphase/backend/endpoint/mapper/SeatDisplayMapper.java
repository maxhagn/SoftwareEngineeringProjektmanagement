package at.ac.tuwien.sepm.groupphase.backend.endpoint.mapper;

import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.seat.SeatDisplayDto;
import at.ac.tuwien.sepm.groupphase.backend.entity.Seat;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface SeatDisplayMapper {

    @Mapping(
        target = "areaName",
        source = "area.name"
    )
    @Mapping(
        target = "row",
        source = "seatRow"
    )
    @Mapping(
        target = "col",
        source = "seatCol"
    )
    @Mapping(
        target = "price",
        source = "area.priceCategory.price"
    )
    @Mapping(
        target = "isSection",
        expression = "java(seat.getArea().getType() == at.ac.tuwien.sepm.groupphase.backend.entity.enums.Type.SECTION)"
    )
    SeatDisplayDto entityToSeatDisplayDto(Seat seat);

    List<SeatDisplayDto> entityToSeatDisplayDtoCollection(List<Seat> seats);

}
