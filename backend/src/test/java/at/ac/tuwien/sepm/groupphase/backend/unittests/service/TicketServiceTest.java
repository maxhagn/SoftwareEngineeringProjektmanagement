package at.ac.tuwien.sepm.groupphase.backend.unittests.service;

import at.ac.tuwien.sepm.groupphase.backend.entity.*;
import at.ac.tuwien.sepm.groupphase.backend.entity.enums.Status;
import at.ac.tuwien.sepm.groupphase.backend.exception.NotAllowedException;
import at.ac.tuwien.sepm.groupphase.backend.exception.NotFoundException;
import at.ac.tuwien.sepm.groupphase.backend.exception.ValidationException;
import at.ac.tuwien.sepm.groupphase.backend.helper.MockUser;
import at.ac.tuwien.sepm.groupphase.backend.helper.UserProvider;
import at.ac.tuwien.sepm.groupphase.backend.repository.PerformanceRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.TicketRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.UserRepository;
import at.ac.tuwien.sepm.groupphase.backend.service.TicketService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@ActiveProfiles("test")
public class TicketServiceTest {

    @MockBean
    private PerformanceRepository performanceRepository;

    @MockBean
    private TicketRepository ticketRepository;

    @Autowired
    private TicketService ticketService;

    @MockBean
    private UserRepository userRepository;

    @Autowired
    private UserProvider userProvider;

    private static final String USER_MAIL = "normal@email.com";
    private static final String ADMIN_MAIL = "admin@email.com";

    @BeforeEach
    public void beforeEach() {

        // Users

        User admin = userProvider.getAdmin();
        mockUser(admin);

        User normal = userProvider.getNormal();
        mockUser(normal);

        // Seats

        Seat newSeat = Seat.builder().id(1L).area(Area.builder().priceCategory(PriceCategory.builder().price(1f).build()).build()).build();

        // Performances + Ticket

        Ticket queuedTicket = Ticket.builder().id(1L).status(Status.IN_QUE).user(normal).build();
        mockTicket(queuedTicket);

        Performance defaultPerformance = Performance.builder().id(1L).min_price(1f).tickets(List.of(queuedTicket)).datetime(LocalDateTime.now().plusDays(1)).build();
        mockPerformance(defaultPerformance);

        Performance pastPerformance = Performance.builder().id(2L).tickets(List.of()).datetime(LocalDateTime.now().minusDays(1)).build();
        mockPerformance(pastPerformance);

        // Tickets

        Ticket newTicket = Ticket.builder().id(2L).performance(defaultPerformance).status(Status.IN_QUE).user(admin).seats(new ArrayList<>()).createDate(LocalDateTime.now()).build();
        when(ticketRepository.save(Mockito.any(Ticket.class))).thenReturn(newTicket);

        Ticket reservedTicket = Ticket.builder().id(3L).performance(defaultPerformance).status(Status.RESERVED).user(normal).seats(new ArrayList<>()).createDate(LocalDateTime.now()).build();
        mockTicket(reservedTicket);

        Ticket boughtTicket = Ticket.builder().id(4L).performance(defaultPerformance).status(Status.BOUGHT).user(normal).seats(new ArrayList<>()).createDate(LocalDateTime.now()).build();
        mockTicket(boughtTicket);

        Ticket performancePastTicket = Ticket.builder().id(5L).performance(pastPerformance).status(Status.IN_QUE).user(normal).seats(new ArrayList<>()).createDate(LocalDateTime.now()).build();
        mockTicket(performancePastTicket);

        Ticket queuedTicketWithSeat = Ticket.builder().id(6L).performance(defaultPerformance).status(Status.IN_QUE).user(normal).seats(List.of(newSeat)).createDate(LocalDateTime.now()).build();
        mockTicket(queuedTicketWithSeat);

        Ticket queuedPastPerformanceTicketWithSeat = Ticket.builder().id(7L).performance(pastPerformance).status(Status.IN_QUE).user(normal).seats(List.of(newSeat)).createDate(LocalDateTime.now()).build();
        mockTicket(queuedPastPerformanceTicketWithSeat);

    }

