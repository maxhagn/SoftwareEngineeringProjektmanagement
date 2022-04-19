package at.ac.tuwien.sepm.groupphase.backend.integrationtest;

import at.ac.tuwien.sepm.groupphase.backend.config.properties.SecurityProperties;
import at.ac.tuwien.sepm.groupphase.backend.datageneration.DeleteDb;
import at.ac.tuwien.sepm.groupphase.backend.datageneration.RealDataService;
import at.ac.tuwien.sepm.groupphase.backend.datageneration.TestDataService;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.performance.PerformanceDetailDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.seat.SeatDisplayDto;
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

import java.util.Collections;
import java.util.List;

import static at.ac.tuwien.sepm.groupphase.backend.helper.TestData.PERFORMANCE_URI;
import static org.junit.Assert.assertEquals;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@ActiveProfiles("deleteDb,generateTestData,test")
@AutoConfigureMockMvc()
public class PerformanceEndpointTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private SecurityProperties securityProperties;

    @Autowired
    private JwtTokenizer jwtTokenizer;

    @Autowired
    private DeleteDb deleteDb;

    @Autowired
    private TestDataService realDataService;

    @BeforeEach
    public void beforeEach() {
        deleteDb.deleteData();
        realDataService.initData();
    }

    @Test
    public void whenGivenInvalidId_findPerformance_ShouldReturn404() throws Exception {
        MvcResult mvcResult = this.mockMvc
            .perform(
                get(PERFORMANCE_URI + "-1")
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
    public void whenGivenValidId_findPerformance_ShouldReturnCorrectMappedPerformance() throws Exception {
        MvcResult mvcResult = this.mockMvc
            .perform(
                get(PERFORMANCE_URI + "201")
                    .contentType(MediaType.APPLICATION_JSON)
                    .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken("user@user", Collections.singletonList("ROLE_USER")))
            )
            .andDo(print())
            .andReturn();
        MockHttpServletResponse response = mvcResult.getResponse();

        PerformanceDetailDto resDto = objectMapper.readValue(response.getContentAsByteArray(), PerformanceDetailDto.class);

        assertAll(
            () -> assertEquals(HttpStatus.OK.value(), response.getStatus()),
            () -> assertEquals( (120), resDto.getDuration()),
            () -> assertEquals("Sall 1", resDto.getHallName()),
            () -> assertEquals("Boom!", resDto.getTitle()),
            () -> assertEquals("Babenberger Passage", resDto.getLocationName())
        );
    }

    @Test
    public void whenGivenInvalidId_getTakenSeats_ShouldReturn404() throws Exception {
        MvcResult mvcResult = this.mockMvc
            .perform(
                get(PERFORMANCE_URI + "-1" + "/taken")
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
    public void whenGivenValidId_getTakenSeats_ShouldReturnTakenSeats() throws Exception {
        MvcResult mvcResult = this.mockMvc
            .perform(
                get(PERFORMANCE_URI + "201" + "/taken")
                    .contentType(MediaType.APPLICATION_JSON)
                    .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken("user@user", Collections.singletonList("ROLE_USER")))
            )
            .andDo(print())
            .andReturn();

        MockHttpServletResponse response = mvcResult.getResponse();

        List<SeatDisplayDto> resDto = objectMapper.readValue(response.getContentAsByteArray(), new TypeReference<>() {
        });

        assertAll(
            () -> assertEquals(HttpStatus.OK.value(), response.getStatus()),
            () -> assertEquals(7, resDto.size()),
            () -> assertEquals(9, resDto.get(0).getCol()),
            () -> assertEquals(12, resDto.get(0).getRow()),
            () -> assertEquals("expensive", resDto.get(0).getAreaName()),
            () -> assertEquals(15.0f, resDto.get(0).getPrice(), 0.0)
        );
    }

}