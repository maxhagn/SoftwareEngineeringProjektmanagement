package at.ac.tuwien.sepm.groupphase.backend.integrationtest;

import at.ac.tuwien.sepm.groupphase.backend.config.properties.SecurityProperties;
import at.ac.tuwien.sepm.groupphase.backend.datageneration.DeleteDb;
import at.ac.tuwien.sepm.groupphase.backend.datageneration.TestDataService;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.events.EventOutputDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.mapper.LocationMapper;
import at.ac.tuwien.sepm.groupphase.backend.entity.Event;
import at.ac.tuwien.sepm.groupphase.backend.repository.EventRepository;
import at.ac.tuwien.sepm.groupphase.backend.security.JwtTokenizer;
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

import java.util.Collections;

import static at.ac.tuwien.sepm.groupphase.backend.helper.TestData.EVENT_URI;
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
public class EventEndpointTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private SecurityProperties securityProperties;

    @Autowired
    private JwtTokenizer jwtTokenizer;

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private DeleteDb deleteDb;

    @Autowired
    private TestDataService testDataService;

    @Autowired
    private LocationMapper locationMapper;

    @BeforeEach
    public void beforeEach() {
        deleteDb.deleteData();
        testDataService.initData();
    }

    private final String USER_MAIL = "user1@user.com";

    /* Get single Ticket by id */

    @Test
    public void whenGivenInvalidEventId_get_ShouldReturn404() throws Exception {
        MvcResult mvcResult = this.mockMvc
            .perform(
                get(EVENT_URI + "-1")
                    .contentType(MediaType.APPLICATION_JSON)
                    .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(USER_MAIL, Collections.singletonList("ROLE_USER")))
            )
            .andDo(print())
            .andReturn();

        MockHttpServletResponse response = mvcResult.getResponse();

        assertAll(
            () -> assertEquals(HttpStatus.NOT_FOUND.value(), response.getStatus())
        );
    }

    @Test
    public void whenGivenValidEventId_get_ShouldReturnCorrectTicket() throws Exception {
        MvcResult mvcResult = this.mockMvc
            .perform(
                get(EVENT_URI + "201")
                    .contentType(MediaType.APPLICATION_JSON)
                    .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(USER_MAIL, Collections.singletonList("ROLE_USER")))
            )
            .andDo(print())
            .andReturn();

        MockHttpServletResponse response = mvcResult.getResponse();

        EventOutputDto resDto = objectMapper.readValue(response.getContentAsByteArray(), EventOutputDto.class);

        Event e = eventRepository.findById(201L).get();

        assertAll(
            () -> assertEquals(HttpStatus.OK.value(), response.getStatus()),
            () -> assertEquals(e.getDuration(), resDto.getDuration()),
            () -> assertEquals(e.getArtist().getFirstname() + " " + e.getArtist().getSurname(), resDto.getArtist()),
            () -> assertEquals(e.getDescription(), resDto.getDescription()),
            () -> assertEquals(e.getTitle(), resDto.getTitle()),
            () -> assertEquals(e.getCategory(), resDto.getCategory()),
            () -> assertEquals(locationMapper.entityToWithAreCodeDto(e.getLocation()), resDto.getLocation()),
            () -> assertEquals(e.getPerformances().size(), resDto.getPerformances().size())
        );
    }

}