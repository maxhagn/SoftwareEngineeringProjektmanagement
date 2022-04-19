package at.ac.tuwien.sepm.groupphase.backend.service.impl;

import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.events.PerformanceOutDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.events.PerformanceSearchDto;
import at.ac.tuwien.sepm.groupphase.backend.entity.Performance;
import at.ac.tuwien.sepm.groupphase.backend.entity.Seat;
import at.ac.tuwien.sepm.groupphase.backend.entity.enums.Status;
import at.ac.tuwien.sepm.groupphase.backend.exception.NotFoundException;
import at.ac.tuwien.sepm.groupphase.backend.exception.ValidationException;
import at.ac.tuwien.sepm.groupphase.backend.repository.PerformanceRepository;
import at.ac.tuwien.sepm.groupphase.backend.service.PerformanceService;
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
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class PerformanceServiceImpl implements PerformanceService {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private final PerformanceRepository performanceRepository;
    private final Validator validator;

    @Autowired
    public PerformanceServiceImpl(PerformanceRepository performanceRepository, Validator validator) {
        this.performanceRepository = performanceRepository;
        this.validator = validator;
    }

    @Override
    public Page<PerformanceOutDto> filter(PerformanceSearchDto performance) {
        try {
            validator.validateSearchPerformance(performance);
            validator.validatePage(performance.getPage());
            int entriesPerPage = 6;
            Pageable pageable = PageRequest.of(performance.getPage(), entriesPerPage);
            Page<PerformanceOutDto> returnPage;
            if (performance.getMixedQuery() == null || performance.getMixedQuery().equals("")) {
                LocalDateTime fromDateTime = null;
                LocalDateTime toDateTime = null;
                LOGGER.info(performance.getDateTime() + "");
                if (performance.getDateTime() != null) {
                    fromDateTime = performance.getDateTime().minusDays(3);
                    toDateTime = performance.getDateTime().plusDays(3);
                }
                returnPage = performanceRepository.extendedFilter(
                    fromDateTime,
                    toDateTime,
                    performance.getEventTitle(),
                    performance.getHallName(),
                    (int) performance.getPrice(),
                    pageable
                );

                if (returnPage.getTotalElements() < entriesPerPage) {
                    pageable = PageRequest.of(0, entriesPerPage);
                    returnPage = performanceRepository.extendedFilter(
                        fromDateTime,
                        toDateTime,
                        performance.getEventTitle(),
                        performance.getHallName(),
                        (int) performance.getPrice(),
                        pageable
                    );
                }
            } else {
                LOGGER.info("here in mixed not null");
                returnPage = performanceRepository.mixedFilter(
                    performance.getMixedQuery(),
                    pageable
                );
                if (returnPage.getTotalElements() < entriesPerPage) {
                    pageable = PageRequest.of(0, entriesPerPage);
                    returnPage = performanceRepository.mixedFilter(
                        performance.getMixedQuery(),
                        pageable
                    );
                }
            }

            return returnPage;
        } catch (ValidationException e) {
            LOGGER.warn("Validation Exception: " + e.getMessage());
            throw new ValidationException(e.getMessage());
        } catch (DataAccessException e) {
            LOGGER.warn("DataAccessException during filtering artists");
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    @Override
    public Performance find(Long id) throws NotFoundException {
        LOGGER.info("Find performance for id: " + id);
        Optional<Performance> found = performanceRepository.findById(id);
        if (found.isPresent()) {
            return found.get();
        }
        throw new NotFoundException(String.format("Could not find the performance with id %s", id));
    }

    @Override
    public List<Seat> getTakenSeats(Long id) throws NotFoundException {
        LOGGER.info("Find taken seats for performance with id: " + id);
        Optional<Performance> foundPerformance = performanceRepository.findById(id);
        if (foundPerformance.isEmpty()) {
            throw new NotFoundException("The given id does not belong to a valid performance.");
        }
        return foundPerformance.get().getTickets().stream().filter(t -> t.getStatus() != Status.CANCELLED && t.getStatus() != Status.REFUNDED).flatMap(t -> t.getSeats().stream()).collect(Collectors.toList());
    }
}
