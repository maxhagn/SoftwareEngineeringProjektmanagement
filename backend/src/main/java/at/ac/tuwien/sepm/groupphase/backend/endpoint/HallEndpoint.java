package at.ac.tuwien.sepm.groupphase.backend.endpoint;

import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.events.HallDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.mapper.HallMapper;
import at.ac.tuwien.sepm.groupphase.backend.exception.NotFoundException;
import at.ac.tuwien.sepm.groupphase.backend.exception.NotImplementedException;
import at.ac.tuwien.sepm.groupphase.backend.service.HallService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.lang.invoke.MethodHandles;

@RestController
@Api(tags = "Hall Endpoint")
@ApiResponses(value = {
    @ApiResponse(code = 401, message = "You are not authorized to view the resource"),
})
@RequestMapping(value = "/api/v1/hall")
public class HallEndpoint {
    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private final HallMapper hallMapper;
    private final HallService hallService;

    @Autowired
    public HallEndpoint(HallMapper hallMapper, HallService hallService) {
        this.hallMapper = hallMapper;
        this.hallService = hallService;
    }

    @GetMapping(value = "/{id}")
    @ApiResponses(value = {
        @ApiResponse(code = 404, message = "Hall not found")
    })
    @ApiOperation(value = "Get detail information about a specific hall")
    public HallDto find(@PathVariable Long id) throws NotFoundException {
        LOGGER.trace("find Hall: {}", id);
        return hallMapper.hallToHallDto(hallService.findOneById(id));
    }

}
