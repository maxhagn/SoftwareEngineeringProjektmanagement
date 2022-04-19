package at.ac.tuwien.sepm.groupphase.backend.service.impl;

import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.seat.SeatSelectDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.seat.SeatUnselectDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.ticket.TicketSearchDto;
import at.ac.tuwien.sepm.groupphase.backend.entity.*;
import at.ac.tuwien.sepm.groupphase.backend.entity.enums.Status;
import at.ac.tuwien.sepm.groupphase.backend.entity.enums.Type;
import at.ac.tuwien.sepm.groupphase.backend.exception.*;
import at.ac.tuwien.sepm.groupphase.backend.repository.PerformanceRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.SeatRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.TicketRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.UserRepository;
import at.ac.tuwien.sepm.groupphase.backend.service.TicketService;
import at.ac.tuwien.sepm.groupphase.backend.utils.CancellationInvoicePdfCreator;
import at.ac.tuwien.sepm.groupphase.backend.utils.InvoicePdfCreator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.lang.invoke.MethodHandles;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

import static at.ac.tuwien.sepm.groupphase.backend.entity.enums.Status.*;

@Service
public class TicketServiceImpl implements TicketService {
    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private final TicketRepository ticketRepository;
    private final PerformanceRepository performanceRepository;
    private final UserRepository userRepository;
    private final SeatRepository seatRepository;

    @Autowired
    public TicketServiceImpl(TicketRepository ticketRepository, PerformanceRepository performanceRepository, UserRepository userRepository, SeatRepository seatRepository) {
        this.ticketRepository = ticketRepository;
        this.performanceRepository = performanceRepository;
        this.userRepository = userRepository;
        this.seatRepository = seatRepository;
    }

    @Override
    public String generateInvoice(Long id) {
        Optional<Ticket> ticketOpt = ticketRepository.findById(id);
        if(ticketOpt.isEmpty()){
            throw new NotFoundException("Ticket not found");
        }
        Ticket ticket = ticketOpt.get();
        try{
            String path = "files/invoices/" + "buy"+id +".pdf";
            InvoicePdfCreator.createPdf(path, ticket);
            return path;
        } catch (Exception e){
            LOGGER.error("Error generating invoice for id "+id+":"+e.getMessage());
        }
        return null;
    }

    @Override
    public String getInvoicePath(Long id){
        return "files/invoices/" + "buy"+id +".pdf";
    }

    @Override
    public String getCancellationInvoicePath(Long id){
        return "files/invoices/" + "canceled"+id +".pdf";
    }

    @Override
    public Ticket find(long id) throws NotFoundException {
        try{
            return ticketRepository.findById(id).get();
        } catch (Exception e){
            LOGGER.error(e.getMessage());
            throw new NotFoundException("Ticket does not exist");
        }
    }

    @Override
    public Page<Ticket> filter(TicketSearchDto ticketSearchDto) {

        int entriesPerPage = 4;
        Pageable pageable = PageRequest.of(ticketSearchDto.getPage(),entriesPerPage);
        Page<Ticket> returnPage;

        if ( ticketSearchDto.getStatus().equals( BOUGHT ) ) {

            returnPage = ticketRepository.filter(
                ticketSearchDto.getUserId(),
                ticketSearchDto.getStatus(),
                REFUNDED,
                pageable
            );

        } else {

            returnPage = ticketRepository.filterWithOneStatus(
                ticketSearchDto.getUserId(),
                ticketSearchDto.getStatus(),
                pageable
            );

        }

        return returnPage;
    }

    @Override
    public Ticket getOrCreateQueuedTicket(Long id) throws NotFoundException, ValidationException {
        LOGGER.info("Find or create queued ticket for performance: " + id);

        // We reset outdated tickets
        resetOutdatedTickets();

        User foundUser = getAuthenticatedUser();

        Optional<Performance> foundPerformance = performanceRepository.findById(id);
        if (foundPerformance.isEmpty()) {
            throw new NotFoundException("The requested performance does not exist");
        }

        if(foundPerformance.get().getDatetime().isBefore(LocalDateTime.now())) {
            throw new ValidationException("The requested performance is in the past");
        }

        Ticket foundQueuedTicket = null;
        for (Ticket ticket : foundPerformance.get().getTickets()) {
            if (ticket.getUser().getId().equals(foundUser.getId()) && ticket.getStatus() == Status.IN_QUE) {
                foundQueuedTicket = ticket;
                break;
            }
        }

        if (foundQueuedTicket != null) {
            return foundQueuedTicket;
        }

        Ticket newQueuedTicket = Ticket.builder().performance(foundPerformance.get()).status(Status.IN_QUE).user(foundUser).seats(new ArrayList<>()).createDate(LocalDateTime.now()).build();
        return ticketRepository.save(newQueuedTicket);
    }

