package at.ac.tuwien.sepm.groupphase.backend.service.impl;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.events.ArtistWithEventsDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.events.PreviewArtistDto;
import at.ac.tuwien.sepm.groupphase.backend.entity.Artist;
import at.ac.tuwien.sepm.groupphase.backend.entity.Event;
import at.ac.tuwien.sepm.groupphase.backend.exception.NotFoundException;
import at.ac.tuwien.sepm.groupphase.backend.exception.ValidationException;
import at.ac.tuwien.sepm.groupphase.backend.repository.ArtistRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.EventRepository;
import at.ac.tuwien.sepm.groupphase.backend.service.ArtistService;
import at.ac.tuwien.sepm.groupphase.backend.utils.Validator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.lang.invoke.MethodHandles;
import java.util.LinkedList;
import java.util.List;

@Service
public class ArtistServiceImpl implements ArtistService {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private final ArtistRepository artistRepository;
    private final EventRepository eventRepository;
    private final Validator validator;

    @Autowired
    public ArtistServiceImpl(ArtistRepository artistRepository, EventRepository eventRepository, Validator validator) {
        this.artistRepository = artistRepository;
        this.eventRepository = eventRepository;
        this.validator = validator;
    }

    @Override
    public Page<PreviewArtistDto> filter(Artist artist, int page) {
        try {
            validator.validateSearchArtist(artist);
            validator.validatePage(page);

            int entriesPerPage = 12;
            Pageable pageable = PageRequest.of(page,entriesPerPage);
            Page<PreviewArtistDto> returnPage;

            returnPage = artistRepository.filter(
                artist.getFirstname(),
                artist.getSurname(),
                pageable
            );

            if ( returnPage.getTotalElements() < entriesPerPage ) {
                pageable = PageRequest.of(0,entriesPerPage);
                returnPage = artistRepository.filter(
                    artist.getFirstname(),
                    artist.getSurname(),
                    pageable
                );
            }

            return returnPage;
        } catch ( ValidationException e ) {
            LOGGER.warn("Validation Exception: " + e.getMessage() );
            throw new ValidationException( e.getMessage() );
        }
        catch ( DataAccessException e ) {
            LOGGER.warn("DataAccessException during filtering artists");
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    @Override
    public ArtistWithEventsDto getArtistWithEvent(Long id) {
        try {
            validator.validateId(id);
            Artist artist = artistRepository.findById(id).orElse(null);
            if ( artist == null ) {
                throw new NotFoundException("Could not find artist with id " + id);
            }

            List<Event> events = eventRepository.getEventsByArtistId(id);

            return  new ArtistWithEventsDto(artist.getId(), artist.getFirstname(), artist.getSurname(), events);
        }
        catch ( ValidationException e ) {
            LOGGER.warn("Validation Exception: " + e.getMessage() );
            throw new ValidationException( e.getMessage() );
        }
        catch ( DataAccessException e ) {
            LOGGER.warn("DataAccessException during reading artist with events");
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    @Override
    public List<PreviewArtistDto> findAll() {
        try {

            List<Artist> all = artistRepository.findAll();
            List<PreviewArtistDto> dtos = new LinkedList<>();
            for (Artist a: all) {
                dtos.add(PreviewArtistDto.builder()
                .firstname(a.getFirstname())
                .surname(a.getSurname())
                .id(a.getId())
                .build());
            }
            return dtos;

        }
        catch ( ValidationException e ) {
            LOGGER.warn("Validation Exception: " + e.getMessage() );
            throw new ValidationException( e.getMessage() );
        }
        catch ( DataAccessException e ) {
            LOGGER.warn("DataAccessException during reading artist with events");
            throw new RuntimeException(e.getMessage(), e);
        }
    }
}
