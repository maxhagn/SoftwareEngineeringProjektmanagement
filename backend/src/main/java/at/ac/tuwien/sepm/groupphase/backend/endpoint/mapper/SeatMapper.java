package at.ac.tuwien.sepm.groupphase.backend.endpoint.mapper;

import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.events.LocationWithAreaCodeDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.seat.SectionInfoDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.ticket.SeatDto;
import at.ac.tuwien.sepm.groupphase.backend.entity.Area;
import at.ac.tuwien.sepm.groupphase.backend.entity.Location;
import at.ac.tuwien.sepm.groupphase.backend.entity.Seat;
import at.ac.tuwien.sepm.groupphase.backend.entity.enums.Type;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;

@Mapper
public interface SeatMapper {

    SeatMapper INSTANCE = Mappers.getMapper(SeatMapper.class);

    @Mapping(
        target = "areaId",
        expression = "java(seat.getArea().getId())"
    )
    @Mapping(target = "col", source = "seatCol")
    @Mapping(target = "row", source = "seatRow")
    @Mapping(target = "sectionInfo", source = "area")
    @Named("seatToDto")
    SeatDto seatToDto(Seat seat);

    default SectionInfoDto AreaToSectionInfoDto(Area area){
        return SectionInfoDto.builder().type(area.getType()).name(area.getName()).build();
    }
}