    private void mockUser(User user) {
        when(userRepository.findUserByEmailIgnoreCase(user.getEmail())).thenReturn(user);
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
    }

    private void mockTicket(Ticket ticket) {
        when(ticketRepository.findById(ticket.getId())).thenReturn(Optional.of(ticket));
    }

    private void mockPerformance(Performance performance) {
        when(performanceRepository.findById(performance.getId())).thenReturn(Optional.of(performance));
    }

    @Test
    @MockUser(username = USER_MAIL, authorities = {"ROLE_USER"})
    public void givenQueuedTicket_getOrCreateQueuedTicket_returnQueuedTicket() {
        Ticket t = ticketService.getOrCreateQueuedTicket(1L);
        assertAll(
            () -> assertEquals(Status.IN_QUE, t.getStatus()),
            () -> assertEquals(1L, t.getId())
        );
    }

    @Test
    @MockUser(username = ADMIN_MAIL, authorities = {"ROLE_USER", "ROLE_ADMIN"})
    public void givenNotValidPerformance_getOrCreateQueuedTicket_throwNotFound() {
        assertAll(
            () -> assertThrows(NotFoundException.class, () -> ticketService.getOrCreateQueuedTicket(-1L))
        );
    }

    @Test
    @MockUser(username = ADMIN_MAIL, authorities = {"ROLE_USER", "ROLE_ADMIN"})
    public void givenNotQueuedTicket_getOrCreateQueuedTicket_returnNewTicket() {
        Ticket t = ticketService.getOrCreateQueuedTicket(1L);
        assertAll(
            () -> assertEquals(Status.IN_QUE, t.getStatus()),
            () -> assertEquals(2L, t.getId())
        );
    }

    @Test
    @MockUser(username = USER_MAIL, authorities = {"ROLE_USER"})
    public void givenInvalidTicket_cancelReservedTicket_throwNotFound() {
        assertAll(
            () -> assertThrows(NotFoundException.class, () -> ticketService.cancelReservedTicket(-1L))
        );
    }

    @Test
    @MockUser(username = ADMIN_MAIL, authorities = {"ROLE_USER", "ROLE_ADMIN"})
    public void givenNotOwnedTicket_cancelReservedTicket_throwNotAllowed() {
        assertAll(
            () -> assertThrows(NotAllowedException.class, () -> ticketService.cancelReservedTicket(3L))
        );
    }

    @Test
    @MockUser(username = USER_MAIL, authorities = {"ROLE_USER"})
    public void givenNotReservedTicket_cancelReservedTicket_throwValidation() {
        assertAll(
            () -> assertThrows(ValidationException.class, () -> ticketService.cancelReservedTicket(1L))
        );
    }

    @Test
    @MockUser(username = USER_MAIL, authorities = {"ROLE_USER"})
    public void givenReservedTicket_cancelReservedTicket_SetCancelled() {
        ticketService.cancelReservedTicket(3L);
        assertAll(
            () -> assertEquals(Status.CANCELLED, ticketRepository.findById(3L).get().getStatus())
        );
    }

    @Test
    @MockUser(username = USER_MAIL, authorities = {"ROLE_USER"})
    public void givenInvalidTicket_cancelBoughtTicket_throwNotFound() {
        assertAll(
            () -> assertThrows(NotFoundException.class, () -> ticketService.cancelBoughtTicket(-1L))
        );
    }

    @Test
    @MockUser(username = ADMIN_MAIL, authorities = {"ROLE_USER", "ROLE_ADMIN"})
    public void givenNotOwnedTicket_cancelBoughtTicket_throwNotAllowed() {
        assertAll(
            () -> assertThrows(NotAllowedException.class, () -> ticketService.cancelBoughtTicket(4L))
        );
    }

    @Test
    @MockUser(username = USER_MAIL, authorities = {"ROLE_USER"})
    public void givenNotBoughtTicket_cancelBoughtTicket_throwValidation() {
        assertAll(
            () -> assertThrows(ValidationException.class, () -> ticketService.cancelBoughtTicket(1L))
        );
    }

