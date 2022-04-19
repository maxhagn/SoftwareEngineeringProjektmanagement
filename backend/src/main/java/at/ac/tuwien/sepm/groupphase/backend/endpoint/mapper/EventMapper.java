package at.ac.tuwien.sepm.groupphase.backend.endpoint.mapper;


import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.events.*;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.performance.SimpleCreatePerformanceFromEventDto;
import at.ac.tuwien.sepm.groupphase.backend.entity.*;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Mapper(componentModel = "spring")
public interface EventMapper {

    EventMapper INSTANCE = Mappers.getMapper(EventMapper.class);

    EventOwnDto eventsToEventsDto(Event event);

    Event eventDtoToEvent(EventOwnOutputDto eventDto);

    //just for displaying
    EventOwnOutputDto eventsToOUTDto(Event event);

    EventPreviewDto eventToPreviewDto(Event event);

    Event eventInputDtoToEntity(EventInputDto event);

    default Location LocationWithAreaCodeDtoToLocation(LocationWithAreaCodeDto location){
        LocationMapper locationMapper = new LocationMapperImpl();
        return locationMapper.locationWithAreaCodeDtoToEntity(location);
    }

    default Set<Performance> SimpleCreatePerformanceFromEventDtoListToPerfomanceList(List<SimpleCreatePerformanceFromEventDto> performancedtos){
        Set<Performance> out = new HashSet<>();
        HallMapper hallMapper = new HallMapperImpl();
        for(SimpleCreatePerformanceFromEventDto dto : performancedtos){
            out.add(Performance.builder().datetime(dto.getDate()).min_price(dto.getMin_price()).hall(hallMapper.hallDtoToHall(dto.getHall())).build());
        }
        return out;
    }

    default Artist artistIdToArtist(Integer artistId){
        return Artist.builder().id(artistId.longValue()).build();
    }

    EventOutputDto eventToEventOutputDto(Event event);

    default LocationWithAreaCodeDto LocationToLocationWithAreaCodeDto(Location location){
        LocationMapper locationMapper = new LocationMapperImpl();
        return locationMapper.entityToWithAreCodeDto(location);
    }

    default List<SimpleCreatePerformanceFromEventDto> PerformanceSetToSimpleCreatePerformanceFromEventDto(Set<Performance> performances){
        List<SimpleCreatePerformanceFromEventDto> dtos = new ArrayList<>();
        HallMapper hallMapper = new HallMapperImpl();
        for(Performance p : performances){
            dtos.add(SimpleCreatePerformanceFromEventDto.builder().id(p.getId()).date(p.getDatetime()).min_price(p.getMin_price()).hall(hallMapper.hallToHallDto(p.getHall())).build());
        }
        return dtos;
    }

    default String artistToArtistString(Artist artist){
        return artist.getFirstname()+" "+artist.getSurname();
    }

}