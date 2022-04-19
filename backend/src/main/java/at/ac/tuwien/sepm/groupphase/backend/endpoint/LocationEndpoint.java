package at.ac.tuwien.sepm.groupphase.backend.endpoint;

import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.events.*;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.events.Location.CreateLocationDto;
import at.ac.tuwien.sepm.groupphase.backend.exception.CreateFailedException;
import io.swagger.annotations.*;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import at.ac.tuwien.sepm.groupphase.backend.service.LocationService;
import at.ac.tuwien.sepm.groupphase.backend.exception.NotFoundException;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import javax.validation.Valid;
import java.lang.invoke.MethodHandles;

@RestController
@Api(tags = "Location Endpoint")
@ApiResponses(value = {
    @ApiResponse(code = 401, message = "You are not authorized to view the resource"),
})
@RequestMapping(value = "/api/v1/location")
public class LocationEndpoint {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private final LocationService locationService;

    @Autowired
    public LocationEndpoint(LocationService locationService) {
        this.locationService = locationService;
    }

    @GetMapping(value = "/")
    @ApiImplicitParams({
        @ApiImplicitParam(name = "mixedQuery", paramType = "path"),
        @ApiImplicitParam(name = "name", paramType = "path"),
        @ApiImplicitParam(name = "street", paramType = "path"),
        @ApiImplicitParam(name = "city", paramType = "path"),
        @ApiImplicitParam(name = "plz", paramType = "path"),
        @ApiImplicitParam(name = "page", paramType = "path"),
        @ApiImplicitParam(name = "order", paramType = "path"),
        @ApiImplicitParam(name = "sortedBy", paramType = "path")
    })
    @ResponseStatus(HttpStatus.OK)
    public Page<LocationDto> filter(LocationSearchDto locationSearchDto) {

        try {
            LOGGER.info("GET LOCATION BY QUERY ON PAGE " + locationSearchDto.getPage());
            return locationService.filter(locationSearchDto);

        } catch (NotFoundException e) {
            LOGGER.warn("Error 404 while filtering locations");
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Error during reading location", e);

        } catch (RuntimeException e) {
            LOGGER.warn("Error 503 while filtering locations");
            throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE, "Error while accessing location", e);
        }
    }

    @GetMapping(value = "/{id}")
    @ApiResponses(value = {
        @ApiResponse(code = 404, message = "Location not found")
    })
    @ApiOperation(value = "Get details including events to a specific location")
    public LocationWithEventsDto find(@PathVariable Long id) {
        try {
            LOGGER.info("GET LOCATION WITH EVENT BY ID " + id);
            return locationService.getLocationWithEvent(id);
        } catch (NotFoundException e) {
            LOGGER.warn("Error 404 while getting location with event");
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Error during reading location", e);

        } catch (RuntimeException e) {
            LOGGER.warn("Error 503 while getting location with event");
            throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE, "Error while accessing location", e);
        }
    }

    @PostMapping(value = "/")
    @ResponseStatus(HttpStatus.CREATED)
    @ApiResponses(value = {
        @ApiResponse(code = 403, message = "You are not allowed to acces this ressource"),
        @ApiResponse(code = 422, message = "Invalid Request")
    })
    @ApiOperation(value = "Create new a new Location")
    public void create(@RequestBody @Valid CreateLocationDto dto) throws CreateFailedException {
        this.locationService.create(dto);
    }

    @GetMapping(value = "/list")
    @ApiResponses(value = {
        @ApiResponse(code = 404, message = "Location not found")
    })
    @ApiOperation(value = "Get all locations with area code")
    public List<LocationWithAreaCodeDto> findAll() {
        try {
            LOGGER.info("Get all locations with area code");
            return locationService.findAll();
        } catch (NotFoundException e) {
            LOGGER.warn("Error 404 while getting location with event");
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Error during reading location", e);

        } catch (RuntimeException e) {
            LOGGER.warn("Error 503 while getting location with event");
            throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE, "Error while accessing location", e);
        }
    }

}