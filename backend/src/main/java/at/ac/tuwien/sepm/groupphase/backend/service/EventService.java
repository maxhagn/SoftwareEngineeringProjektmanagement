package at.ac.tuwien.sepm.groupphase.backend.service;

import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.events.EventInputDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.events.TopEventDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.events.TopEventQueryDto;
import at.ac.tuwien.sepm.groupphase.backend.entity.Event;
import at.ac.tuwien.sepm.groupphase.backend.entity.Location;
import at.ac.tuwien.sepm.groupphase.backend.entity.Performance;
import at.ac.tuwien.sepm.groupphase.backend.exception.CreateFailedException;
import at.ac.tuwien.sepm.groupphase.backend.exception.NotFoundException;
import at.ac.tuwien.sepm.groupphase.backend.exception.ValidationException;
import org.springframework.data.domain.Page;
import java.util.List;

public interface EventService {


    /**
     * This method filters artists according to the following parameters
     * @param event contains search parameters firstname and lastname
     * @param page contains page with size and pagenumber
     * @return Page of artists that meet the criteria
     */
    Page<Event> filter(Event event, int page);

    /**
     * This method filters artists according to the following parameters
     * @param event contains search parameters firstname and lastname
     * @param page contains page with size and pagenumber
     * @param order contains order direction
     * @param sortedBy contains sortedby param
     * @return Page of artists that meet the criteria
     */
    Page<Event> filterDetailed(Event event, int page, String order, String sortedBy);

    /**
     * Find all location entries ordered by published at date (descending).
     *
     * @return ordered list of all event entries
     */
    List<Event> findAll();

    /**
     * Creates a new Event with the given payload
     *
     * @param event the event to create
     * @return the created Event inclusive id
     */
    Event create(Event event);

    /**
     * Creates an event and the performances that correspond to it
     * @param event the dto containing the event and performance information
     * @return the event created
     * @throws CreateFailedException when the event is not valid as defined in Validator
     */
    Event createEventAndPerformances(Event event) throws CreateFailedException;

    /**
     * Finds an event by a given id
     * @param id the id to search by
     * @return the event found
     * @throws NotFoundException when there is no event with the given id
     */
    Event find(long id) throws NotFoundException;

    /**
     * Returns the information regarding the top 10 events in a given month in a given category
     * @param topEventQueryDto contains the category and month information
     * @return the top 10 events with their ticket count
     */
    List<TopEventDto> getTop(TopEventQueryDto topEventQueryDto);
}
