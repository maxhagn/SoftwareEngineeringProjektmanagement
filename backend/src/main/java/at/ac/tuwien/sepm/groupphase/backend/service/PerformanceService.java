package at.ac.tuwien.sepm.groupphase.backend.service;

import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.events.PerformanceOutDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.events.PerformanceSearchDto;
import at.ac.tuwien.sepm.groupphase.backend.entity.Hall;
import at.ac.tuwien.sepm.groupphase.backend.entity.Performance;
import org.springframework.data.domain.Page;
import at.ac.tuwien.sepm.groupphase.backend.entity.Performance;
import at.ac.tuwien.sepm.groupphase.backend.entity.Seat;
import at.ac.tuwien.sepm.groupphase.backend.exception.NotFoundException;

import java.util.List;

public interface PerformanceService {
    /**
     * returns the Performance with the given parameter
     * @param performance search query with values page, event title, hall name, date and price
     * @return Page with Array of PerformanceOutDto's with the given parameter
     */
    Page<PerformanceOutDto> filter(PerformanceSearchDto performance);

    /** @param id of the performance to find
     * @return the Performance if found
     * @throws NotFoundException if performance for given id does not exist
     * */
    Performance find(Long id) throws NotFoundException;

    /**
     * Get all taken seats from performance
     *
     * @param id the performance from which taken seats should be taken
     * @return List of seats which are already taken
     * @throws NotFoundException if there is no performance with given id
     * */
    List<Seat> getTakenSeats(Long id) throws NotFoundException;
}
