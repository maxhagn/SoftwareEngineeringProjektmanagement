package at.ac.tuwien.sepm.groupphase.backend.service;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.events.*;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.events.Location.CreateLocationDto;
import at.ac.tuwien.sepm.groupphase.backend.exception.CreateFailedException;
import org.springframework.data.domain.Page;

import java.util.List;

public interface LocationService {

    /**
     * This method filters locations according to the following parameters
     * @param locationSearchDto contains all search parameter
     * @return page with locations that meet the criteria
     */
    Page<LocationDto> filter(LocationSearchDto locationSearchDto);

    /**
     * This method gets one location with events by id
     * @param id contains the id of the location
     * @return LocationWithEventsDto holding a location and a list of events
     */
    LocationWithEventsDto getLocationWithEvent(Long id);

    /**
     * This method finds  all locations with area code
     * @return list of locations with area code
     */
    List<LocationWithAreaCodeDto> findAll();

    /**
     * Creates a new Location with Halls and everything
     * @param dto to create
     */
    void create(CreateLocationDto dto) throws CreateFailedException;
}
