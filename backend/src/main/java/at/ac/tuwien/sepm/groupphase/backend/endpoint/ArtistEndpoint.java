package at.ac.tuwien.sepm.groupphase.backend.endpoint;

import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.events.ArtistWithEventsDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.events.PreviewArtistDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.mapper.ArtistMapper;
import at.ac.tuwien.sepm.groupphase.backend.exception.NotFoundException;
import at.ac.tuwien.sepm.groupphase.backend.service.ArtistService;
import io.swagger.annotations.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.lang.invoke.MethodHandles;
import java.util.List;

@RestController
@Api(tags = "Artist Endpoint")
@RequestMapping(value = "/api/v1/artists")
@ApiResponses(value = {
    @ApiResponse(code = 401, message = "You are not authorized to view the resource"),
})
public class ArtistEndpoint {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private final ArtistService artistService;
    private final ArtistMapper artistMapper;

    @Autowired
    public ArtistEndpoint(ArtistService artistService, ArtistMapper artistMapper) {
        this.artistService = artistService;
        this.artistMapper = artistMapper;
    }


    @GetMapping(value = "/")
    @ApiImplicitParams({
        @ApiImplicitParam(name = "query", paramType = "path"),
        @ApiImplicitParam(name = "page", paramType = "path")
    })
    @ApiOperation(value = "Search through all artists by first and last names")
    public Page<PreviewArtistDto> filter(String query, int page) {

        //try {
            LOGGER.info("GET ARTISTS BY QUERY ON PAGE " + page);
            PreviewArtistDto artist = new PreviewArtistDto();
            artist.setFirstname(query);
            artist.setSurname(query);
            return artistService.filter(artistMapper.dtoToEntity(artist), page);

        /*} catch (NotFoundException e) {
            LOGGER.warn("Error 404 while filtering artists");
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Error during reading artist", e);

        } catch (RuntimeException e) {
            LOGGER.warn("Error 503 while filtering artists");
            throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE, "Error while accessing artist", e);
        }*/
    }

    @GetMapping(value = "/{id}")
    @ApiResponses(value = {
        @ApiResponse(code = 404, message = "Artist not found")
    })
    @ApiOperation(value = "Get details including events to a specific artist")
    public ArtistWithEventsDto find(@PathVariable Long id) {
        //try {
            LOGGER.info("GET ARTISTS WITH EVENT BY ID " + id);
            return artistService.getArtistWithEvent(id);

       /* } catch (NotFoundException e) {
            LOGGER.warn("Error 404 while getting artist with event");
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Error during reading artist", e);

        } catch (RuntimeException e) {
            LOGGER.warn("Error 503 while getting artist with event");
            throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE, "Error while accessing artist", e);
        }*/
    }

    @GetMapping(value = "/list")
    @ApiResponses(value = {
        @ApiResponse(code = 404, message = "Location not found")
    })
    @ApiOperation(value = "Get all locations with area code")
    public List<PreviewArtistDto> findAll() {
       // try {
            LOGGER.info("Get all locations with area code");
            return artistService.findAll();
        /*} catch (NotFoundException e) {
            LOGGER.warn("Error 404 while getting location with event");
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Error during reading location", e);

        } catch (RuntimeException e) {
            LOGGER.warn("Error 503 while getting location with event");
            throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE, "Error while accessing location", e);
        }*/
    }
}