    private void resetOutdatedTickets() {
        List<Ticket> outdatedTicket = ticketRepository.findAll().stream().filter(t -> t.getStatus() == Status.IN_QUE && t.getCreateDate().until(LocalDateTime.now(), ChronoUnit.MINUTES) > 15).collect(Collectors.toList());
        outdatedTicket.forEach(t -> ticketRepository.deleteById(t.getId()));
        ticketRepository.flush();
    }

    @Override
    public void cancelReservedTicket(Long id) throws NotFoundException, NotAllowedException, ValidationException {
        LOGGER.info("Cancel reservation for ticket with id: " + id);
        Ticket foundTicket = getUserTicket(id);
        if(foundTicket.getStatus() != Status.RESERVED) {
            throw new ValidationException("The ticket is not reserved and therefore the reservation cannot be canceled");
        }
        foundTicket.setStatus(CANCELLED);
        ticketRepository.save(foundTicket);
    }

    @Override
    public void generateCancellationInvoice(Long id) {
        Optional<Ticket> ticketOpt = ticketRepository.findById(id);
        if(ticketOpt.isEmpty()){
            throw new NotFoundException("Ticket not found");
        }
        Ticket ticket = ticketOpt.get();
        try{
            String path = "files/invoices/" + "canceled"+id +".pdf";
            CancellationInvoicePdfCreator.createPdf(path, ticket);
        } catch (Exception e){
            LOGGER.error("Error generating invoice for id "+id+":"+e.getMessage());
        }
    }

    @Override
    public void cancelBoughtTicket(Long id) throws NotFoundException, NotAllowedException, ValidationException {
        LOGGER.info("Cancel bought ticket with id: " + id);
        Ticket foundTicket = getUserTicket(id);
        if(foundTicket.getStatus() != Status.BOUGHT) {
            throw new ValidationException("The ticket is not bought and therefore cannot be canceled");
        }
        foundTicket.setStatus(Status.REFUNDED);
        ticketRepository.save(foundTicket);
        generateCancellationInvoice(id);
    }

    @Override
    public Ticket getCheckoutInfo(Long id) throws NotFoundException, NotAllowedException, ValidationException {
        LOGGER.info("Get checkout info for ticket with id: " + id);
        Ticket foundTicket = getUserTicket(id);
        if(foundTicket.getPerformance().getDatetime().isBefore(LocalDateTime.now())) {
            throw new ValidationException("The ticket is no longer valid because the performance is in the past");
        }
        return foundTicket;
    }

    @Override
    public List<Seat> getSelectedSeats(Long id) throws NotFoundException, ValidationException, NotAllowedException {
        LOGGER.info("Find selected seats for ticket with id: " + id);
        Ticket foundTicket = getUserTicket(id);
        if(foundTicket.getPerformance().getDatetime().isBefore(LocalDateTime.now())) {
            throw new ValidationException("The ticket is no longer valid because the performance is in the past");
        }
        return foundTicket.getSeats();
    }

    @Override
    public void buyTicket(Long id) throws NotFoundException, NotAllowedException, ValidationException {
        LOGGER.info("Buy ticket with id: " + id);
        Ticket foundTicket = getUserTicket(id);
        if (foundTicket.getStatus() != Status.IN_QUE && foundTicket.getStatus() != Status.RESERVED) {
            throw new ValidationException("The ticket is not in queue nor is it reserved and therefore it cannot be bought");
        }
        if (foundTicket.getSeats().isEmpty()) {
            throw new ValidationException("A ticket must at least contain 1 seat");
        }
        if(foundTicket.getPerformance().getDatetime().isBefore(LocalDateTime.now())) {
            throw new ValidationException("The ticket is no longer valid because the performance is in the past");
        }
        foundTicket.setStatus(Status.BOUGHT);
        ticketRepository.save(foundTicket);
    }

