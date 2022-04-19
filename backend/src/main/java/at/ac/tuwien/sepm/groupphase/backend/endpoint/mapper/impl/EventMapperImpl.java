package at.ac.tuwien.sepm.groupphase.backend.endpoint.mapper.impl;

import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.events.EventOwnDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.events.EventOwnOutputDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.events.EventPreviewDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.mapper.EventMapper;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.mapper.UserListMapper;
import at.ac.tuwien.sepm.groupphase.backend.entity.*;
import org.mapstruct.Mapper;
/*
public class EventMapperImpl implements EventMapper {

    public EventPreviewDto eventToPreviewDto(Event event) {
        return EventPreviewDto.builder().id(event.getId()).title(event.getTitle()).description(event.getDescription()).category(event.getCategory()).duration(event.getDuration()).build();
    }

    @Override
    public EventOwnDto eventsToEventsDto(Event event) {
        return new EventOwnDto(
            event.getId(),
            event.getTitle(),
            event.getDescription(),
            event.getCategory(),
            event.getDuration(),
            event.getArtist_id());
    }

    @Override
    public Event eventDtoToEvent(EventOwnDto eventDto) {
       return Event.builder()
           .artist_id(eventDto.getArtist())
           .description(eventDto.getDescription())
           .category(eventDto.getCategory())

           .duration(eventDto.getDuration())
           .build();
    }

    @Override
    public EventOwnOutputDto eventsToOUTDto(Event event) {
        return new EventOwnOutputDto(
            event.getId(),
            event.getTitle(),
            event.getDescription(),
            event.getCategory(),
            event.getDuration(),
            "firstname",
            "news",
            "performances",
            "images");
    }


}
*/