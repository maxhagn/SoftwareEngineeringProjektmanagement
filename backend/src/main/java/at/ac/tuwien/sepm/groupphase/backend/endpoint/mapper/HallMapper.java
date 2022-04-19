package at.ac.tuwien.sepm.groupphase.backend.endpoint.mapper;

import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.events.HallDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.ticket.SeatDto;
import at.ac.tuwien.sepm.groupphase.backend.entity.Area;
import at.ac.tuwien.sepm.groupphase.backend.entity.Hall;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

@Mapper
public abstract class HallMapper {

    public HallDto hallToHallDtoWithSeats(Hall hall, Long performanceId){
        List<SeatDto> seats = new LinkedList<>();
        for (Area a : hall.getAreas()) {
            seats.addAll(
                a.getSeats().stream()
                    .filter(s -> s.getTicket().getPerformance().getId().equals(performanceId))
                    .map(SeatMapper.INSTANCE::seatToDto).collect(Collectors.toList())
            );
        }

        return HallDto.builder()
            .id(hall.getId())
            .name(hall.getName())
            .rows(hall.getRows())
            .cols(hall.getCols())
            .areas(hall.getAreas().stream().map(AreaMapper.INSTANCE::areaToDto).collect(Collectors.toList()))
            .seats(seats)
            .build();
    }

    public abstract HallDto hallToHallDto(Hall oneById);

    public abstract Hall hallDtoToHall(HallDto hallDto);

}
