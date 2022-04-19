package at.ac.tuwien.sepm.groupphase.backend.endpoint;

import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.events.PerformanceOutDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.events.PerformanceSearchDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.performance.PerformanceDetailDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.seat.SeatDisplayDto;
import at.ac.tuwien.sepm.groupphase.backend.exception.NotFoundException;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.mapper.PerformanceDetailMapper;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.mapper.SeatDisplayMapper;
import at.ac.tuwien.sepm.groupphase.backend.service.PerformanceService;
import io.swagger.annotations.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.lang.invoke.MethodHandles;
import java.util.List;

@RestController
@Api(tags = "Performance Endpoint")
@ApiResponses(value = {
    @ApiResponse(code = 401, message = "You are not authorized to view the resource"),
})
@RequestMapping(value = "/api/v1/performance")
public class PerformanceEndpoint {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private final PerformanceService performanceService;
    private final PerformanceDetailMapper performanceDetailMapper;
    private final SeatDisplayMapper seatDisplayMapper;

    @Autowired
    public PerformanceEndpoint(PerformanceService performanceService, PerformanceDetailMapper performanceDetailMapper, SeatDisplayMapper seatDisplayMapper) {
        this.performanceService = performanceService;
        this.performanceDetailMapper = performanceDetailMapper;
        this.seatDisplayMapper = seatDisplayMapper;
    }

    @GetMapping(value = "/")
    @ApiImplicitParams({
        @ApiImplicitParam(name = "mixedQuery", paramType = "path"),
        @ApiImplicitParam(name = "dateTime", paramType = "path"),
        @ApiImplicitParam(name = "price", paramType = "path"),
        @ApiImplicitParam(name = "event", paramType = "path"),
        @ApiImplicitParam(name = "hall", paramType = "path"),
        @ApiImplicitParam(name = "page", paramType = "path")
    })
    @ApiOperation(value = "Search through all performances by given parameters")
    public Page<PerformanceOutDto> filter(PerformanceSearchDto performance) {


        try {

            LOGGER.info("GET PERFORMANCES BY QUERY ON PAGE " + performance.getPage());
            return performanceService.filter(performance);

        } catch (NotFoundException e) {
            LOGGER.warn("Error 404 while filtering performances");
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Error during reading performances", e);

        } catch (RuntimeException e) {
            LOGGER.warn("Error 503 while filtering performances");
            throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE, "Error while accessing performances", e);
        }

    }

    @GetMapping(value = "/{id}")
    @ApiResponses(value = {
        @ApiResponse(code = 404, message = "Performance not found")
    })
    @ApiOperation(value = "Get detail information about a specific performance")
    public PerformanceDetailDto find(@PathVariable Long id) throws NotFoundException {
        LOGGER.info("GET performance by id: " + id);
        return performanceDetailMapper.entityToPerformanceDetailDto(performanceService.find(id));
    }

    @GetMapping(value = "/{id}/taken")
    @ApiImplicitParams({
        @ApiImplicitParam(name = "id", paramType = "path")
    })
    @ApiOperation(value = "Get all taken seats from ticket with given id")
    public List<SeatDisplayDto> getTakenSeats(@PathVariable long id) throws NotFoundException {
        LOGGER.info("GET taken seats by performance id: " + id);
        return seatDisplayMapper.entityToSeatDisplayDtoCollection(performanceService.getTakenSeats(id));
    }
}