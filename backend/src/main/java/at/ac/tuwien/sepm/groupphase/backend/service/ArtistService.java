package at.ac.tuwien.sepm.groupphase.backend.service;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.events.ArtistWithEventsDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.events.PreviewArtistDto;
import at.ac.tuwien.sepm.groupphase.backend.entity.Artist;
import org.springframework.data.domain.Page;

import java.util.List;

public interface ArtistService {

    /**
     * This method filters artists according to the following parameters
     * @param artist contains search parameters firstname and lastname
     * @param page contains page with size and pagenumber
     * @return Page of artists that meet the criteria
     */
    Page<PreviewArtistDto> filter(Artist artist, int page);

    /**
     * This method gets one artist with events by id
     * @param id contains the id of the artist
     * @return ArtistWithEventsDto holding a artist and a list of events
     */
    ArtistWithEventsDto getArtistWithEvent(Long id);

    /**
     * This method finds  all artists
     * @return preview artist dto
     */
    List<PreviewArtistDto> findAll();
}
