package at.ac.tuwien.sepm.groupphase.backend.integrationtest;

import at.ac.tuwien.sepm.groupphase.backend.config.properties.SecurityProperties;
import at.ac.tuwien.sepm.groupphase.backend.datageneration.DeleteDb;
import at.ac.tuwien.sepm.groupphase.backend.datageneration.TestDataService;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.seat.SeatDisplayDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.seat.SeatSelectDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.seat.SeatUnselectDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.ticket.TicketCheckoutDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.ticket.TicketQueueDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.ticket.TicketSearchDto;
import at.ac.tuwien.sepm.groupphase.backend.entity.enums.Status;
import at.ac.tuwien.sepm.groupphase.backend.entity.enums.Type;
import at.ac.tuwien.sepm.groupphase.backend.repository.TicketRepository;
import at.ac.tuwien.sepm.groupphase.backend.security.JwtTokenizer;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static at.ac.tuwien.sepm.groupphase.backend.helper.TestData.TICKET_URI;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@ActiveProfiles("deleteDb,generateTestData,test")
@AutoConfigureMockMvc()
public class TicketEndpointTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private SecurityProperties securityProperties;

    @Autowired
    private JwtTokenizer jwtTokenizer;

    @Autowired
    private TicketRepository ticketRepository;

    @Autowired
    private DeleteDb deleteDb;

    @Autowired
    private TestDataService testDataService;

    @BeforeEach
    public void beforeEach() {
        deleteDb.deleteData();
        testDataService.initData();
    }

    private final String USER_MAIL = "user1@user.com";

    @Test
    public void whenUserInDbAndHasTicket_loadTicket_success() throws Exception {
        MvcResult mvcResult = this.mockMvc
            .perform(
                get(TICKET_URI + "200")
                    .contentType(MediaType.APPLICATION_JSON)
                    .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(USER_MAIL, Collections.singletonList("ROLE_USER")))
            )
            .andDo(print())
            .andReturn();

        MockHttpServletResponse response = mvcResult.getResponse();

        assertAll(
            () -> assertEquals(HttpStatus.valueOf(200).value(), response.getStatus())
        );
    }

    @Test
    public void whenUserInDbAndDoesNotHaveTicket_loadTicket_forbidden() throws Exception {
        MvcResult mvcResult = this.mockMvc
            .perform(
                get(TICKET_URI + "500000")
                    .contentType(MediaType.APPLICATION_JSON)
                    .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(USER_MAIL, Collections.singletonList("ROLE_USER")))
            )
            .andDo(print())
            .andReturn();

        MockHttpServletResponse response = mvcResult.getResponse();

        assertAll(
            () -> assertEquals(HttpStatus.FORBIDDEN.value(), response.getStatus())
        );
    }

    /* Buy Ticket */

    @Test
    public void whenGivenInvalidTicketId_buy_ShouldReturn404() throws Exception {
        MvcResult mvcResult = this.mockMvc
            .perform(
                patch(TICKET_URI + "-1/buy")
                    .contentType(MediaType.APPLICATION_JSON)
                    .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken("user@user", Collections.singletonList("ROLE_USER")))
            )
            .andDo(print())
            .andReturn();

        MockHttpServletResponse response = mvcResult.getResponse();

        assertAll(
            () -> assertEquals(HttpStatus.NOT_FOUND.value(), response.getStatus())
        );
    }

    @Test
    public void whenGivenTicketInPast_buy_ShouldReturn422() throws Exception {
        MvcResult mvcResult = this.mockMvc
            .perform(
                patch(TICKET_URI + "204/buy")
                    .contentType(MediaType.APPLICATION_JSON)
                    .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(USER_MAIL, Collections.singletonList("ROLE_USER")))
            )
            .andDo(print())
            .andReturn();

        MockHttpServletResponse response = mvcResult.getResponse();

        assertAll(
            () -> assertEquals(HttpStatus.UNPROCESSABLE_ENTITY.value(), response.getStatus())
        );
    }

    @Test
    public void whenGivenTicketNotOwned_buy_ShouldReturn403() throws Exception {
        MvcResult mvcResult = this.mockMvc
            .perform(
                patch(TICKET_URI + "200/buy")
                    .contentType(MediaType.APPLICATION_JSON)
                    .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken("admin@user", Arrays.asList("ROLE_ADMIN", "ROLE_USER")))
            )
            .andDo(print())
            .andReturn();

        MockHttpServletResponse response = mvcResult.getResponse();

        assertAll(
            () -> assertEquals(HttpStatus.FORBIDDEN.value(), response.getStatus())
        );
    }

    @Test
    public void whenGivenTicketNotInQueue_buy_ShouldReturn422() throws Exception {
        MvcResult mvcResult = this.mockMvc
            .perform(
                patch(TICKET_URI + "200/buy")
                    .contentType(MediaType.APPLICATION_JSON)
                    .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(USER_MAIL, Collections.singletonList("ROLE_USER")))
            )
            .andDo(print())
            .andReturn();

        MockHttpServletResponse response = mvcResult.getResponse();

        assertAll(
            () -> assertEquals(HttpStatus.UNPROCESSABLE_ENTITY.value(), response.getStatus())
        );
    }

    @Test
    public void whenGivenTicketNoSeats_buy_ShouldReturn422() throws Exception {
        MvcResult mvcResult = this.mockMvc
            .perform(
                patch(TICKET_URI + "202/buy")
                    .contentType(MediaType.APPLICATION_JSON)
                    .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(USER_MAIL, Collections.singletonList("ROLE_USER")))
            )
            .andDo(print())
            .andReturn();

        MockHttpServletResponse response = mvcResult.getResponse();

        assertAll(
            () -> assertEquals(HttpStatus.UNPROCESSABLE_ENTITY.value(), response.getStatus())
        );
    }

    @Test
    public void whenGivenTicket_buy_ShouldSetStatusBought() throws Exception {
        MvcResult mvcResult = this.mockMvc
            .perform(
                patch(TICKET_URI + "201/buy")
                    .contentType(MediaType.APPLICATION_JSON)
                    .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(USER_MAIL, Collections.singletonList("ROLE_USER")))
            )
            .andDo(print())
            .andReturn();

        MockHttpServletResponse response = mvcResult.getResponse();

        assertAll(
            () -> assertEquals(HttpStatus.OK.value(), response.getStatus()),
            () -> assertEquals(Status.BOUGHT, ticketRepository.findById(201L).get().getStatus())
        );
    }

    /* Reserve ticket */

    @Test
    public void whenGivenInvalidTicketId_reserve_ShouldReturn404() throws Exception {
        MvcResult mvcResult = this.mockMvc
            .perform(
                patch(TICKET_URI + "-1/reserve")
                    .contentType(MediaType.APPLICATION_JSON)
                    .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken("user@user", Collections.singletonList("ROLE_USER")))
            )
            .andDo(print())
            .andReturn();

        MockHttpServletResponse response = mvcResult.getResponse();

        assertAll(
            () -> assertEquals(HttpStatus.NOT_FOUND.value(), response.getStatus())
        );
    }

    @Test
    public void whenGivenTicketInPast_reserve_ShouldReturn422() throws Exception {
        MvcResult mvcResult = this.mockMvc
            .perform(
                patch(TICKET_URI + "204/reserve")
                    .contentType(MediaType.APPLICATION_JSON)
                    .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(USER_MAIL, Collections.singletonList("ROLE_USER")))
            )
            .andDo(print())
            .andReturn();

        MockHttpServletResponse response = mvcResult.getResponse();

        assertAll(
            () -> assertEquals(HttpStatus.UNPROCESSABLE_ENTITY.value(), response.getStatus())
        );
    }

    @Test
    public void whenGivenTicketNotInQueue_reserve_ShouldReturn422() throws Exception {
        MvcResult mvcResult = this.mockMvc
            .perform(
                patch(TICKET_URI + "200/reserve")
                    .contentType(MediaType.APPLICATION_JSON)
                    .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(USER_MAIL, Collections.singletonList("ROLE_USER")))
            )
            .andDo(print())
            .andReturn();

        MockHttpServletResponse response = mvcResult.getResponse();

        assertAll(
            () -> assertEquals(HttpStatus.UNPROCESSABLE_ENTITY.value(), response.getStatus())
        );
    }

    @Test
    public void whenGivenTicketNotOwned_reserve_ShouldReturn403() throws Exception {
        MvcResult mvcResult = this.mockMvc
            .perform(
                patch(TICKET_URI + "200/reserve")
                    .contentType(MediaType.APPLICATION_JSON)
                    .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken("admin@user", Arrays.asList("ROLE_ADMIN", "ROLE_USER")))
            )
            .andDo(print())
            .andReturn();

        MockHttpServletResponse response = mvcResult.getResponse();

        assertAll(
            () -> assertEquals(HttpStatus.FORBIDDEN.value(), response.getStatus())
        );
    }

    @Test
    public void whenGivenTicketNoSeats_reserve_ShouldReturn422() throws Exception {
        MvcResult mvcResult = this.mockMvc
            .perform(
                patch(TICKET_URI + "202/reserve")
                    .contentType(MediaType.APPLICATION_JSON)
                    .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(USER_MAIL, Collections.singletonList("ROLE_USER")))
            )
            .andDo(print())
            .andReturn();

        MockHttpServletResponse response = mvcResult.getResponse();

        assertAll(
            () -> assertEquals(HttpStatus.UNPROCESSABLE_ENTITY.value(), response.getStatus())
        );
    }

    @Test
    public void whenGivenTicket_reserve_ShouldSetRandomResNum() throws Exception {
        MvcResult mvcResult = this.mockMvc
            .perform(
                patch(TICKET_URI + "201/reserve")
                    .contentType(MediaType.APPLICATION_JSON)
                    .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(USER_MAIL, Arrays.asList("ROLE_ADMIN", "ROLE_USER")))
            )
            .andDo(print())
            .andReturn();

        MockHttpServletResponse response = mvcResult.getResponse();

        assertAll(
            () -> assertEquals(HttpStatus.OK.value(), response.getStatus()),
            () -> assertNotNull(ticketRepository.findById(201L).get().getRes_nr()),
            () -> assertNotEquals(0, ticketRepository.findById(201L).get().getRes_nr().length()),
            () -> assertEquals(Status.RESERVED, ticketRepository.findById(201L).get().getStatus())
        );
    }

    /* Get or create queued ticket */

    @Test
    public void whenGivenInvalidPerformanceId_getOrCreateQueuedTicket_ShouldReturn404() throws Exception {
        MvcResult mvcResult = this.mockMvc
            .perform(
                post(TICKET_URI + "-1/queue")
                    .contentType(MediaType.APPLICATION_JSON)
                    .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken("user@user", Collections.singletonList("ROLE_USER")))
            )
            .andDo(print())
            .andReturn();

        MockHttpServletResponse response = mvcResult.getResponse();

        assertAll(
            () -> assertEquals(HttpStatus.NOT_FOUND.value(), response.getStatus())
        );
    }

    @Test
    public void whenGivenValidPerformanceId_getOrCreateQueuedTicket_ShouldReturnNewTicket() throws Exception {
        MvcResult mvcResult = this.mockMvc
            .perform(
                post(TICKET_URI + "203/queue")
                    .contentType(MediaType.APPLICATION_JSON)
                    .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(USER_MAIL, Collections.singletonList("ROLE_USER")))
            )
            .andDo(print())
            .andReturn();

        MockHttpServletResponse response = mvcResult.getResponse();

        TicketQueueDto resDto = objectMapper.readValue(response.getContentAsByteArray(), TicketQueueDto.class);

        assertAll(
            () -> assertEquals(HttpStatus.OK.value(), response.getStatus()),
            () -> assertEquals(0, resDto.getSeats().size())
        );
    }

    @Test
    public void whenGivenValidPerformanceId_getOrCreateQueuedTicket_ShouldReturnQueuedTicket() throws Exception {
        MvcResult mvcResult = this.mockMvc
            .perform(
                post(TICKET_URI + "201/queue")
                    .contentType(MediaType.APPLICATION_JSON)
                    .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(USER_MAIL, Collections.singletonList("ROLE_USER")))
            )
            .andDo(print())
            .andReturn();

        MockHttpServletResponse response = mvcResult.getResponse();

        TicketQueueDto resDto = objectMapper.readValue(response.getContentAsByteArray(), TicketQueueDto.class);

        assertAll(
            () -> assertEquals(HttpStatus.OK.value(), response.getStatus()),
            () -> assertEquals(2, resDto.getSeats().size()),
            () -> assertEquals(12, resDto.getSeats().get(0).getRow()),
            () -> assertEquals(5, resDto.getSeats().get(0).getCol())
        );
    }

    @Test
    public void whenGivenPerformanceInPast_getOrCreateQueuedTicket_ShouldReturn422() throws Exception {
        MvcResult mvcResult = this.mockMvc
            .perform(
                post(TICKET_URI + "12/queue")
                    .contentType(MediaType.APPLICATION_JSON)
                    .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(USER_MAIL, Collections.singletonList("ROLE_USER")))
            )
            .andDo(print())
            .andReturn();

        MockHttpServletResponse response = mvcResult.getResponse();

        assertAll(
            () -> assertEquals(HttpStatus.UNPROCESSABLE_ENTITY.value(), response.getStatus())
        );
    }

    /* Select seat */

    @Test
    public void whenGivenInvalidTicketId_select_ShouldReturn404() throws Exception {
        SeatSelectDto ticketSelectDto = new SeatSelectDto(-1L, Type.SEATS, null, null, null, null);

        MvcResult mvcResult = this.mockMvc
            .perform(
                post(TICKET_URI + "select")
                    .content(objectMapper.writeValueAsBytes(ticketSelectDto))
                    .contentType(MediaType.APPLICATION_JSON)
                    .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken("user@user", Collections.singletonList("ROLE_USER")))
            )
            .andDo(print())
            .andReturn();

        MockHttpServletResponse response = mvcResult.getResponse();

        assertAll(
            () -> assertEquals(HttpStatus.NOT_FOUND.value(), response.getStatus())
        );
    }

    @Test
    public void whenGivenTicketInPast_select_ShouldReturn422() throws Exception {
        SeatSelectDto ticketSelectDto = new SeatSelectDto(204L, Type.SEATS, 1, 1, null, null);

        MvcResult mvcResult = this.mockMvc
            .perform(
                post(TICKET_URI + "select")
                    .content(objectMapper.writeValueAsBytes(ticketSelectDto))
                    .contentType(MediaType.APPLICATION_JSON)
                    .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(USER_MAIL, Collections.singletonList("ROLE_USER")))
            )
            .andDo(print())
            .andReturn();

        MockHttpServletResponse response = mvcResult.getResponse();

        assertAll(
            () -> assertEquals(HttpStatus.UNPROCESSABLE_ENTITY.value(), response.getStatus())
        );
    }

    @Test
    public void whenGivenTicketMissingRowParam_select_ShouldReturn422() throws Exception {
        SeatSelectDto ticketSelectDto = new SeatSelectDto(202L, Type.SEATS, null, 1, null, null);

        MvcResult mvcResult = this.mockMvc
            .perform(
                post(TICKET_URI + "select")
                    .content(objectMapper.writeValueAsBytes(ticketSelectDto))
                    .contentType(MediaType.APPLICATION_JSON)
                    .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(USER_MAIL, Collections.singletonList("ROLE_USER")))
            )
            .andDo(print())
            .andReturn();

        MockHttpServletResponse response = mvcResult.getResponse();

        assertAll(
            () -> assertEquals(HttpStatus.UNPROCESSABLE_ENTITY.value(), response.getStatus())
        );
    }

    @Test
    public void whenGivenTicketSeatTaken_select_ShouldReturn422() throws Exception {
        SeatSelectDto ticketSelectDto = new SeatSelectDto(201L, Type.SEATS, 12, 5, null, null);

        MvcResult mvcResult = this.mockMvc
            .perform(
                post(TICKET_URI + "select")
                    .content(objectMapper.writeValueAsBytes(ticketSelectDto))
                    .contentType(MediaType.APPLICATION_JSON)
                    .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(USER_MAIL, Collections.singletonList("ROLE_USER")))
            )
            .andDo(print())
            .andReturn();

        MockHttpServletResponse response = mvcResult.getResponse();

        assertAll(
            () -> assertEquals(HttpStatus.UNPROCESSABLE_ENTITY.value(), response.getStatus())
        );
    }

    @Test
    public void whenGivenTicketInvalidSectionTypeSection_select_ShouldReturn422() throws Exception {
        SeatSelectDto ticketSelectDto = new SeatSelectDto(205L, Type.SEATS, 22, 3, null, null);

        MvcResult mvcResult = this.mockMvc
            .perform(
                post(TICKET_URI + "select")
                    .content(objectMapper.writeValueAsBytes(ticketSelectDto))
                    .contentType(MediaType.APPLICATION_JSON)
                    .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(USER_MAIL, Collections.singletonList("ROLE_USER")))
            )
            .andDo(print())
            .andReturn();

        MockHttpServletResponse response = mvcResult.getResponse();

        assertAll(
            () -> assertEquals(HttpStatus.UNPROCESSABLE_ENTITY.value(), response.getStatus())
        );
    }

    @Test
    public void whenGivenTicketMissingSeatCount_select_ShouldReturn422() throws Exception {
        SeatSelectDto ticketSelectDto = new SeatSelectDto(205L, Type.SECTION, null, null, 250L, null);

        MvcResult mvcResult = this.mockMvc
            .perform(
                post(TICKET_URI + "select")
                    .content(objectMapper.writeValueAsBytes(ticketSelectDto))
                    .contentType(MediaType.APPLICATION_JSON)
                    .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(USER_MAIL, Collections.singletonList("ROLE_USER")))
            )
            .andDo(print())
            .andReturn();

        MockHttpServletResponse response = mvcResult.getResponse();

        assertAll(
            () -> assertEquals(HttpStatus.UNPROCESSABLE_ENTITY.value(), response.getStatus())
        );
    }

    @Test
    public void whenGivenTicketInvalidSectionTypeSeats_select_ShouldReturn422() throws Exception {
        SeatSelectDto ticketSelectDto = new SeatSelectDto(205L, Type.SECTION, null, null, 202L, 1);

        MvcResult mvcResult = this.mockMvc
            .perform(
                post(TICKET_URI + "select")
                    .content(objectMapper.writeValueAsBytes(ticketSelectDto))
                    .contentType(MediaType.APPLICATION_JSON)
                    .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(USER_MAIL, Collections.singletonList("ROLE_USER")))
            )
            .andDo(print())
            .andReturn();

        MockHttpServletResponse response = mvcResult.getResponse();

        assertAll(
            () -> assertEquals(HttpStatus.UNPROCESSABLE_ENTITY.value(), response.getStatus())
        );
    }

    @Test
    public void whenGivenTicketTooMuchSeats_select_ShouldReturn422() throws Exception {
        SeatSelectDto ticketSelectDto = new SeatSelectDto(205L, Type.SECTION, null, null, 250L, 1000);

        MvcResult mvcResult = this.mockMvc
            .perform(
                post(TICKET_URI + "select")
                    .content(objectMapper.writeValueAsBytes(ticketSelectDto))
                    .contentType(MediaType.APPLICATION_JSON)
                    .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(USER_MAIL, Collections.singletonList("ROLE_USER")))
            )
            .andDo(print())
            .andReturn();

        MockHttpServletResponse response = mvcResult.getResponse();

        assertAll(
            () -> assertEquals(HttpStatus.UNPROCESSABLE_ENTITY.value(), response.getStatus())
        );
    }

    @Test
    public void whenGivenTicketInvalidType_select_ShouldReturn422() throws Exception {
        SeatSelectDto ticketSelectDto = new SeatSelectDto(205L, Type.SCREEN, null, null, null, null);

        MvcResult mvcResult = this.mockMvc
            .perform(
                post(TICKET_URI + "select")
                    .content(objectMapper.writeValueAsBytes(ticketSelectDto))
                    .contentType(MediaType.APPLICATION_JSON)
                    .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(USER_MAIL, Collections.singletonList("ROLE_USER")))
            )
            .andDo(print())
            .andReturn();

        MockHttpServletResponse response = mvcResult.getResponse();

        assertAll(
            () -> assertEquals(HttpStatus.UNPROCESSABLE_ENTITY.value(), response.getStatus())
        );
    }

    @Test
    public void whenGivenBoughtTicket_select_ShouldReturn422() throws Exception {
        SeatSelectDto ticketSelectDto = new SeatSelectDto(200L, Type.SEATS, 1, 1, null, null);

        MvcResult mvcResult = this.mockMvc
            .perform(
                post(TICKET_URI + "select")
                    .content(objectMapper.writeValueAsBytes(ticketSelectDto))
                    .contentType(MediaType.APPLICATION_JSON)
                    .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(USER_MAIL, Collections.singletonList("ROLE_USER")))
            )
            .andDo(print())
            .andReturn();

        MockHttpServletResponse response = mvcResult.getResponse();

        assertAll(
            () -> assertEquals(HttpStatus.UNPROCESSABLE_ENTITY.value(), response.getStatus())
        );
    }

    @Test
    public void whenGivenInvalidColumn_select_ShouldReturn422() throws Exception {
        SeatSelectDto ticketSelectDto = new SeatSelectDto(201L, Type.SEATS, 1, 200, null, null);

        MvcResult mvcResult = this.mockMvc
            .perform(
                post(TICKET_URI + "select")
                    .content(objectMapper.writeValueAsBytes(ticketSelectDto))
                    .contentType(MediaType.APPLICATION_JSON)
                    .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(USER_MAIL, Collections.singletonList("ROLE_USER")))
            )
            .andDo(print())
            .andReturn();

        MockHttpServletResponse response = mvcResult.getResponse();

        assertAll(
            () -> assertEquals(HttpStatus.UNPROCESSABLE_ENTITY.value(), response.getStatus())
        );
    }

    @Test
    public void whenGivenInvalidRow_select_ShouldReturn422() throws Exception {
        SeatSelectDto ticketSelectDto = new SeatSelectDto(201L, Type.SEATS, 200, 1, null, null);

        MvcResult mvcResult = this.mockMvc
            .perform(
                post(TICKET_URI + "select")
                    .content(objectMapper.writeValueAsBytes(ticketSelectDto))
                    .contentType(MediaType.APPLICATION_JSON)
                    .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(USER_MAIL, Collections.singletonList("ROLE_USER")))
            )
            .andDo(print())
            .andReturn();

        MockHttpServletResponse response = mvcResult.getResponse();

        assertAll(
            () -> assertEquals(HttpStatus.UNPROCESSABLE_ENTITY.value(), response.getStatus())
        );
    }

    @Test
    public void whenGivenInvalidType_select_ShouldReturn422() throws Exception {
        SeatSelectDto ticketSelectDto = new SeatSelectDto(201L, Type.SCREEN, 1, 1, null, null);

        MvcResult mvcResult = this.mockMvc
            .perform(
                post(TICKET_URI + "select")
                    .content(objectMapper.writeValueAsBytes(ticketSelectDto))
                    .contentType(MediaType.APPLICATION_JSON)
                    .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(USER_MAIL, Collections.singletonList("ROLE_USER")))
            )
            .andDo(print())
            .andReturn();

        MockHttpServletResponse response = mvcResult.getResponse();

        assertAll(
            () -> assertEquals(HttpStatus.UNPROCESSABLE_ENTITY.value(), response.getStatus())
        );
    }

    @Test
    public void whenGivenTakenSeats_select_ShouldReturn422() throws Exception {
        SeatSelectDto ticketSelectDto = new SeatSelectDto(201L, Type.SEATS, 9, 12, null, null);

        MvcResult mvcResult = this.mockMvc
            .perform(
                post(TICKET_URI + "select")
                    .content(objectMapper.writeValueAsBytes(ticketSelectDto))
                    .contentType(MediaType.APPLICATION_JSON)
                    .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(USER_MAIL, Collections.singletonList("ROLE_USER")))
            )
            .andDo(print())
            .andReturn();

        MockHttpServletResponse response = mvcResult.getResponse();

        assertAll(
            () -> assertEquals(HttpStatus.UNPROCESSABLE_ENTITY.value(), response.getStatus())
        );
    }

    @Test
    public void whenGivenSectionAlthoughSeats_select_ShouldReturn422() throws Exception {
        SeatSelectDto ticketSelectDto = new SeatSelectDto(201L, Type.SECTION, null, null, 1L, 1);

        MvcResult mvcResult = this.mockMvc
            .perform(
                post(TICKET_URI + "select")
                    .content(objectMapper.writeValueAsBytes(ticketSelectDto))
                    .contentType(MediaType.APPLICATION_JSON)
                    .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(USER_MAIL, Collections.singletonList("ROLE_USER")))
            )
            .andDo(print())
            .andReturn();

        MockHttpServletResponse response = mvcResult.getResponse();

        assertAll(
            () -> assertEquals(HttpStatus.UNPROCESSABLE_ENTITY.value(), response.getStatus())
        );
    }

    @Test
    public void whenGivenValidData_select_ShouldAddSeat() throws Exception {
        SeatSelectDto ticketSelectDto = new SeatSelectDto(201L, Type.SEATS, 3, 1, null, null);

        MvcResult mvcResult = this.mockMvc
            .perform(
                post(TICKET_URI + "select")
                    .content(objectMapper.writeValueAsBytes(ticketSelectDto))
                    .contentType(MediaType.APPLICATION_JSON)
                    .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(USER_MAIL, Collections.singletonList("ROLE_USER")))
            )
            .andDo(print())
            .andReturn();

        MockHttpServletResponse response = mvcResult.getResponse();

        assertAll(
            () -> assertEquals(HttpStatus.OK.value(), response.getStatus()),
            () -> assertEquals(1, ticketRepository.findById(201L).get().getSeats().stream().filter(s -> s.getSeatRow() == 3 && s.getSeatCol() == 1).count())
        );
    }

    /* Unselect seat */

    @Test
    public void whenGivenInvalidTicketId_unselect_ShouldReturn404() throws Exception {
        SeatUnselectDto ticketSelectDto = new SeatUnselectDto(-1L, Type.SEATS, null, null, null);

        MvcResult mvcResult = this.mockMvc
            .perform(
                post(TICKET_URI + "unselect")
                    .content(objectMapper.writeValueAsBytes(ticketSelectDto))
                    .contentType(MediaType.APPLICATION_JSON)
                    .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken("user@user", Collections.singletonList("ROLE_USER")))
            )
            .andDo(print())
            .andReturn();

        MockHttpServletResponse response = mvcResult.getResponse();

        assertAll(
            () -> assertEquals(HttpStatus.NOT_FOUND.value(), response.getStatus())
        );
    }

    @Test
    public void whenGivenTicketInPast_unselect_ShouldReturn422() throws Exception {
        SeatUnselectDto ticketSelectDto = new SeatUnselectDto(204L, Type.SEATS, null, null, null);

        MvcResult mvcResult = this.mockMvc
            .perform(
                post(TICKET_URI + "unselect")
                    .content(objectMapper.writeValueAsBytes(ticketSelectDto))
                    .contentType(MediaType.APPLICATION_JSON)
                    .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(USER_MAIL, Collections.singletonList("ROLE_USER")))
            )
            .andDo(print())
            .andReturn();

        MockHttpServletResponse response = mvcResult.getResponse();

        assertAll(
            () -> assertEquals(HttpStatus.UNPROCESSABLE_ENTITY.value(), response.getStatus())
        );
    }

    @Test
    public void whenGivenTicketMissingRowParam_unselect_ShouldReturn422() throws Exception {
        SeatUnselectDto ticketSelectDto = new SeatUnselectDto(202L, Type.SEATS, null, 1, 202L);

        MvcResult mvcResult = this.mockMvc
            .perform(
                post(TICKET_URI + "unselect")
                    .content(objectMapper.writeValueAsBytes(ticketSelectDto))
                    .contentType(MediaType.APPLICATION_JSON)
                    .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(USER_MAIL, Collections.singletonList("ROLE_USER")))
            )
            .andDo(print())
            .andReturn();

        MockHttpServletResponse response = mvcResult.getResponse();

        assertAll(
            () -> assertEquals(HttpStatus.UNPROCESSABLE_ENTITY.value(), response.getStatus())
        );
    }

    @Test
    public void whenGivenTicketWrongRowParam_unselect_ShouldReturn422() throws Exception {
        SeatUnselectDto ticketSelectDto = new SeatUnselectDto(202L, Type.SEATS, 110, 1, 202L);

        MvcResult mvcResult = this.mockMvc
            .perform(
                post(TICKET_URI + "unselect")
                    .content(objectMapper.writeValueAsBytes(ticketSelectDto))
                    .contentType(MediaType.APPLICATION_JSON)
                    .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(USER_MAIL, Collections.singletonList("ROLE_USER")))
            )
            .andDo(print())
            .andReturn();

        MockHttpServletResponse response = mvcResult.getResponse();

        assertAll(
            () -> assertEquals(HttpStatus.UNPROCESSABLE_ENTITY.value(), response.getStatus())
        );
    }

    @Test
    public void whenGivenTicketInvalidSectionTypeSeats_unselect_ShouldReturn422() throws Exception {
        SeatUnselectDto ticketSelectDto = new SeatUnselectDto(205L, Type.SEATS, 22, 1, 250L);

        MvcResult mvcResult = this.mockMvc
            .perform(
                post(TICKET_URI + "unselect")
                    .content(objectMapper.writeValueAsBytes(ticketSelectDto))
                    .contentType(MediaType.APPLICATION_JSON)
                    .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(USER_MAIL, Collections.singletonList("ROLE_USER")))
            )
            .andDo(print())
            .andReturn();

        MockHttpServletResponse response = mvcResult.getResponse();

        assertAll(
            () -> assertEquals(HttpStatus.UNPROCESSABLE_ENTITY.value(), response.getStatus())
        );
    }

    @Test
    public void whenGivenTicket_unselect_ShouldReturnOk() throws Exception {
        SeatUnselectDto ticketSelectDto = new SeatUnselectDto(205L, Type.SECTION, null, null, 250L);

        MvcResult mvcResult = this.mockMvc
            .perform(
                post(TICKET_URI + "unselect")
                    .content(objectMapper.writeValueAsBytes(ticketSelectDto))
                    .contentType(MediaType.APPLICATION_JSON)
                    .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(USER_MAIL, Collections.singletonList("ROLE_USER")))
            )
            .andDo(print())
            .andReturn();

        MockHttpServletResponse response = mvcResult.getResponse();

        assertAll(
            () -> assertEquals(HttpStatus.OK.value(), response.getStatus()),
            () -> assertEquals(0, ticketRepository.findById(205L).get().getSeats().size())
        );
    }

    @Test
    public void whenGivenTicketInvalidSection_unselect_ShouldReturn422() throws Exception {
        SeatUnselectDto ticketSelectDto = new SeatUnselectDto(205L, Type.SECTION, null, null, 204L);

        MvcResult mvcResult = this.mockMvc
            .perform(
                post(TICKET_URI + "unselect")
                    .content(objectMapper.writeValueAsBytes(ticketSelectDto))
                    .contentType(MediaType.APPLICATION_JSON)
                    .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(USER_MAIL, Collections.singletonList("ROLE_USER")))
            )
            .andDo(print())
            .andReturn();

        MockHttpServletResponse response = mvcResult.getResponse();

        assertAll(
            () -> assertEquals(HttpStatus.UNPROCESSABLE_ENTITY.value(), response.getStatus())
        );
    }

    @Test
    public void whenGivenTicketInvalidSectionTypeSection_unselect_ShouldReturn422() throws Exception {
        SeatUnselectDto ticketSelectDto = new SeatUnselectDto(202L, Type.SECTION, null, null, 202L);

        MvcResult mvcResult = this.mockMvc
            .perform(
                post(TICKET_URI + "unselect")
                    .content(objectMapper.writeValueAsBytes(ticketSelectDto))
                    .contentType(MediaType.APPLICATION_JSON)
                    .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(USER_MAIL, Collections.singletonList("ROLE_USER")))
            )
            .andDo(print())
            .andReturn();

        MockHttpServletResponse response = mvcResult.getResponse();

        assertAll(
            () -> assertEquals(HttpStatus.UNPROCESSABLE_ENTITY.value(), response.getStatus())
        );
    }

    @Test
    public void whenGivenNotSelectedSeat_unselect_ShouldReturn422() throws Exception {
        SeatUnselectDto ticketSelectDto = new SeatUnselectDto(201L, Type.SEATS, 12, 7, null);

        MvcResult mvcResult = this.mockMvc
            .perform(
                post(TICKET_URI + "unselect")
                    .content(objectMapper.writeValueAsBytes(ticketSelectDto))
                    .contentType(MediaType.APPLICATION_JSON)
                    .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(USER_MAIL, Collections.singletonList("ROLE_USER")))
            )
            .andDo(print())
            .andReturn();

        MockHttpServletResponse response = mvcResult.getResponse();

        assertAll(
            () -> assertEquals(HttpStatus.UNPROCESSABLE_ENTITY.value(), response.getStatus())
        );
    }

    @Test
    public void whenGivenTicketInvalidType_unselect_ShouldReturn422() throws Exception {
        SeatUnselectDto ticketSelectDto = new SeatUnselectDto(205L, Type.SCREEN, null, null, 204L);

        MvcResult mvcResult = this.mockMvc
            .perform(
                post(TICKET_URI + "unselect")
                    .content(objectMapper.writeValueAsBytes(ticketSelectDto))
                    .contentType(MediaType.APPLICATION_JSON)
                    .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(USER_MAIL, Collections.singletonList("ROLE_USER")))
            )
            .andDo(print())
            .andReturn();

        MockHttpServletResponse response = mvcResult.getResponse();

        assertAll(
            () -> assertEquals(HttpStatus.UNPROCESSABLE_ENTITY.value(), response.getStatus())
        );
    }

    @Test
    public void whenGivenBoughtTicket_unselect_ShouldReturn422() throws Exception {
        SeatSelectDto ticketSelectDto = new SeatSelectDto(200L, Type.SEATS, 1, 1, null, null);

        MvcResult mvcResult = this.mockMvc
            .perform(
                post(TICKET_URI + "unselect")
                    .content(objectMapper.writeValueAsBytes(ticketSelectDto))
                    .contentType(MediaType.APPLICATION_JSON)
                    .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(USER_MAIL, Collections.singletonList("ROLE_USER")))
            )
            .andDo(print())
            .andReturn();

        MockHttpServletResponse response = mvcResult.getResponse();

        assertAll(
            () -> assertEquals(HttpStatus.UNPROCESSABLE_ENTITY.value(), response.getStatus())
        );
    }

    @Test
    public void whenGivenTicketNotOwned_unselect_ShouldReturn403() throws Exception {
        SeatSelectDto ticketSelectDto = new SeatSelectDto(200L, Type.SEATS, 1, 1, null, null);

        MvcResult mvcResult = this.mockMvc
            .perform(
                post(TICKET_URI + "unselect")
                    .content(objectMapper.writeValueAsBytes(ticketSelectDto))
                    .contentType(MediaType.APPLICATION_JSON)
                    .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken("admin@user", Collections.singletonList("ROLE_USER")))
            )
            .andDo(print())
            .andReturn();

        MockHttpServletResponse response = mvcResult.getResponse();

        assertAll(
            () -> assertEquals(HttpStatus.FORBIDDEN.value(), response.getStatus())
        );
    }

    /* Get ticket checkout info */

    @Test
    public void whenGivenInvalidTicketId_checkout_ShouldReturn404() throws Exception {
        MvcResult mvcResult = this.mockMvc
            .perform(
                get(TICKET_URI + "-1/checkout")
                    .contentType(MediaType.APPLICATION_JSON)
                    .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken("user@user", Collections.singletonList("ROLE_USER")))
            )
            .andDo(print())
            .andReturn();

        MockHttpServletResponse response = mvcResult.getResponse();

        assertAll(
            () -> assertEquals(HttpStatus.NOT_FOUND.value(), response.getStatus())
        );
    }

    @Test
    public void whenGivenNotOwnedTicket_checkout_ShouldReturn403() throws Exception {
        MvcResult mvcResult = this.mockMvc
            .perform(
                get(TICKET_URI + "200/checkout")
                    .contentType(MediaType.APPLICATION_JSON)
                    .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken("admin@user", Arrays.asList("ROLE_ADMIN", "ROLE_USER")))
            )
            .andDo(print())
            .andReturn();

        MockHttpServletResponse response = mvcResult.getResponse();

        assertAll(
            () -> assertEquals(HttpStatus.FORBIDDEN.value(), response.getStatus())
        );
    }

    @Test
    public void whenGivenOwnedTicket_checkout_ShouldReturnTicketInfo() throws Exception {
        MvcResult mvcResult = this.mockMvc
            .perform(
                get(TICKET_URI + "200/checkout")
                    .contentType(MediaType.APPLICATION_JSON)
                    .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(USER_MAIL, Arrays.asList("ROLE_ADMIN", "ROLE_USER")))
            )
            .andDo(print())
            .andReturn();

        MockHttpServletResponse response = mvcResult.getResponse();

        TicketCheckoutDto resDto = objectMapper.readValue(response.getContentAsByteArray(), TicketCheckoutDto.class);

        assertAll(
            () -> assertEquals(HttpStatus.OK.value(), response.getStatus()),
            () -> assertEquals("Sall 1", resDto.getHallName()),
            () -> assertEquals("Boom!", resDto.getTitle()),
            () -> assertEquals(3, resDto.getSeats().size())
        );
    }

    @Test
    public void whenGivenOwnedTicketInPast_checkout_ShouldReturn422() throws Exception {
        MvcResult mvcResult = this.mockMvc
            .perform(
                get(TICKET_URI + "204/checkout")
                    .contentType(MediaType.APPLICATION_JSON)
                    .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(USER_MAIL, Collections.singletonList("ROLE_USER")))
            )
            .andDo(print())
            .andReturn();

        MockHttpServletResponse response = mvcResult.getResponse();

        assertAll(
            () -> assertEquals(HttpStatus.UNPROCESSABLE_ENTITY.value(), response.getStatus())
        );
    }

    /* Get selected seats */

    @Test
    public void whenGivenInvalidTicketId_selectedSeats_ShouldReturn404() throws Exception {
        MvcResult mvcResult = this.mockMvc
            .perform(
                get(TICKET_URI + "-1/seats")
                    .contentType(MediaType.APPLICATION_JSON)
                    .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken("user@user", Collections.singletonList("ROLE_USER")))
            )
            .andDo(print())
            .andReturn();

        MockHttpServletResponse response = mvcResult.getResponse();

        assertAll(
            () -> assertEquals(HttpStatus.NOT_FOUND.value(), response.getStatus())
        );
    }

    @Test
    public void whenGivenTicketInPast_selectedSeats_ShouldReturn422() throws Exception {
        MvcResult mvcResult = this.mockMvc
            .perform(
                get(TICKET_URI + "204/seats")
                    .contentType(MediaType.APPLICATION_JSON)
                    .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(USER_MAIL, Collections.singletonList("ROLE_USER")))
            )
            .andDo(print())
            .andReturn();

        MockHttpServletResponse response = mvcResult.getResponse();

        assertAll(
            () -> assertEquals(HttpStatus.UNPROCESSABLE_ENTITY.value(), response.getStatus())
        );
    }

    @Test
    public void whenGivenNotOwnedTicket_selectedSeats_ShouldReturn403() throws Exception {
        MvcResult mvcResult = this.mockMvc
            .perform(
                get(TICKET_URI + "200/seats")
                    .contentType(MediaType.APPLICATION_JSON)
                    .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken("admin@user", Arrays.asList("ROLE_ADMIN", "ROLE_USER")))
            )
            .andDo(print())
            .andReturn();

        MockHttpServletResponse response = mvcResult.getResponse();

        assertAll(
            () -> assertEquals(HttpStatus.FORBIDDEN.value(), response.getStatus())
        );
    }

    @Test
    public void whenGivenOwnedTicket_selectedSeats_ShouldReturnSelectedSeats() throws Exception {
        MvcResult mvcResult = this.mockMvc
            .perform(
                get(TICKET_URI + "200/seats")
                    .contentType(MediaType.APPLICATION_JSON)
                    .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(USER_MAIL, Arrays.asList("ROLE_ADMIN", "ROLE_USER")))
            )
            .andDo(print())
            .andReturn();

        MockHttpServletResponse response = mvcResult.getResponse();

        List<SeatDisplayDto> resDto = objectMapper.readValue(response.getContentAsByteArray(), new TypeReference<List<SeatDisplayDto>>() {
        });

        assertAll(
            () -> assertEquals(HttpStatus.OK.value(), response.getStatus()),
            () -> assertEquals(3, resDto.size()),
            () -> assertEquals(9, resDto.get(0).getCol()),
            () -> assertEquals(12, resDto.get(0).getRow())
        );
    }

    @Test
    public void whenGivenInvalidTicketId_cancelReserved_ShouldReturn404() throws Exception {
        MvcResult mvcResult = this.mockMvc
            .perform(
                post(TICKET_URI + "-1/cancelReserved")
                    .contentType(MediaType.APPLICATION_JSON)
                    .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken("user@user", Collections.singletonList("ROLE_USER")))
            )
            .andDo(print())
            .andReturn();

        MockHttpServletResponse response = mvcResult.getResponse();

        assertAll(
            () -> assertEquals(HttpStatus.NOT_FOUND.value(), response.getStatus())
        );
    }

    @Test
    public void whenGivenNotOwnedTicket_cancelReserved_ShouldReturn403() throws Exception {
        MvcResult mvcResult = this.mockMvc
            .perform(
                post(TICKET_URI + "203/cancelReserved")
                    .contentType(MediaType.APPLICATION_JSON)
                    .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken("admin@user", Arrays.asList("ROLE_ADMIN", "ROLE_USER")))
            )
            .andDo(print())
            .andReturn();

        MockHttpServletResponse response = mvcResult.getResponse();

        assertAll(
            () -> assertEquals(HttpStatus.FORBIDDEN.value(), response.getStatus())
        );
    }

    @Test
    public void whenGivenNotReservedTicket_cancelReserved_ShouldReturn422() throws Exception {
        MvcResult mvcResult = this.mockMvc
            .perform(
                post(TICKET_URI + "200/cancelReserved")
                    .contentType(MediaType.APPLICATION_JSON)
                    .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(USER_MAIL, Collections.singletonList("ROLE_USER")))
            )
            .andDo(print())
            .andReturn();

        MockHttpServletResponse response = mvcResult.getResponse();

        assertAll(
            () -> assertEquals(HttpStatus.UNPROCESSABLE_ENTITY.value(), response.getStatus())
        );
    }

    @Test
    public void whenGivenReservedTicket_cancelReserved_ShouldSetTicketCancelled() throws Exception {
        MvcResult mvcResult = this.mockMvc
            .perform(
                post(TICKET_URI + "203/cancelReserved")
                    .contentType(MediaType.APPLICATION_JSON)
                    .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(USER_MAIL, Collections.singletonList("ROLE_USER")))
            )
            .andDo(print())
            .andReturn();

        MockHttpServletResponse response = mvcResult.getResponse();

        assertAll(
            () -> assertEquals(HttpStatus.OK.value(), response.getStatus()),
            () -> assertEquals(Status.CANCELLED, ticketRepository.findById(203L).get().getStatus())
        );
    }

    @Test
    public void whenGivenInvalidTicketId_cancelBought_ShouldReturn404() throws Exception {
        MvcResult mvcResult = this.mockMvc
            .perform(
                post(TICKET_URI + "-1/cancelBought")
                    .contentType(MediaType.APPLICATION_JSON)
                    .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken("user1@user.com", Collections.singletonList("ROLE_USER")))
            )
            .andDo(print())
            .andReturn();

        MockHttpServletResponse response = mvcResult.getResponse();

        assertAll(
            () -> assertEquals(HttpStatus.NOT_FOUND.value(), response.getStatus())
        );
    }

    @Test
    public void whenGivenNotOwnedTicket_cancelBought_ShouldReturn403() throws Exception {
        MvcResult mvcResult = this.mockMvc
            .perform(
                post(TICKET_URI + "200/cancelBought")
                    .contentType(MediaType.APPLICATION_JSON)
                    .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken("admin@user", Arrays.asList("ROLE_ADMIN", "ROLE_USER")))
            )
            .andDo(print())
            .andReturn();

        MockHttpServletResponse response = mvcResult.getResponse();

        assertAll(
            () -> assertEquals(HttpStatus.FORBIDDEN.value(), response.getStatus())
        );
    }

    @Test
    public void whenGivenNotBoughtTicket_cancelBought_ShouldReturn422() throws Exception {
        MvcResult mvcResult = this.mockMvc
            .perform(
                post(TICKET_URI + "203/cancelBought")
                    .contentType(MediaType.APPLICATION_JSON)
                    .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(USER_MAIL, Collections.singletonList("ROLE_USER")))
            )
            .andDo(print())
            .andReturn();

        MockHttpServletResponse response = mvcResult.getResponse();

        assertAll(
            () -> assertEquals(HttpStatus.UNPROCESSABLE_ENTITY.value(), response.getStatus())
        );
    }

    @Test
    public void whenGivenBoughtTicket_cancelBought_ShouldSetTicketRefunded() throws Exception {
        MvcResult mvcResult = this.mockMvc
            .perform(
                post(TICKET_URI + "200/cancelBought")
                    .contentType(MediaType.APPLICATION_JSON)
                    .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(USER_MAIL, Collections.singletonList("ROLE_USER")))
            )
            .andDo(print())
            .andReturn();

        MockHttpServletResponse response = mvcResult.getResponse();

        assertAll(
            () -> assertEquals(HttpStatus.OK.value(), response.getStatus()),
            () -> assertEquals(Status.REFUNDED, ticketRepository.findById(200L).get().getStatus())
        );
    }
}