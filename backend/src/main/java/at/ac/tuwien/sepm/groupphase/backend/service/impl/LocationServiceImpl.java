package at.ac.tuwien.sepm.groupphase.backend.service.impl;

import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.events.*;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.events.Location.CreateLocationDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.mapper.LocationMapper;
import at.ac.tuwien.sepm.groupphase.backend.entity.*;
import at.ac.tuwien.sepm.groupphase.backend.exception.CreateFailedException;
import at.ac.tuwien.sepm.groupphase.backend.exception.NotFoundException;
import at.ac.tuwien.sepm.groupphase.backend.exception.ValidationException;
import at.ac.tuwien.sepm.groupphase.backend.repository.*;
import at.ac.tuwien.sepm.groupphase.backend.utils.Validator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import at.ac.tuwien.sepm.groupphase.backend.service.LocationService;

import java.lang.invoke.MethodHandles;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class LocationServiceImpl implements LocationService {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private final LocationRepository locationRepository;
    private final LocationMapper locationMapper;
    private final EventRepository eventRepository;
    private final PriceCategoryRepository priceCategoryRepository;
    private final HallRepository hallRepository;
    private final AreaRepository areaRepository;
    private final Validator validator;

    @Autowired
    public LocationServiceImpl(LocationRepository locationRepository, LocationMapper locationMapper, EventRepository eventRepository, PriceCategoryRepository priceCategoryRepository, HallRepository hallRepository, AreaRepository areaRepository, Validator validator) {
        this.locationRepository = locationRepository;
        this.locationMapper = locationMapper;
        this.eventRepository = eventRepository;
        this.priceCategoryRepository = priceCategoryRepository;
        this.hallRepository = hallRepository;
        this.areaRepository = areaRepository;
        this.validator = validator;
    }

    @Override
    public Page<LocationDto> filter(LocationSearchDto locationSearchDto) {
        try {
            validator.validateSearchLocation(locationSearchDto);
            validator.validatePage(locationSearchDto.getPage());
            int entriesPerPage = 12;
            if(locationSearchDto.getSortedBy() == null){
                locationSearchDto.setSortedBy("id");
            }
            if(locationSearchDto.getOrder() == null){
                locationSearchDto.setOrder("asc");
            }
            Pageable pageable = PageRequest.of(locationSearchDto.getPage(), entriesPerPage, Sort.by(locationSearchDto.getSortedBy()).ascending());

            if ( locationSearchDto.getOrder().equals("desc") ) {
                pageable = PageRequest.of(locationSearchDto.getPage(), entriesPerPage, Sort.by(locationSearchDto.getSortedBy()).descending());
            }

            Page<LocationDto> returnPage;
            if (locationSearchDto.getMixedQuery() == null || locationSearchDto.getMixedQuery().equals("")) {
                returnPage = locationRepository.extendedFilter(
                    locationSearchDto.getName(),
                    locationSearchDto.getStreet(),
                    locationSearchDto.getCity(),
                    locationSearchDto.getPlz(),
                    pageable
                );

                if (returnPage.getTotalElements() < entriesPerPage) {
                    pageable = PageRequest.of(locationSearchDto.getPage(), entriesPerPage, Sort.by(locationSearchDto.getSortedBy()).ascending());

                    if ( locationSearchDto.getOrder().equals("desc") ) {
                        pageable = PageRequest.of(locationSearchDto.getPage(), entriesPerPage, Sort.by(locationSearchDto.getSortedBy()).descending());
                    }

                    returnPage = locationRepository.extendedFilter(
                        locationSearchDto.getName(),
                        locationSearchDto.getStreet(),
                        locationSearchDto.getCity(),
                        locationSearchDto.getPlz(),
                        pageable
                    );
                }
            } else {
                returnPage = locationRepository.mixedFilter(
                    locationSearchDto.getMixedQuery(),
                    pageable
                );

                if (returnPage.getTotalElements() < entriesPerPage) {
                    pageable = PageRequest.of(locationSearchDto.getPage(), entriesPerPage, Sort.by(locationSearchDto.getSortedBy()).ascending());

                    if ( locationSearchDto.getOrder().equals("desc") ) {
                        pageable = PageRequest.of(locationSearchDto.getPage(), entriesPerPage, Sort.by(locationSearchDto.getSortedBy()).descending());
                    }

                    returnPage = locationRepository.mixedFilter(
                        locationSearchDto.getMixedQuery(),
                        pageable
                    );
                }
            }

            return returnPage;
        }
        catch ( ValidationException e ) {
            LOGGER.warn("Validation Exception: " + e.getMessage() );
            throw new ValidationException( e.getMessage() );
        }
        catch ( DataAccessException e ) {
            LOGGER.warn("DataAccessException during filtering artists");
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    @Override
    public LocationWithEventsDto getLocationWithEvent(Long id) throws NotFoundException {
        try {
            validator.validateId(id);
            Location location = locationRepository.findById(id).orElse(null);
            if ( location == null ) {
                throw new NotFoundException("Could not find location with id " + id);
            }

            List<Event> events = eventRepository.getEventsByLocationId(id);

            return new LocationWithEventsDto(location.getId(), location.getName(), location.getStreet(), location.getCity(), location.getArea_code(), events);
        }
        catch ( ValidationException e ) {
            LOGGER.warn("Validation Exception: " + e.getMessage() );
            throw new ValidationException( e.getMessage() );
        }
        catch ( DataAccessException e ) {
            LOGGER.warn("DataAccessException during reading location with events");
            throw new RuntimeException(e.getMessage(), e);
        }

    }

    @Override
    public List<LocationWithAreaCodeDto> findAll() {
        try {
            List<Location> all = locationRepository.findAll();
            List<LocationWithAreaCodeDto> dtos = new LinkedList<>();
            for (Location l: all) {
                dtos.add(LocationWithAreaCodeDto.builder()
                    .area_code(l.getArea_code())
                    .id(l.getId())
                    .city(l.getCity())
                    .name(l.getName())
                    .halls(locationMapper.halltoSimpleHallDto(l.getHalls()))
                    .build());
            }
            LOGGER.info("Locations with area code" + Arrays.toString(dtos.toArray()));

            return dtos;
        }
        catch ( ValidationException e ) {
            LOGGER.warn("Validation Exception: " + e.getMessage() );
            throw new ValidationException( e.getMessage() );
        }
        catch ( DataAccessException e ) {
            LOGGER.warn("DataAccessException during reading location with events");
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    @Override
    public void create(CreateLocationDto dto) throws CreateFailedException {
        try {
            validator.validateCreateLocationDto(dto);
        }catch (ValidationException e){
            throw new CreateFailedException(e.getMessage());
        }
        try {
            Map<Long, PriceCategory> categories = new HashMap<>();
            Location l = Location.builder().area_code(dto.getArea_code()).city(dto.getCity()).name(dto.getName()).street(dto.getStreet()).build();
            l = locationRepository.saveAndFlush(l);
            Location finalL = l;
            dto.getCategories().forEach(c -> {
                PriceCategory p = PriceCategory.builder().name(c.getName()).price(c.getPrice()).location(finalL).build();
                p = priceCategoryRepository.saveAndFlush(p);
                categories.put(c.getTmpId(), p);
            });
            Location finalL1 = l;
            dto.getHalls().forEach(h -> {
                Hall hall = Hall.builder().name(h.getName()).cols(h.getCols()).rows(h.getRows()).location(finalL1).build();
                hall = hallRepository.saveAndFlush(hall);
                Hall finalHall = hall;
                h.getAreas().forEach(a -> {
                    Area area = Area.builder().endCol(a.getEndCol()).startCol(a.getStartCol()).endRow(a.getEndRow()).startRow(a.getStartRow()).type(a.getType()).name(a.getName()).hall(finalHall).priceCategory(categories.get(a.getTmpPriceCategoryId())).build();
                    areaRepository.saveAndFlush(area);
                });
            });
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
            throw new RuntimeException(e.getMessage(), e);
        }
    }
}