    @Override
    public void reserveTicket(Long id) throws NotFoundException, NotAllowedException, ValidationException {
        LOGGER.info("Reserve ticket with id: " + id);
        Ticket foundTicket = getUserTicket(id);
        if (foundTicket.getStatus() != Status.IN_QUE) {
            throw new ValidationException("The ticket is not in que and therefore cannot be reserved");
        }
        if (foundTicket.getSeats().isEmpty()) {
            throw new ValidationException("A ticket must at least contain 1 seat");
        }
        if(foundTicket.getPerformance().getDatetime().isBefore(LocalDateTime.now())) {
            throw new ValidationException("The ticket is no longer valid because the performance is in the past");
        }
        foundTicket.setStatus(Status.RESERVED);
        foundTicket.setRes_nr(String.valueOf(UUID.randomUUID().hashCode()));
        ticketRepository.save(foundTicket);
    }

    @Override
    public void unselectSeat(SeatUnselectDto seatUnselectDto) throws NotFoundException, ValidationException, NotAllowedException {
        LOGGER.info("Unselect seat for info: " + seatUnselectDto);

        Ticket ticket = getUserTicket(seatUnselectDto.getTicketId());
        if (ticket.getStatus() != Status.IN_QUE && ticket.getStatus() != Status.RESERVED) {
            throw new ValidationException("Seats can only be unselected for queued and reserved tickets");
        }

        Performance ticketPerformance = ticket.getPerformance();

        if(ticketPerformance.getDatetime().isBefore(LocalDateTime.now())) {
            throw new ValidationException("The ticket is no longer valid because the performance is in the past");
        }

        if (seatUnselectDto.getType() == Type.SEATS) {
            Integer requestedRow = seatUnselectDto.getRow();
            Integer requestedColumn = seatUnselectDto.getCol();
            if (requestedRow == null || requestedColumn == null) {
                throw new ValidationException("When using type SEATS a row and a column must be given.");
            }

            Optional<Area> foundArea = ticketPerformance.getHall().getAreas().stream().filter(a -> a.getStartRow() <= requestedRow && a.getEndRow() >= requestedRow && a.getStartCol() <= requestedColumn && a.getEndCol() >= requestedColumn).findFirst();
            if (foundArea.isEmpty()) {
                throw new ValidationException("Found seat doesn't belong to any area");
            }

            if (foundArea.get().getType() == Type.SECTION) {
                throw new ValidationException("The given seat is inside a area and therefore cannot be selected individually");
            }

            Optional<Seat> seat = ticket.getSeats().stream().filter(s -> s.getSeatCol() == requestedColumn && s.getSeatRow() == requestedRow).findFirst();
            if (seat.isEmpty()) {
                throw new ValidationException("The given seat is not selected");
            }

            seatRepository.delete(seat.get());
            return;
        } else if (seatUnselectDto.getType() == Type.SECTION) {
            Optional<Area> foundArea = ticketPerformance.getHall().getAreas().stream().filter(a -> a.getId().equals(seatUnselectDto.getAreaId())).findFirst();
            if (foundArea.isEmpty()) {
                throw new ValidationException("The given areaId does not belong to a valid area");
            }

            if (foundArea.get().getType() == Type.SEATS) {
                throw new ValidationException("The given area contains individual seats which can only be unselected individually");
            }

            List<Seat> foundSeats = ticket.getSeats().stream().filter(s -> s.getArea().getId().equals(seatUnselectDto.getAreaId())).collect(Collectors.toList());
            seatRepository.deleteAll(foundSeats);
            return;
        }
        throw new ValidationException("Given type does not exist");
    }

