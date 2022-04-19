package at.ac.tuwien.sepm.groupphase.backend.endpoint.mapper;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.events.LocationDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.events.LocationWithAreaCodeDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.events.SimpleHallDto;
import at.ac.tuwien.sepm.groupphase.backend.entity.Hall;
import at.ac.tuwien.sepm.groupphase.backend.entity.Location;
import org.mapstruct.IterableMapping;
import org.mapstruct.Named;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface LocationMapper {

    public LocationDto entityToDto(Location location);

    public Location dtoToEntity(LocationDto locationDto);

    public LocationWithAreaCodeDto entityToWithAreCodeDto(Location location);

    public Location locationWithAreaCodeDtoToEntity(LocationWithAreaCodeDto locationDto);

    default Set<Hall> simpleHallDtoToHall(Set<SimpleHallDto> halldtos){
        Set<Hall> halls = new HashSet<>();
        for(SimpleHallDto h : halldtos){
            halls.add(Hall.builder().id(h.getId()).name(h.getName()).build());
        }
        return halls;
    }

    default Set<SimpleHallDto> halltoSimpleHallDto(Set<Hall> halls){
        Set<SimpleHallDto> dtos = new HashSet<>();
        for (Hall h: halls
             ) {
            dtos.add(SimpleHallDto.builder().id(h.getId()).name(h.getName()).build());
        }
        return dtos;
    }

}
