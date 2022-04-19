package at.ac.tuwien.sepm.groupphase.backend.endpoint;

import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.seat.SeatDisplayDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.seat.SeatSelectDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.seat.SeatUnselectDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.ticket.TicketCheckoutDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.ticket.TicketPreviewDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.ticket.TicketSearchDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.mapper.TicketMapper;
import at.ac.tuwien.sepm.groupphase.backend.entity.Ticket;
import at.ac.tuwien.sepm.groupphase.backend.entity.User;
import at.ac.tuwien.sepm.groupphase.backend.exception.NotAuthorizedException;
import at.ac.tuwien.sepm.groupphase.backend.exception.NotFoundException;
import at.ac.tuwien.sepm.groupphase.backend.exception.NotImplementedException;
import at.ac.tuwien.sepm.groupphase.backend.security.MyAuthUser;
import at.ac.tuwien.sepm.groupphase.backend.service.TicketService;
import at.ac.tuwien.sepm.groupphase.backend.service.UserService;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.ticket.TicketQueueDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.mapper.SeatDisplayMapper;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.mapper.TicketCheckoutMapper;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.mapper.TicketQueueMapper;
import at.ac.tuwien.sepm.groupphase.backend.exception.*;
import io.swagger.annotations.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.io.FileInputStream;
import java.io.IOException;
import java.lang.invoke.MethodHandles;
import javax.validation.Valid;
import java.util.List;

@RestController
@Api(tags = "Ticket Endpoint")
@ApiResponses(value = {
    @ApiResponse(code = 401, message = "You are not authorized to view the resource"),
})
@RequestMapping(value = "/api/v1/ticket")
public class TicketEndpoint {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private final TicketMapper ticketMapper;
    private final TicketService ticketService;
    private final UserService userService;
    private final TicketQueueMapper ticketQueueMapper;
    private final TicketCheckoutMapper ticketCheckoutMapper;
    private final SeatDisplayMapper seatDisplayMapper;

    @Autowired
    public TicketEndpoint(TicketMapper ticketMapper, TicketService ticketService, UserService userService, TicketQueueMapper ticketQueueMapper, TicketCheckoutMapper ticketCheckoutMapper, SeatDisplayMapper seatDisplayMapper) {
        this.ticketMapper = ticketMapper;
        this.ticketService = ticketService;
        this.userService = userService;
        this.ticketQueueMapper = ticketQueueMapper;
        this.ticketCheckoutMapper = ticketCheckoutMapper;
        this.seatDisplayMapper = seatDisplayMapper;
    }

    @GetMapping(value = "/{id}")
    @ApiResponses(value = {
        @ApiResponse(code = 401, message = "Invalid Credentials supported"),
        @ApiResponse(code = 404, message = "Not Found")
    })
    @ApiOperation(value = "Loads a Ticket")
    public TicketPreviewDto loadTicket(@PathVariable long id, Authentication authentication) throws NotAuthorizedException, NotFoundException {
        LOGGER.info("Searching ticket " + id);
        MyAuthUser u = new MyAuthUser(userService, authentication.getPrincipal());
        if (u.isAllowedToAccessTicket(id)) {
            Ticket t = ticketService.find(id);
            return ticketMapper.entityToPreviewDto(t);
        }
        return null;
        //never reached because error is thrown
    }

    @PatchMapping(value = "/{id}/buy")
    @ApiResponses(value = {
        @ApiResponse(code = 401, message = "Invalid Credentials supported"),
        @ApiResponse(code = 422, message = "Invalid Request")
    })
    @ApiOperation(value = "Buys a ticket")
    public void buy(@PathVariable Long id) throws NotFoundException, NotAllowedException, ValidationException {
        LOGGER.info("PATCH buy ticket with id: " + id);
        this.ticketService.buyTicket(id);
    }

    @GetMapping(value = "/{id}/invoice", produces = MediaType.APPLICATION_PDF_VALUE)
    @ApiResponses(value = {
        @ApiResponse(code = 401, message = "Invalid Credentials supported"),
        @ApiResponse(code = 422, message = "Invalid Request")
    })
    @ApiOperation(value = "Returns the invoice to a ticket")
    public byte[] invoice(@PathVariable Long id, Authentication authentication) throws NotAuthorizedException, IOException {
        MyAuthUser u = new MyAuthUser(userService, authentication.getPrincipal());
        if (u.isAllowedToAccessTicket(id)) {
            //TODO after merge: getPath and generate on submit
            String path = ticketService.generateInvoice(id);
            return new FileInputStream(path).readAllBytes();
        }
        return null;
    }

    @PatchMapping(value = "/{id}/reserve")
    @ApiResponses(value = {
        @ApiResponse(code = 401, message = "Invalid Credentials supported"),
        @ApiResponse(code = 422, message = "Invalid Request")
    })
    @ApiOperation(value = "Reserves a ticket")
    public void reserve(@PathVariable Long id) throws NotFoundException, NotAllowedException, ValidationException {
        LOGGER.info("PATCH reserve ticket with id: " + id);
        this.ticketService.reserveTicket(id);
    }

