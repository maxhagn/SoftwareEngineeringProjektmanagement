package at.ac.tuwien.sepm.groupphase.backend.service;

import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.seat.SeatSelectDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.seat.SeatUnselectDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.ticket.TicketSearchDto;
import at.ac.tuwien.sepm.groupphase.backend.entity.Seat;
import at.ac.tuwien.sepm.groupphase.backend.entity.Ticket;
import at.ac.tuwien.sepm.groupphase.backend.exception.NotAllowedException;
import at.ac.tuwien.sepm.groupphase.backend.exception.NotFoundException;
import at.ac.tuwien.sepm.groupphase.backend.exception.SeatNotAvailableException;
import at.ac.tuwien.sepm.groupphase.backend.exception.ValidationException;
import org.springframework.data.domain.Page;

import java.util.List;

public interface TicketService {

    /**
     * Generates an Invoice and stores it on the server.
     * @param id of the ticket
     * @return path of the invoice or null if an error occured
     */
    String generateInvoice(Long id);

    /**
     * Returns the path for an invoice
     * @param id of the ticket
     * @return path of the invoice
     */
    String getInvoicePath(Long id);

    /**
     * Returns the path for a cancellation invoice
     * @param id of the ticket
     * @return path of the invoice
     */
    String getCancellationInvoicePath(Long id);

    /**
     * Searches for a Ticket in the Database
     * @param id of the ticket
     * @return the ticket found
     * @throws at.ac.tuwien.sepm.groupphase.backend.exception.NotFoundException
     */
    Ticket find(long id) throws NotFoundException;

    /**
     * returns the tickets with the given parameter
     * @param ticketSearchDto search query with values status and user id
     * @return Page with Array of Tickets with the given parameter
     */
    Page<Ticket> filter(TicketSearchDto ticketSearchDto);

    /** @param id of the performance of which we should get or create a new ticket
     * @return the TicketQueueDto which belongs to the current user to the given performance
     * @throws NotFoundException if no performance exists with the given id
     * @throws ValidationException if the requested performance is in the past
     */
    Ticket getOrCreateQueuedTicket(Long id) throws NotFoundException, ValidationException;

    /**
     * Try to select a specific seat or a seat inside an area
     *
     * @param seatSelectDto infos about which seat should be selected
     * @throws SeatNotAvailableException if the requested seat is already taken
     * @throws NotFoundException if the request seat or area does not exist
     * @throws NotAllowedException if the user is not allowed to select seats for the specified ticket
     * @throws ValidationException if the given info for the seat to select is invalid
     * */
    void selectSeat(SeatSelectDto seatSelectDto) throws SeatNotAvailableException, NotFoundException, ValidationException, NotAllowedException;

    /**
     * Try to unselect a specific seat or area
     *
     * @param seatUnselectDto infos about which seat should be selected
     * @throws NotFoundException if the request seat or area does not exist or wasn't selected at first
     * @throws ValidationException if the given info for the seat to unselect is invalid
     * @throws NotAllowedException if the current user is not allowed to access the ticket
     * */
    void unselectSeat(SeatUnselectDto seatUnselectDto) throws NotFoundException, ValidationException, NotAllowedException;

    /**
     * Get information about a ticket for checkout
     *
     * @param id of the ticket to get information about
     * @throws NotFoundException if the request ticket with id does not exist
     * @throws NotAllowedException if the current user is not allowed to access the ticket
     * @throws ValidationException if the performance of the ticket is in the past
     * */
    Ticket getCheckoutInfo(Long id) throws NotFoundException, NotAllowedException, ValidationException;

    /**
     * Buys a ticket by changing its status
     *
     * @param id of the ticket to buy
     * @throws NotFoundException if the request ticket with id does not exist
     * @throws NotAllowedException if the current user is not allowed to access the ticket
     * @throws ValidationException if the given ticket is not reserved nor in queue
     * */
    void buyTicket(Long id) throws NotFoundException, NotAllowedException, ValidationException;

    /**
     * Reserves a ticket by changing its status
     *
     * @param id of the ticket to reserve
     * @throws NotFoundException if the request ticket with id does not exist
     * @throws NotAllowedException if the current user is not allowed to access the ticket
     * @throws ValidationException if the given ticket is not in queue
     * */
    void reserveTicket(Long id) throws NotFoundException, NotAllowedException, ValidationException;

    /**
     * Get all selected seats from ticket
     *
     * @param id the ticket from which selected seats should be taken
     * @return List of seats which are selected
     * @throws NotFoundException if there is no ticket with given id
     * @throws NotAllowedException if the current user is not allowed to access the ticket
     * @throws ValidationException if the Ticket is not valid
     * */
    List<Seat> getSelectedSeats(Long id) throws NotFoundException, NotAllowedException, ValidationException;

    /**
     * Cancel reservation for given ticket
     *
     * @param id of the ticket to cancel
     * @throws NotFoundException if there is no ticket with given id
     * @throws NotAllowedException if the current user is not allowed to access the ticket
     * @throws ValidationException if the given ticket is not reserved
     * */
    void cancelReservedTicket(Long id) throws NotFoundException, NotAllowedException, ValidationException;

    /**
     * generates the file containing the cancellation invoice
     * @param id the ticket id
     */
    void generateCancellationInvoice(Long id);

    /**
     * Cancel bought ticket
     *
     * @param id of the ticket to cancel
     * @throws NotFoundException if there is no ticket with given id
     * @throws NotAllowedException if the current user is not allowed to access the ticket
     * @throws ValidationException if the given ticket is not bought
     * */
    void cancelBoughtTicket(Long id) throws NotFoundException, NotAllowedException, ValidationException;
}
