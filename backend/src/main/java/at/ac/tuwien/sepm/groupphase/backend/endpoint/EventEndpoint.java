package at.ac.tuwien.sepm.groupphase.backend.endpoint;

import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.events.*;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.mapper.EventMapper;
import at.ac.tuwien.sepm.groupphase.backend.entity.Event;
import at.ac.tuwien.sepm.groupphase.backend.entity.enums.Category;
import at.ac.tuwien.sepm.groupphase.backend.exception.CreateFailedException;
import at.ac.tuwien.sepm.groupphase.backend.exception.NotFoundException;
import at.ac.tuwien.sepm.groupphase.backend.exception.NotImplementedException;
import at.ac.tuwien.sepm.groupphase.backend.service.EventService;
import io.swagger.annotations.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.lang.invoke.MethodHandles;
import java.util.LinkedList;
import java.util.List;

@RestController
@Api(tags = "Event Endpoint")
@RequestMapping(value = "/api/v1/events")
@ApiResponses(value = {
    @ApiResponse(code = 401, message = "You are not authorized to view the resource"),
})
public class EventEndpoint {
    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private final EventMapper eventMapper;
    private final EventService eventService;

    @Autowired
    public EventEndpoint(EventMapper eventMapper, EventService eventService) {
        this.eventMapper = eventMapper;
        this.eventService = eventService;
    }

    @GetMapping(value = "/top/")
    @ApiImplicitParams({
        @ApiImplicitParam(name = "category", paramType = "path"),
        @ApiImplicitParam(name = "month", paramType = "path")
    })
    @ApiOperation(value = "Get the top ten events to a specific category, or overall if none is sepcified")
    public List<TopEventDto> top(String category, String month) throws NotImplementedException {
        TopEventQueryDto topEventQueryDto = new TopEventQueryDto(category, month);

        LOGGER.info("GET Top Ten Events of " + month);

        return eventService.getTop(topEventQueryDto);


    }

    @GetMapping(value = "/")
    @ApiImplicitParams({
        @ApiImplicitParam(name = "title", paramType = "path"),
        @ApiImplicitParam(name = "titleExtended", paramType = "path"),
        @ApiImplicitParam(name = "duration", paramType = "path", defaultValue = "0"),
        @ApiImplicitParam(name = "content", paramType = "path"),
        @ApiImplicitParam(name = "type", paramType = "path"),
        @ApiImplicitParam(name = "page", paramType = "path"),
        @ApiImplicitParam(name = "detailed", paramType = "path"),
        @ApiImplicitParam(name = "order", paramType = "path"),
        @ApiImplicitParam(name = "sortedBy", paramType = "path")
    })
    @ApiOperation(value = "Search through all events by given parameters")
    public Page<Event> filter(String title, String titleExtended, String duration, String content, String type, int page, boolean detailed, String order, String sortedBy) {
        LOGGER.info("GET Events BY QUERY ON PAGE " + page);


        Event event;

        if (detailed) {
            LOGGER.info("detailed");
            event = Event.builder()
                .description(content)
                .duration(0)
                .title(titleExtended)
                .id(0L)
                .performances(null)
                .category(null)
                .eventImages(null)
                .build();

            if (content == "") {
                LOGGER.info("setting description");
                event.setDescription(null);
            }

            for (Category c : Category.values()) {
                LOGGER.info(c.name());
                LOGGER.info(type);
                if (c.name().equals(type)) {
                    event.setCategory(Category.valueOf(type));
                    LOGGER.info("category was set");
                }
            }

            if (duration != null) {
                LOGGER.info("setting duration");

                event.setDuration(Integer.parseInt(duration));
            }

            return eventService.filterDetailed(event, page, order, sortedBy);
        } else {
            event = Event.builder()
                .description(title)
                .duration(null)
                .title(title)
                .id(0L)
                .performances(null)
                .category(null)
                .eventImages(null)
                .build();
            return eventService.filter(event, page);
        }


    }

    @GetMapping(value = "/{id}")
    @ApiResponses(value = {
        @ApiResponse(code = 404, message = "Event not found")
    })
    @ApiOperation(value = "Get details including performances to a specific event")
    public EventOutputDto find(@PathVariable long id) throws NotFoundException {
        Event event = eventService.find(id);
        return eventMapper.eventToEventOutputDto(event);
    }


    @PostMapping(value = "/")
    @ApiResponses(value = {
        @ApiResponse(code = 403, message = "You are not allowed to acces this ressource"),
        @ApiResponse(code = 422, message = "Invalid Request")
    })
    @ApiOperation(value = "Create new Event")
    @Secured("ROLE_ADMIN")
    @ResponseStatus(HttpStatus.CREATED)
    public EventOwnOutputDto create(@RequestBody @Valid EventInputDto event) throws CreateFailedException {
        LOGGER.info(event.toString());
        Event evententity = eventMapper.eventInputDtoToEntity(event);
        evententity.setId(null);
        Event out = eventService.createEventAndPerformances(evententity);
        LOGGER.info(out.toString());
        return eventMapper.eventsToOUTDto(out);
    }

    @GetMapping(value = "/list")
    @ApiResponses(value = {
        @ApiResponse(code = 403, message = "You are not allowed to acces this ressource"),
        @ApiResponse(code = 422, message = "Invalid Request")
    })
    @ApiOperation(value = "Get all events")
    public List<EventOwnOutputDto> getAll() {
        List<Event> all = eventService.findAll();
        List<EventOwnOutputDto> dtos = new LinkedList<>();
        for (Event event : all) {
            dtos.add(eventMapper.eventsToOUTDto(event));

        }
        return dtos;

    }

}
