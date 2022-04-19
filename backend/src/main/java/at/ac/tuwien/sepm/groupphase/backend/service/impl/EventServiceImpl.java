package at.ac.tuwien.sepm.groupphase.backend.service.impl;

import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.events.TopEventDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.events.TopEventQueryDto;
import at.ac.tuwien.sepm.groupphase.backend.entity.Event;
import at.ac.tuwien.sepm.groupphase.backend.entity.Performance;
import at.ac.tuwien.sepm.groupphase.backend.entity.enums.Category;
import at.ac.tuwien.sepm.groupphase.backend.exception.CreateFailedException;
import at.ac.tuwien.sepm.groupphase.backend.exception.NotFoundException;
import at.ac.tuwien.sepm.groupphase.backend.exception.ValidationException;
import at.ac.tuwien.sepm.groupphase.backend.repository.EventRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.PerformanceRepository;
import at.ac.tuwien.sepm.groupphase.backend.service.EventService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Service;

import at.ac.tuwien.sepm.groupphase.backend.utils.Validator;
import java.lang.invoke.MethodHandles;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service

public class EventServiceImpl implements EventService {
    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private final EventRepository eventRepository;
    private final Validator validator;
    private final PerformanceRepository performanceRepository;
    public EventServiceImpl(EventRepository eventRepository, Validator validator, PerformanceRepository performanceRepository) {
        this.eventRepository = eventRepository;
        this.validator = validator;
        this.performanceRepository = performanceRepository;
    }

    public Page<Event> filter(Event event, int page) {

        LOGGER.info("Inside filter Service now");

        try {
            //validator.validateEvent(event);
            validator.validatePage(page);

            int entriesPerPage = 12;
            Pageable pageable = PageRequest.of(page,entriesPerPage);
            Page<Event> returnPage;


            returnPage = eventRepository.filterMixed(
              event.getTitle(),
                pageable
            );

            LOGGER.info(event.getTitle() + page);

            if ( returnPage.getTotalElements() < entriesPerPage ) {
                pageable = PageRequest.of(0,entriesPerPage);
                returnPage = eventRepository.filterMixed(
                    event.getTitle(),
                    pageable
                );
            }

            return returnPage;
        } catch ( ValidationException e ) {
            LOGGER.warn("Validation Exception: " + e.getMessage() );
            throw new ValidationException( e.getMessage() );
        }
        catch ( DataAccessException e ) {
            LOGGER.warn("DataAccessException during filtering events");
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    public Page<Event> filterDetailed(Event event, int page, String order, String sortedBy) {

        LOGGER.info("Inside detailed filter Service now");
        if(event.getCategory() != null){
            LOGGER.info("Category: " + event.getCategory().toString());
        }
        try {
            //validator.validateEvent(event);
            validator.validatePage(page);

            if( sortedBy == null){
                sortedBy = "id";
            }
            if( order == null){
                order = "asc";
            }

            LOGGER.info(sortedBy+order);

            int entriesPerPage = 12;

            Pageable pageable = PageRequest.of(page,entriesPerPage, Sort.by(sortedBy).ascending());

            if ( order.equals("desc") ) {
                pageable = PageRequest.of(page,entriesPerPage, Sort.by(sortedBy).descending());
            }

            Page<Event> returnPage;

            returnPage = eventRepository.filterDetailed(
                event.getTitle(),event.getDuration(),event.getDescription(),event.getCategory(),
                pageable
            );

            LOGGER.info(event.getTitle() + page);

            if ( returnPage.getTotalElements() < entriesPerPage ) {
                pageable = PageRequest.of(0,entriesPerPage, Sort.by(sortedBy).ascending());

                if ( order.equals("desc") ) {
                    pageable = PageRequest.of(0,entriesPerPage, Sort.by(sortedBy).descending());
                }

                returnPage = eventRepository.filterDetailed(
                    event.getTitle(),event.getDuration(),event.getDescription(), event.getCategory(),
                    pageable
                );
            }

            return returnPage;
        } catch ( ValidationException e ) {
            LOGGER.warn("Validation Exception: " + e.getMessage() );
            throw new ValidationException( e.getMessage() );
        }
        catch ( DataAccessException e ) {
            LOGGER.warn("DataAccessException during filtering events");
            throw new RuntimeException(e.getMessage(), e);
        }
    }


    @Override
    public Event find(long id) throws NotFoundException {
        try {
            return eventRepository.findById(id).get();
        }catch (Exception e){
            LOGGER.error(e.getMessage());
            throw new NotFoundException("Event does not exist");
        }
    }
    @Override
    public List<TopEventDto> getTop(TopEventQueryDto topEventQueryDto) {

        Category category = Category.valueOf(topEventQueryDto.getCategory());

        String str = topEventQueryDto.getDate();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate initial = LocalDate.parse(str, formatter);
        LocalDateTime begin = initial.withDayOfMonth(1).atStartOfDay();
        LocalDateTime end = initial.withDayOfMonth(initial.lengthOfMonth()).atStartOfDay();

        LOGGER.info("Service: " + category.name() + " " + begin.toString() + " " + end.toString());

        LocalDateTime dateTime = LocalDateTime.now();

        List<TopEventDto> result = eventRepository.getTopEvents(category,begin,end);

        if(result.size() > 10){
            return result.subList(0,10);
        }
        else {
            return result;
        }
    }

    @Override
    public List<Event> findAll() {
        return eventRepository.findAll();
    }

    @Override
    public Event create(Event event) {
        Event retVal = null;
        if(event != null){
            retVal = eventRepository.save(event);
        }

        return retVal;}

    @Override
    public Event createEventAndPerformances(Event event) throws CreateFailedException {
        try{validator.validateCreateEvent(event);}catch (ValidationException e){
            throw new CreateFailedException(e.getMessage());
        }
        Event out = eventRepository.save(event);
        for(Performance p : event.getPerformances()){
            p.setEvent(out);
            LOGGER.info("trying to save performance:"+p.toString());
            performanceRepository.save(p);
        }
        return out;
    }
}