    @Test
    @MockUser(username = USER_MAIL, authorities = {"ROLE_USER"})
    public void givenBoughtTicket_cancelBoughtTicket_SetCancelled() {
        ticketService.cancelBoughtTicket(4L);
        assertAll(
            () -> assertEquals(Status.REFUNDED, ticketRepository.findById(4L).get().getStatus())
        );
    }

    @Test
    @MockUser(username = USER_MAIL, authorities = {"ROLE_USER"})
    public void givenInvalidTicket_getSelectedSeats_throwNotFound() {
        assertAll(
            () -> assertThrows(NotFoundException.class, () -> ticketService.getSelectedSeats(-1L))
        );
    }

    @Test
    @MockUser(username = ADMIN_MAIL, authorities = {"ROLE_USER", "ROLE_ADMIN"})
    public void givenNotOwnerTicket_getSelectedSeats_throwNotAllowed() {
        assertAll(
            () -> assertThrows(NotAllowedException.class, () -> ticketService.getSelectedSeats(5L))
        );
    }

    @Test
    @MockUser(username = USER_MAIL, authorities = {"ROLE_USER"})
    public void givenPastPerformance_getSelectedSeats_throwValidation() {
        assertAll(
            () -> assertThrows(ValidationException.class, () -> ticketService.getSelectedSeats(5L))
        );
    }

    @Test
    @MockUser(username = USER_MAIL, authorities = {"ROLE_USER"})
    public void givenTicket_getSelectedSeats_returnSeats() {
        List<Seat> seats = ticketService.getSelectedSeats(6L);
        assertAll(
            () -> assertEquals(1, seats.size()),
            () -> assertEquals(1L, seats.get(0).getId())
        );
    }

    @Test
    @MockUser(username = USER_MAIL, authorities = {"ROLE_USER"})
    public void givenBoughtTicket_buyTicket_throwValidation() {
        assertAll(
            () -> assertThrows(ValidationException.class, () -> ticketService.buyTicket(4L))
        );
    }

    @Test
    @MockUser(username = USER_MAIL, authorities = {"ROLE_USER"})
    public void givenNoSeatsTicket_buyTicket_throwValidation() {
        assertAll(
            () -> assertThrows(ValidationException.class, () -> ticketService.buyTicket(5L))
        );
    }

    @Test
    @MockUser(username = USER_MAIL, authorities = {"ROLE_USER"})
    public void givenPastPerformance_buyTicket_throwValidation() {
        assertAll(
            () -> assertThrows(ValidationException.class, () -> ticketService.buyTicket(7L))
        );
    }

    @Test
    @MockUser(username = USER_MAIL, authorities = {"ROLE_USER"})
    public void givenTicket_buyTicket_setBought() {
        ticketService.buyTicket(6L);
        assertAll(
            () -> assertEquals(Status.BOUGHT, ticketRepository.findById(6L).get().getStatus())
        );
    }

    @Test
    @MockUser(username = USER_MAIL, authorities = {"ROLE_USER"})
    public void givenReservedTicket_reserveTicket_throwValidation() {
        assertAll(
            () -> assertThrows(ValidationException.class, () -> ticketService.reserveTicket(3L))
        );
    }

    @Test
    @MockUser(username = USER_MAIL, authorities = {"ROLE_USER"})
    public void givenNoSeatsTicket_reserveTicket_throwValidation() {
        assertAll(
            () -> assertThrows(ValidationException.class, () -> ticketService.reserveTicket(5L))
        );
    }

    @Test
    @MockUser(username = USER_MAIL, authorities = {"ROLE_USER"})
    public void givenPastPerformance_reserveTicket_throwValidation() {
        assertAll(
            () -> assertThrows(ValidationException.class, () -> ticketService.reserveTicket(7L))
        );
    }

    @Test
    @MockUser(username = USER_MAIL, authorities = {"ROLE_USER"})
    public void givenTicket_reserveTicket_setReserved() {
        ticketService.reserveTicket(6L);
        assertAll(
            () -> assertEquals(Status.RESERVED, ticketRepository.findById(6L).get().getStatus())
        );
    }

}