    @GetMapping(value = "/{id}/storno")
    @ApiResponses(value = {
        @ApiResponse(code = 401, message = "Invalid Credentials supported"),
        @ApiResponse(code = 422, message = "Invalid Request")
    })
    @ApiOperation(value = "Stornos a ticket")
    public byte[] storno(@PathVariable Long id, Authentication authentication) throws NotAuthorizedException, IOException {
        MyAuthUser u = new MyAuthUser(userService, authentication.getPrincipal());
        if (u.isAllowedToAccessTicket(id)) {
            String path = ticketService.getCancellationInvoicePath(id);
            return new FileInputStream(path).readAllBytes();
        }
        //never reached because error is thrown
        return null;
    }

    @GetMapping(value = "/")
    @ApiImplicitParams({
        @ApiImplicitParam(name = "status", paramType = "path"),
        @ApiImplicitParam(name = "page", paramType = "path")
    })

    @ApiOperation(value = "Get a list of all your tickets")
    public Page<Ticket> list(TicketSearchDto ticketSearchDto, Authentication authentication) {
        try {
            LOGGER.info("GET TICKETS BY QUERY ON PAGE " + ticketSearchDto.getPage());
            MyAuthUser authUser = new MyAuthUser(userService, authentication.getPrincipal());
            User user = authUser.getCurrentUser();
            ticketSearchDto.setUserId(user.getId());
            return ticketService.filter(ticketSearchDto);
        } catch (NotFoundException e) {
            LOGGER.warn("Error 404 while filtering locations");
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Error during reading tickets", e);

        } catch (RuntimeException e) {
            LOGGER.warn("Error 503 while filtering locations");
            throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE, "Error while accessing tickets", e);
        }
    }

    @PostMapping(value = "/{id}/queue")
    @ApiImplicitParams({
        @ApiImplicitParam(name = "id", paramType = "path")
    })
    @ApiOperation(value = "Get ticket for current user that are in queue or create a new one if not existing for the given performance")
    public TicketQueueDto getOrCreateQueuedTicket(@PathVariable long id) throws NotFoundException, ValidationException {
        LOGGER.info("POST get or create queued ticket by id: " + id);
        return ticketQueueMapper.entityToTicketQueueDto(ticketService.getOrCreateQueuedTicket(id));
    }

    @PostMapping(value = "/select")
    @ApiOperation(value = "Try to select a seat or area if it is available")
    public void selectSeat(@Validated @RequestBody SeatSelectDto seatSelectDto) throws SeatNotAvailableException, ValidationException, NotFoundException, NotAllowedException {
        LOGGER.info("POST try to select a seat or area with: " + seatSelectDto);
        ticketService.selectSeat(seatSelectDto);
    }

    @PostMapping(value = "/unselect")
    @ApiOperation(value = "Try to unselect a seat or area if it is available")
    public void unselectSeat(@Valid @RequestBody SeatUnselectDto seatUnselectDto) throws NotFoundException, ValidationException, NotAllowedException {
        LOGGER.info("POST try to unselect a seat or area with: " + seatUnselectDto);
        ticketService.unselectSeat(seatUnselectDto);
    }

    @GetMapping(value = "/{id}/checkout")
    @ApiImplicitParams({
        @ApiImplicitParam(name = "id", paramType = "path")
    })
    @ApiOperation(value = "Get overview of the given ticket before checkout")
    public TicketCheckoutDto checkoutInfo(@PathVariable long id) throws NotFoundException, NotAllowedException, ValidationException {
        LOGGER.info("GET checkout info for ticket with id: " + id);
        return ticketCheckoutMapper.entityToTicketCheckoutDto(ticketService.getCheckoutInfo(id));
    }

    @GetMapping(value = "/{id}/seats")
    @ApiImplicitParams({
        @ApiImplicitParam(name = "id", paramType = "path")
    })
    @ApiOperation(value = "Get all selected seats from ticket with given id")
    public List<SeatDisplayDto> getSelectedSeats(@PathVariable long id) throws NotFoundException, NotAllowedException, ValidationException {
        LOGGER.info("GET selected seats for ticket with id: " + id);
        return seatDisplayMapper.entityToSeatDisplayDtoCollection(ticketService.getSelectedSeats(id));
    }

    @PostMapping(value = "/{id}/cancelReserved")
    @ApiImplicitParams({
        @ApiImplicitParam(name = "id", paramType = "path")
    })
    @ApiOperation(value = "Cancel reservation for ticket with given id")
    public void cancelReservation(@PathVariable long id) throws NotFoundException, NotAllowedException, ValidationException {
        LOGGER.info("POST cancel reservation for ticket with id: " + id);
        ticketService.cancelReservedTicket(id);
    }

    @PostMapping(value = "/{id}/cancelBought")
    @ApiImplicitParams({
        @ApiImplicitParam(name = "id", paramType = "path")
    })
    @ApiOperation(value = "Cancel bought ticket with given id")
    public void cancelBoughtTicket(@PathVariable long id) throws NotFoundException, NotAllowedException, ValidationException {
            LOGGER.info("POST cancel bought ticket with id: " + id);
            ticketService.cancelBoughtTicket(id);
    }
}