    @Override
    public void selectSeat(SeatSelectDto seatSelectDto) throws SeatNotAvailableException, NotFoundException, NotAllowedException {
        LOGGER.info("Select seat for info: " + seatSelectDto);

        Ticket ticket = getUserTicket(seatSelectDto.getTicketId());
        if (ticket.getStatus() != Status.IN_QUE && ticket.getStatus() != Status.RESERVED) {
            throw new ValidationException("Seats can only be selected for queued and reserved tickets");
        }

        Performance ticketPerformance = ticket.getPerformance();

        if(ticketPerformance.getDatetime().isBefore(LocalDateTime.now())) {
            throw new ValidationException("The ticket is no longer valid because the performance is in the past");
        }

        if (seatSelectDto.getType() == Type.SEATS) {
            Integer requestedRow = seatSelectDto.getRow();
            Integer requestedColumn = seatSelectDto.getCol();
            if (requestedRow == null || requestedColumn == null) {
                throw new ValidationException("When using type SEATS a row and a column must be given.");
            }

            if (ticketPerformance.getTickets().stream().filter(t -> t.getStatus() != CANCELLED && t.getStatus() != Status.REFUNDED).flatMap(t -> t.getSeats().stream()).anyMatch(s -> s.getSeatCol() == requestedColumn && s.getSeatRow() == requestedRow)) {
                throw new SeatNotAvailableException("Seat is already taken");
            }

            Optional<Area> foundArea = ticketPerformance.getHall().getAreas().stream().filter(a -> a.getStartRow() <= requestedRow && a.getEndRow() >= requestedRow && a.getStartCol() <= requestedColumn && a.getEndCol() >= requestedColumn).findFirst();
            if (foundArea.isEmpty()) {
                throw new ValidationException("Found seat doesn't belong to any area");
            }

            if (foundArea.get().getType() != Type.SEATS) {
                throw new ValidationException("The given seat is inside a area and therefore cannot be selected individually");
            }

            Seat newSeat = Seat.builder().seatCol(requestedColumn).seatRow(requestedRow).area(foundArea.get()).ticket(ticket).build();
            seatRepository.save(newSeat);
            return;
        } else if (seatSelectDto.getType() == Type.SECTION) {
            Integer seatCount = seatSelectDto.getSeatCount();
            if (seatCount == null) {
                throw new ValidationException("When using type SECTION a seatCount must be given.");
            }

            Optional<Area> foundArea = ticketPerformance.getHall().getAreas().stream().filter(a -> a.getId().equals(seatSelectDto.getAreaId())).findFirst();
            if (foundArea.isEmpty()) {
                throw new ValidationException("The given areaId does not belong to a valid area");
            }

            if (foundArea.get().getType() != Type.SECTION) {
                throw new ValidationException("The given area contains individual seats which can only be selected individually");
            }

            List<Seat> chosenSeats = getFreeSeats(ticket, foundArea.get(), seatCount);
            if (chosenSeats.size() < seatCount) {
                throw new NotEnoughSeatsException("The chosen area does not have enough seats");
            }

            seatRepository.saveAll(chosenSeats);
            return;
        }
        throw new ValidationException("Given type does not exist");
    }

    private List<Seat> getFreeSeats(Ticket ticket, Area area, int count) {
        List<Seat> chosenSeats = new ArrayList<>();
        for (int cRow = area.getStartRow(); cRow <= area.getEndRow(); cRow++) {
            for (int cCol = area.getStartCol(); cCol <= area.getEndCol(); cCol++) {
                boolean taken = false;
                for (Seat seat : area.getSeats()) {
                    if (seat.getSeatRow() == cRow && seat.getSeatCol() == cCol && seat.getTicket().getStatus() != CANCELLED && seat.getTicket().getStatus() != Status.REFUNDED) {
                        taken = true;
                        break;
                    }
                }
                if (!taken) {
                    chosenSeats.add(Seat.builder().area(area).seatCol(cCol).seatRow(cRow).ticket(ticket).build());
                    if (chosenSeats.size() == count) {
                        return chosenSeats;
                    }
                }
            }
        }
        return chosenSeats;
    }

    private Ticket getUserTicket(Long ticketId) throws NotFoundException, NotAllowedException {
        User foundUser = getAuthenticatedUser();
        Optional<Ticket> ticket = ticketRepository.findById(ticketId);
        if (ticket.isEmpty()) {
            throw new NotFoundException("TicketId does not belong to a valid ticket");
        }
        if (!ticket.get().getUser().getId().equals(foundUser.getId())) {
            throw new NotAllowedException("This ticket does not belong to the current user");
        }
        return ticket.get();
    }

    private User getAuthenticatedUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication instanceof AnonymousAuthenticationToken) {
            throw new ServerErrorException("No authenticated user was found");
        }

        User foundUser = userRepository.findUserByEmailIgnoreCase(authentication.getName());
        if (foundUser == null) {
            throw new ServerErrorException("Current user does not exist in database");
        }

        return foundUser;
    }

}
