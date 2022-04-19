package at.ac.tuwien.sepm.groupphase.backend.service;

import at.ac.tuwien.sepm.groupphase.backend.entity.Hall;
import at.ac.tuwien.sepm.groupphase.backend.exception.NotFoundException;

public interface HallService {

    /**
     * Returns a Hall with a given id or a NotFoundException if
     * no hall with the given id exists
     *
     * @throws NotFoundException if not found
     * @param id of the hall to search for
     * @return The Hall with the given id
     */
    Hall findOneById(Long id) throws NotFoundException;
}
